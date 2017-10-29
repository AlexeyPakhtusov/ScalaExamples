package controllers

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.{FakeRequest, Injecting}
import play.api.test.Helpers._

class BooksControllerFuncTest extends PlaySpec with GuiceOneAppPerSuite with Injecting {

  "BooksController GET" should {

    "return list of books without filter" in {
      val controller = inject[BooksController]
      val content = controller.index("")(FakeRequest(GET, "/books"))

      status(content) mustBe OK
      contentType(content) mustBe Some("text/html")
      contentAsString(content) must include("3 books found")
    }

    "return list of books with filter" in {
      val controller = inject[BooksController]
      val content = controller.index("va")(FakeRequest(GET, "/books"))

      status(content) mustBe OK
      contentType(content) mustBe Some("text/html")
      contentAsString(content) must include("1 books found")
    }

    "return create page" in {
      val content = route(app, FakeRequest(GET, "/books/create")).get

      status(content) mustBe OK
      contentType(content) mustBe Some("text/html")
      contentAsString(content) must include("Create book")
    }

    "return book page" in {
      val controller = inject[BooksController]
      val content = controller.show(1L)(FakeRequest(GET, "/books/1"))

      status(content) mustBe OK
      contentType(content) mustBe Some("text/html")
      contentAsString(content) must include("C++")
      contentAsString(content) must include("30")
      contentAsString(content) must include("Bjarne Stroustrup")
    }

    "return not found book page" in {
      route(app, FakeRequest(GET, "/books/-1")).map(status) mustBe Some(NOT_FOUND)
    }

    "return book edit page" in {
      val controller = inject[BooksController]
      val content = controller.edit(1L)(FakeRequest(GET, "/books/edit/1"))

      status(content) mustBe OK
      contentType(content) mustBe Some("text/html")
      contentAsString(content) must include("C++")
      contentAsString(content) must include("30")
      contentAsString(content) must include("Bjarne Stroustrup")
    }

    "return not found book edit page" in {
      route(app, FakeRequest(GET, "/books/edit/-1")).map(status) mustBe Some(NOT_FOUND)
    }
  }

  "BooksController POST" should {

    "successfully save new book" in {
      val controller = inject[BooksController]
      val result = controller.save {
        FakeRequest().withFormUrlEncodedBody("title" -> "Some title", "price" -> "20", "author" -> "Some author")
      }

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("/books")
      flash(result).get("success") mustBe Some("Book Some title has been added")
    }

    "return bad request while saving new book" in {
      val controller = inject[BooksController]
      val result = controller.save {
        FakeRequest().withFormUrlEncodedBody("title" -> "Some title", "price" -> "invalid", "author" -> "Some author")
      }

      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("Some title")
      contentAsString(result) must include("invalid")
      contentAsString(result) must include("Some author")
    }

    "successfully update book" in {
      val controller = inject[BooksController]
      val result = controller.update(2L) {
        FakeRequest().withFormUrlEncodedBody("title" -> "Some title", "price" -> "20", "author" -> "Some author")
      }

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some("/books")
      flash(result).get("success") mustBe Some("Book Some title has been updated")
    }

    "return bad request while updating book" in {
      val controller = inject[BooksController]
      val result = controller.update(2L) {
        FakeRequest().withFormUrlEncodedBody("title" -> "Some title", "price" -> "invalid", "author" -> "Some author")
      }

      status(result) mustBe BAD_REQUEST
      contentAsString(result) must include("Some title")
      contentAsString(result) must include("invalid")
      contentAsString(result) must include("Some author")
    }
  }

  "BooksController DELETE" should {

    "successfully delete book" in {
      val controller = inject[BooksController]
      val result = controller.destroy(2L)(FakeRequest(DELETE, "/bookd/delete/2"))

      status(result) mustBe OK
      flash(result).get("success") mustBe Some("Book has been deleted")
    }

    "return not found" in {
      route(app, FakeRequest(DELETE, "/books/delete/-1")).map(status) mustBe Some(NOT_FOUND)
    }
  }
}
