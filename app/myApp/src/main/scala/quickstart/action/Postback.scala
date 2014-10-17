package quickstart.action
import xitrum.annotation.{GET, POST}

@GET("postback")
class PostbackIndex extends DefaultLayout {
  def execute() {
    respondInlineView(
      <form data-postback="submit" action={url[PostbackAction]}>
        <label>Title</label>
        <input type="text" name="title" class="required" /><br />

        <label>Body</label>
        <textarea name="body" class="required"></textarea><br />

        <input type="submit" value="Postback" />
      </form>
      <hr />
      <form data-postback="submit" action={url[PostbackAction2]}>
        <label>Title</label>
        <input type="text" name="title" class="required" /><br />

        <label>Body</label>
        <textarea name="body" class="required"></textarea><br />

        <input type="submit" value="Postback2" />
      </form>
    )
  }
}

@POST("postback")
class PostbackAction extends DefaultLayout {
  def execute() {
    val title   = param("title")
    val body    = param("body")
    flash("Posted.")
    jsRedirectTo[PostbackIndex]()
  }
}

@POST("postback2")
class PostbackAction2 extends DefaultLayout {
  def execute() {
    val title   = param("title")
    val body    = param("body")
    jsRespond(s"""$$('body').append('title:$title\\nbody:$body')""")
  }
}
