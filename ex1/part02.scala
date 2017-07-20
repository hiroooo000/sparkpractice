
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

val arr = Array[Double](1.0, Double.NaN, 17.29)
val nas = arr.map( d => NAStatCounter(d) )

import org.apache.spark.rdd.RDD

def statsWithMissing(rdd: RDD[Array[Double]]): Array[NAStatCounter] = {
  val stats = rdd.mapPartitions((iter: Iterator[Array[Double]]) => {
    val nas: Array[NAStatCounter] = iter.next().map(d => NAStatCounter(d))
    iter.foreach( arr => {
      nas.zip(arr).foreach( { case (n,d) => n.add(d) } )
    })
    Iterator(nas)
  })
  stats.reduce( (n1,n2) => {
    n1.zip(n2).map ({case (a,b) => a.merge(b)})
  })
}

val statsm = statsWithMissing(parsed.filter(_.matched).map(_.scores))
val statsn = statsWithMissing(parsed.filter(!_.matched).map(_.scores))
