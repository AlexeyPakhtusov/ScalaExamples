package services

import models.Book

import scala.concurrent.Future

trait BooksService {

  def getBooks(filter: String): Future[Seq[Book]]

  def getBookById(id: Long): Future[Option[Book]]

  def addBook(book: Book): Future[Int]

  def removeBook(id: Long): Future[Int]

  def updateBook(id: Long, book: Book): Future[Int]
}
