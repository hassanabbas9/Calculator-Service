package com.calc.models

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.ArrayBuffer
import scala.util.Try
import org.json4s._
import org.json4s.JsonDSL._

// Make Appropriate Json Response based on results
object ApiResponse {
  def jsonResponse(result: Double, error: String): JObject = {
    if(error == "") {
      var json = ("error" -> "false") ~ ("result" -> result.toString)
      json
    }
    else {
      var json = ("error" -> "true") ~ ("message" -> error)
      json
    }
  }
}

class CalculusExpression(expresion: String) {
  val LA = "LA"
  val RA = "RA"
  val OPS = Map("^" -> (4, RA), "/" -> (3, LA), "*" -> (3, LA), "+" -> (2, LA), "-" -> (2, LA))
  val LB = "("
  val RB = ")"
  val LBT = (-100, LB, LB)
  var errorMsg = ""

  def getError: String = errorMsg

  def isNumber(exp: String) = Try{exp.toInt}.isSuccess || Try{exp.toLong}.isSuccess || Try{exp.toDouble}.isSuccess

  // Add spaces to make a standard expression
  def convertToStandardExpression: String = {
    val parsingIdentifiers = List("+", "-", "/", "*", "(", ")", "^", "{", "}", "%", "[", "]")
    var standardExpression, number = ""

    expresion.foreach { e =>
      if(parsingIdentifiers.contains(e.toString)) {
        standardExpression = standardExpression +  " " + number + " " + e
        number = ""
      }
      else
        number = number + e
    }
    if(number != "")
      standardExpression = standardExpression +  " " + number + " "

    standardExpression = standardExpression.trim().replaceAll("( )+", " ")
    standardExpression
  }

  // Evaluate the expression from postFixNotation
  def evalute(postFixNotation: ArrayBuffer[String]): Double = {
    var stack: ArrayBuffer[Double] = ArrayBuffer()
    try {
      postFixNotation.foreach { v =>
        v match {
          case "+" =>
            var operand1 = stack.remove(stack.length-1).toDouble
            var operand2 = stack.remove(stack.length-1).toDouble
            var result = (operand2 + operand1).toDouble
            stack += result
          case "-" =>
            var operand1 = stack.remove(stack.length-1).toDouble
            var operand2 = stack.remove(stack.length-1).toDouble
            var result = (operand2 - operand1).toDouble
            stack += result
          case "*" =>
            var operand1 = stack.remove(stack.length-1).toDouble
            var operand2 = stack.remove(stack.length-1).toDouble
            var result = (operand2 * operand1).toDouble
            stack += result
          case "/" =>
            var operand1 = stack.remove(stack.length-1).toDouble
            var operand2 = stack.remove(stack.length-1).toDouble
            var result = (operand2 / operand1).toDouble
            stack += result
          case _ =>
            if(isNumber(v))
              stack += v.toDouble
            else
              throw new Exception()
        }
      }
    }
    catch {
      case ex: Exception => {
        errorMsg = "Following operations are not supported: ^ { } % "
      }
    }
    // Round off to 3 decimal places
    (math floor stack.remove(0) * 1000) / 1000
  }

  // Convert the infixNotation to postFixNotation and then evaluate
  def calculate: Double = {
    var stack: ListBuffer[(Int, String, String)] = ListBuffer()
    var buffer: ArrayBuffer[String] = ArrayBuffer()
    var standardExpression = convertToStandardExpression
    val tokens: Array[String] = standardExpression.split(" ")

    def handleRightBracket = {
      var foundRB = false
      while(!stack.isEmpty && !foundRB) {
        stack.remove(0)._3 match {
          case (LB) =>
            foundRB = true
          case (op: String) =>
            buffer.append(op)
        }
      }
    }

    def pop(op: (Int, String)) = {
      while(!stack.isEmpty && op._1 < stack.head._1)
        buffer.append(stack.remove(0)._3)
    }

    try {
      tokens map {
        case(token: String) if(isNumber(token)) => buffer += token
        case(token: String) if(token == LB) => stack.prepend(LBT)
        case(token: String) if(token == RB) => handleRightBracket
        case(token: String) => OPS.get(token) match {
          case None => throw new IllegalArgumentException("")
          case Some(op: (Int, String)) if(stack.isEmpty) => stack.prepend((op._1, op._2,token))
          case Some(op: (Int, String)) if(op._2 == RA && op._1 >= stack.head._1) => stack.prepend((op._1, op._2,token))
          case Some(op: (Int, String)) if(op._2 == RA && op._1 < stack.head._1) =>
            while(!stack.isEmpty && op._1 < stack.head._1)
              buffer.append(stack.remove(0)._3)
            stack.prepend((op._1, op._2,token))
          case Some(op: (Int, String)) if(op._2 == LA && op._1 > stack.head._1) => stack.prepend((op._1, op._2,token))
          case Some(op: (Int, String)) if(op._2 == LA && op._1 <= stack.head._1) =>
            while(!stack.isEmpty && op._1 <= stack.head._1)
              buffer.append(stack.remove(0)._3)
            stack.prepend((op._1, op._2,token))
        }
      }
      while(!stack.isEmpty)
        buffer.append(stack.remove(0)._3)
    }
    catch {
      case ex: Exception =>
        errorMsg = "Following operations are not supported: ^ { } % "
    }
    evalute(buffer)
  }
}
