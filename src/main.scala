package functionalafrica

import scala.util.Try


object ParamMap:
  def unapply(args: List[String]): Option[ParamMap] =
    def parse(xs: List[String], params: ParamMap = ParamMap(Map())): ParamMap = xs match
      case Nil =>
        params
      case s"-$arg" :: tail =>
        val values = tail.takeWhile(!_.startsWith("-"))
        val rest = tail.drop(values.length)
        parse(rest, params.copy(map = params.map.updated(arg, values)))
      case args =>
        parse(args.dropWhile(!_.startsWith("-")))
    
    Some(parse(args))

case class ParamMap(map: Map[String, List[String]])

object Day extends Parameter[Date]("d", Date)
object Rainfall extends Parameter[Depth]("r", Depth)
object Temp extends Parameter[Temperature]("t", Temperature)

trait Parameter[T](name: String, extractor: Extractor[T]):
  def unapply(params: ParamMap): Option[T] =
    for
      args  <- params.map.get(name)
      str   <- args.headOption
      value <- extractor.unapply(str)
    yield value

object ¬ :
  def unapply(params: ParamMap): Option[(ParamMap, ParamMap)] = Some((params, params))
@main
def db(cmd: String*): Unit =
  cmd.toList match
    case "add" :: ParamMap(Temp(temp) ¬ Rainfall(rain) ¬ Day(date)) =>
      val report = Report(date, temp, rain)
      Datastore.add(report)
      println(s"Adding a new weather report: $report")
    case "del" :: _ =>
      println("Deleting a weather report")
    case "list" :: _ =>
      Datastore.read().foreach(println)
    case _ =>
      println("Unknown command!")

object Datastore:
  import java.io._
  val filename = "reports.db"
  val file = File(filename)

  def add(report: Report): Unit =
    val writer = BufferedWriter(FileWriter(file, true))
    writer.write(s"${report.date},${report.temp},${report.rainfall}\n")
    writer.close()
  
  def read(): List[Report] =
    scala.io.Source.fromFile(file).getLines.toList.map {
      line => line.split(",").toList match
        case Date(date) :: Temperature(temp) :: Depth(rainfall) :: Nil =>
          Report(date, temp, rainfall)
    }

case class Report(date: Date, temp: Temperature, rainfall: Depth)

trait Extractor[T]:
  def unapply(str: String): Option[T]

case class Depth(mm: Int):
  override def toString: String = s"${mm}mm"

object Depth extends Extractor[Depth]:
  def unapply(str: String): Option[Depth] = str match
    case s"${value}mm" => Try(Depth(value.toInt)).toOption
    case _             => None


enum Temperature:
  case Celsius(value: Int)
  case Fahrenheit(value: Int)

  override def toString: String = this match
    case Celsius(value) => s"${value}C"
    case Fahrenheit(value) => s"${value}F"

object Temperature extends Extractor[Temperature]:
  def unapply(str: String): Option[Temperature] = str match
    case s"${value}C" => Try(Temperature.Celsius(value.toInt)).toOption
    case s"${value}F" => Try(Temperature.Fahrenheit(value.toInt)).toOption
    case _            => None


object Date extends Extractor[Date]:
  def unapply(str: String): Option[Date] = str match
    case s"$year/$month/$day" => Try(Date(year.toInt, month.toInt, day.toInt)).toOption
    case _                    => None

case class Date(year: Int, month: Int, day: Int):
  def pad(int: Int): String = if int < 10 then s"0$int" else int.toString
  override def toString: String = s"$year/${pad(month)}/${pad(day)}"

