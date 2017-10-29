package repository

import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

class BooksRepositoryTest extends PlaySpec with GuiceOneAppPerSuite with ScalaFutures {
  import models._

  import scala.concurrent.ExecutionContext.Implicits.global

  def booksRepository: BooksRepository = app.injector.instanceOf(classOf[BooksRepositoryDatabase])

  "Repository of books" should {

    "get book with existed id" in {
      whenReady(booksRepository.getBookById(1)) { optionBook =>
        optionBook mustNot equal(None)

        val book = optionBook.get

        book.title mustBe "C++"
        book.price mustBe 30
        book.author must equal("Bjarne Stroustrup")
      }
    }

    "get none book" in {
      whenReady(booksRepository.getBookById(123456)) { optionBook =>
        optionBook mustBe None
      }
    }

    "get sequence of books" in {
      whenReady(booksRepository.getBooks("")) { books =>
        books.length mustBe 3
      }
    }

    "get filtered sequence of books" in {
      whenReady(booksRepository.getBooks("av")) { books =>
        books.length mustBe 1
      }
    }

    "add book" in {
      whenReady(booksRepository.addBook(Book(-1L, "Some title", 10, "Some author"))) { code =>
        code mustBe 1
      }
    }

    "not add book with invalid price" in {
      whenReady(booksRepository.addBook(Book(-1L, "Some title", 0, "Some author"))) { code =>
        code mustBe 0
      }
    }

    "remove book" in {
      whenReady(booksRepository.removeBook(2)) { code =>
        code mustBe 1
      }
    }

    "not remove with non existing id" in {
      whenReady(booksRepository.removeBook(123456)) { code =>
        code mustBe 0
      }
    }

    "update book" in {
      whenReady(booksRepository.getBookById(1).flatMap { book =>
        booksRepository.updateBook(1, Book(-1, "Some title", 100, "Some author")).flatMap { _ =>
          booksRepository.getBookById(1)
        }
      }) { optionBook =>
        optionBook mustNot equal(None)

        val book = optionBook.get

        book.title mustBe "Some title"
        book.price mustBe 100
        book.author mustBe "Some author"
      }
    }

    "not update book with invalid price" in {
      whenReady(booksRepository.getBookById(1).flatMap {book =>
        booksRepository.updateBook(1, Book(-1, "Some title", 0, "Some author")).flatMap { _ =>
          booksRepository.getBookById(1)
        }
      }) { optionBook =>
        optionBook mustNot equal(None)

        val book = optionBook.get

        book.price mustNot equal(0)
      }
    }
  }
}
