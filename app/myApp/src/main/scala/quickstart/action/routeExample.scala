package quickstart.action

import xitrum.{Action, SkipCsrfCheck}
import xitrum.annotation.{GET, POST, PUT, DELETE, PATCH, First}

@GET("/path/to/myaction")
class MyAction extends ClassNameResponder {
  def execute() {
    log.debug("MyAction")
    respondClassNameAsText()
  }
}

@GET("one" ,"two")
class MultiPathAction extends ClassNameResponder {
  def execute() {
    log.debug("MultiPathAction")
    respondClassNameAsText()
  }
}

@GET("/dot.html")
class DotInPathAction extends ClassNameResponder {
  def execute() {
    log.debug("DotInPathAction")
    respondClassNameAsText()
  }
}

@GET("/item/:categoryId/:itemId")
class ItemDetailAction extends ClassNameResponder {
  def execute() {
    log.debug("ItemDetailAction")

    // pathParamの取得
    val categoryId = param("categoryId")
    log.debug(categoryId)

    val itemId = param("itemId")
    log.debug(itemId)

    respondClassNameAsText()
  }
}

@First
@GET("/item/list/:categoryId")
class ItemListAction extends ClassNameResponder {
  def execute() {
    log.debug("ItemListAction")

    // pathParamの取得
    val categoryId = param("categoryId")
    log.debug(categoryId)

    respondClassNameAsText()
  }
}

@GET("animal/:categoryId<[0-9]+>/:animalId")
class AnimalDetailAction extends ClassNameResponder {
  def execute() {
    log.debug("AnimalDetailAction")

    // pathParamの取得
    val categoryId = param("categoryId")
    log.debug(categoryId)

    val animalId = param("animalId")
    log.debug(animalId)

    respondClassNameAsText()
  }
}

@GET("/animal/list/:categoryId")
class AnimalListAction extends ClassNameResponder {
  def execute() {
    log.debug("AnimalListAction")

    // pathParamの取得
    val categoryId = param("categoryId")
    log.debug(categoryId)

    respondClassNameAsText()
  }
}
