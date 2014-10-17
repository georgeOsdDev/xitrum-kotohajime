package quickstart.action

import xitrum.Action
import xitrum.annotation.GET

@GET("/requestheader")
class RequestHeaderIndex extends Action {
  def execute() {
    val headers = request.headers
    log.debug("Header:" + headers.toString)

    val entries = headers.entries
    log.debug("Entries:" + entries.toString)

    val myHeader = headers.get("X-MyHeader")
    log.debug("X-MyHeader:" + myHeader.toString)


    respondText(
s"""
Header:${headers.toString}
Entries:${entries.toString}
X-MyHeader:${myHeader.toString}
"""
    )
  }
}
