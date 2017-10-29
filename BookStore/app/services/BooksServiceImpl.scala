package services

import javax.inject.{Inject, Singleton}

import models.Book
import repository.BooksRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BooksServiceImpl @Inject()(booksRepository: BooksRepository)
                                (implicit context: ExecutionContext) extends BooksService {

  override def getBooks(filter: String): Future[Seq[Book]] = booksRepository.getBooks(filter)

  override def getBookById(id: Long): Future[Option[Book]] = booksRepository.getBookById(id)

  override def addBook(book: Book): Future[Int] = booksRepository.addBook(book)

  override def removeBook(id: Long): Future[Int] = booksRepository.removeBook(id)

  override def updateBook(id: Long, book: Book): Future[Int] = booksRepository.updateBook(id, book)
}
