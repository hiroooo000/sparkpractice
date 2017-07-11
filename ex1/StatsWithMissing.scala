
class NAStatCounter extends Serializable {
  import org.apache.spark.util.StatCounter
  val stats: StatCounter = new StatCounter()
  var missing: Long =0

  def add(x: Double): NAStatCounter = {
    if ( java.lang.Double.isNaN(x) ) {
      missing += 1
    } else {
      stats.merge(x)
    }
    this
  }
  
  def merge(other: NAStatCounter): NAStatCounter = {
    stats.merge(other.stats)
    missing += other.missing
    this
  }
 
  override def toString = {
    "stats:" + stats.toString() + " NaN:" + missing
  }
}

object NAStatCounter extends Serializable {
  def apply(x: Double) = new NAStatCounter().add(x)
}

val rb = sc.textFile("data/linkage")

def isHeader(line: String): Boolean = {
  line.contains("id_1")
}

val noheaderRDD = rb.filter(!isHeader(_))

case class MatchData(
  id1: Int,
  id2: Int,
  scores: Array[Double],
  matched: Boolean
)

def parse(line: String) = {
  val pieces = line.split(",")
  val id1 = pieces(0).toInt
  val id2 = pieces(1).toInt
  val scores = pieces.slice(2,11).map( x => { if("?".equals(x)) Double.NaN else x.toDouble } )
  val matched = pieces(11).toBoolean
  MatchData(id1,id2,scores,matched)
}

val parsed = noheaderRDD.map(parse)

val matchCount = parsed.map(x => x.matched).countByValue()

val stats = (0 until 9).foreach( i => {
  parsed.map(x => x.scores(i)).filter(!java.lang.Double.isNaN(_)).stats()
})

stats.foreach(println)
