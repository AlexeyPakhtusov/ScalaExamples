package repository

import javax.inject.{Inject, Singleton}

import models.Book

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BooksRepositoryHardcode @Inject()(implicit context: ExecutionContext) extends BooksRepository {

  private var books = Map(
    1L -> Book(1, "C++", 30, "Bjarne Stroustrup"),
    2L -> Book(2, "Java", 25, "James Gosling"),
    3L -> Book(3, "Scala", 25, "Martin Odersky")
  )

  override def getBooks(filter: String): Future[Seq[Book]] = Future {
    books.values.filter(book => s".*$filter.*".r.pattern.matcher(book.title.toLowerCase).matches).toSeq
  }

  override def getBookById(id: Long): Future[Option[Book]] = Future(books.get(id))

  override def addBook(book: Book): Future[Int] = Future {
    books += book.id -> book
    if (books.contains(book.id)) 1 else 0
  }

  override def removeBook(id: Long): Future[Int] = Future {
    books -= id
    if (!books.contains(id)) 1 else 0
  }

  override def updateBook(id: Long, book: Book): Future[Int] = Future {
    books += id -> book
    books.get(id) match {
      case Some(value) if value.id == id && value.title == book.title && value.price == book.price && value.author == book.author => 1
      case _ => 0
    }
  }
}
