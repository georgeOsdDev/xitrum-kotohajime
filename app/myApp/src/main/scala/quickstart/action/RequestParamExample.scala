package quickstart.action

import xitrum.{Action, SkipCsrfCheck}
import xitrum.annotation.{GET, POST}

@GET("/requestparam/:path1/:path2")
@POST("/requestparam/:path1/:path2")
class RequestParamIndex extends Action with SkipCsrfCheck {
  def execute() {

    // From path param
    val path1  = param("path1")
    val path2  = param("path2")
    log.debug("path1"+path1)
    log.debug("path2"+path2)

    // From query param
    val query1  = param("query1")
    val query2  = param("query2")
    log.debug("query1"+query1)
    log.debug("query2"+query2)

    // From body param when HTTP method is POST, PUT, PATCH
    val body1   = param("body1")
    val body2   = param("body2")
    log.debug("body1"+body1)
    log.debug("body2"+body2)

    respondText(
s"""
textParams:${textParams}
queryParams:${queryParams}
bodyTextParams:${bodyTextParams}
pathParams:${pathParams}
path1:${path1}
path2:${path2}
query1:${query1}
query2:${query2}
"""
    )
  }
}

@GET("/requestparamoption/:path1/:path2")
@POST("/requestparamoption/:path1/:path2")
class RequestParamOptionIndex extends Action with SkipCsrfCheck {
  def execute() {

    // From path param
    val path1  = paramo("path1")
    val path2  = paramo("path2")
    log.debug("path1"+path1)
    log.debug("path2"+path2)

    // From query param
    val query1  = paramo("query1")
    val query2  = paramo("query2")
    log.debug("query1"+query1)
    log.debug("query2"+query2)

    // From query param
    val body1   = paramo("body1")
    val body2   = paramo("body2")
    log.debug("body1"+body1)
    log.debug("body2"+body2)

    respondText(
s"""
textParams:${textParams}
queryParams:${queryParams}
bodyTextParams:${bodyTextParams}
pathParams:${pathParams}
path1:${path1}
path2:${path2}
query1:${query1}
query2:${query2}
body1:${body1}
body2:${body2}
"""
    )
  }
}
