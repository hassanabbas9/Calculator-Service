package com.calc

import org.scalatra._
import java.util.Base64
import java.nio.charset.StandardCharsets
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._
import com.calc.models._

class CalculatorServlet extends CalculatorAppStack with JacksonJsonSupport {
  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  // Decode Base64 query
  private def decodeBase64(query: String): String = {
    var decodedQuery = Base64.getDecoder.decode(query.getBytes(StandardCharsets.UTF_8))
    var standardQuery = new String(decodedQuery, "UTF-8")
    return standardQuery
  }

  get("/calculus") {
    var query = params("query")
    var expression = new CalculusExpression(decodeBase64(query))
    ApiResponse.jsonResponse(expression.calculate, expression.getError)
  }
}
