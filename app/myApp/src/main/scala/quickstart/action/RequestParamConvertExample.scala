package quickstart.action

import scala.reflect.runtime.universe._

import xitrum.Action
import xitrum.annotation.{GET, POST}

@GET("/requestparamconvert/:one")
class RequestConvertIndex extends Action {
  def execute() {

      val one_as_String   = param[String]("one")
      val one_as_Char     = param[Char]("one")
      val one_as_Byte     = param[Byte]("one")
      val one_as_Short    = param[Short]("one")
      val one_as_Int      = param[Int]("one")
      val one_as_Long     = param[Long]("one")
      val one_as_Float    = param[Float]("one")
      val one_as_Double   = param[Double]("one")
      val one_as_Implicit = param("one")


    respondText(
s"""
one_as_String =>   Class:${one_as_String.getClass.toString}, Value:${one_as_String}
one_as_Char =>     Class:${one_as_Char.getClass.toString},   Value:${one_as_Char}
one_as_Byte =>     Class:${one_as_Byte.getClass.toString},   Value:${one_as_Byte}
one_as_Short =>    Class:${one_as_Short.getClass.toString},  Value:${one_as_Short}
one_as_Int =>      Class:${one_as_Int.getClass.toString},    Value:${one_as_Int}
one_as_Long =>     Class:${one_as_Long.getClass.toString},   Value:${one_as_Long}
one_as_Float =>    Class:${one_as_Float.getClass.toString},  Value:${one_as_Float}
one_as_Double =>   Class:${one_as_Double.getClass.toString}, Value:${one_as_Double}
one_as_Implicit => Class:${one_as_Implicit.getClass.toString}, Value:${one_as_Implicit}
"""
    )
  }
}

case class MyClass(value:String)

@GET("/requestparamconvertcustome/:one")
class RequestConvertCustomeIndex extends Action {
  override  def convertTextParam[T: TypeTag](value: String): T = {
    val t = typeOf[T]
    val any: Any =
           if (t <:< typeOf[String])  value
      else if (t <:< typeOf[MyClass]) MyClass(value)
      else if (t <:< typeOf[Int])    value.toInt
      else throw new Exception("convertTextParam cannot covert " + value + " to " + t)
    any.asInstanceOf[T]
  }

  def execute() {

      val one_as_MyClass  = param[MyClass]("one")
      val one_as_String   = param[String]("one")
      val one_as_Int      = param[Int]("one")


    respondText(
s"""
one_as_String =>   Class:${one_as_String.getClass.toString},  Value:${one_as_String}
one_as_MyClass=>   Class:${one_as_MyClass.getClass.toString}, Value:${one_as_MyClass}
one_as_Int =>      Class:${one_as_Int.getClass.toString},     Value:${one_as_Int}
"""
    )
  }
}
