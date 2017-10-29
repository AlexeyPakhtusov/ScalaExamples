import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerTest

class BrowserTest extends PlaySpec
  with GuiceOneServerPerTest
  with OneBrowserPerTest
  with HtmlUnitFactory {

  "Application" must {

    "work from within the browser" in {
      go to s"http://localhost:$port"

      pageSource must include("Welcome to Book Store Application!")

      go to s"http://localhost:$port/books"

      pageSource must include("3 books found")

      click on id("filterBox")
      enter("va")
      click on id("submitFilter")

      pageSource must include("1 books found")

      go to s"http://localhost:$port/books/create"

      find("createBook").get.text must equal("Create book")

      click on id("title")
      enter("Some title")
      click on id("price")
      enter("invalid")
      click on id("author")
      enter("Some author")
      click on id("createButton")

      pageSource must include("Create book")

      click on id("price")
      enter("20")
      click on id("createButton")

      find("booksCount").get.text must equal("4 books found")
      pageSource must include("Some title")
      pageSource must include("20")
      pageSource must include("Some author")

      go to s"http://localhost:$port/books/100"

      click on id("editBook")

      pageSource must include("Some title")
      pageSource must include("20")
      pageSource must include("Some author")

      click on id("title")
      enter("Another title")
      click on id("editButton")

      find("booksCount").get.text must equal("4 books found")
      pageSource must include("Another title")
      pageSource must include("20")
      pageSource must include("Some author")
    }
  }
}
