
def factorial( i: Long ): Long = {
  def fact( i: Long, accumulator: Long): Long = {
    if ( i <= 1 ) {
      accumulator
    } else {
      fact( i - 1, accumulator * i )
    }
  }

  fact(i , 1)
}


val a = List(1,2,3,4,5,6,78,9,10)

for( i <- a )
  println(i)

for( i <- a if i > 4)
  println(i)

for{ i <- a
  if i > 4
  if i%2 == 1
} println(i)

import scala.util.Random
val randInt = new Random().nextInt(10)

randInt match {
  case 7 => println("Lucky seven!")
  case othernumber => println("boo, " + othernumber)
}


val ss = List(23, "Hello", 8.5, 'q')
for ( s <- ss){
  s match {
    case i: Int => println("Int")
    case s: String => println("String")
    case d: Double => println("Double")
    case other => println("other")
  }
}
