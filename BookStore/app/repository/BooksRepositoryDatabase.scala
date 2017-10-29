package repository

import java.sql.{Connection, SQLException}
import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import models.Book
import anorm._
import anorm.SqlParser._
import play.api.db.DBApi
import play.api.libs.concurrent.CustomExecutionContext

import scala.concurrent.Future

@Singleton
class DatabaseExecutionContext @Inject()(actorSystem: ActorSystem) extends CustomExecutionContext(actorSystem, "database.dispatcher")

@Singleton
class BooksRepositoryDatabase @Inject()(dbApi: DBApi)
                                       (implicit context: DatabaseExecutionContext) extends BooksRepository {

  private val database = dbApi.database("default")

  private val bookParser =
    get[Long]("book.id") ~
    get[String]("book.title") ~
    get[Int]("book.price") ~
    get[String]("book.author") map {
      case id ~ title ~ price ~ author => Book(id, title, price, author)
    }

  override def getBooks(filter: String): Future[Seq[Book]] = Future {
    database.withConnection { implicit connection =>
      SQL("select * from book where book.title like {filter} or cast(book.price as text) like {filter} or book.author like {filter}")
        .on("filter" -> s"%$filter%")
        .as(bookParser *)
    }
  }

  override def getBookById(id: Long): Future[Option[Book]] = Future {
    database.withConnection { implicit connection =>
      SQL("select * from book where id = {id}")
        .on("id" -> id)
        .as(bookParser singleOpt)
    }
  }

  override def addBook(book: Book): Future[Int] = Future {
    database.withConnection { implicit connection =>
      SQL("insert into book values((select next value for book_seq), {title}, {price}, {author})")
        .on(
          "title" -> book.title,
          "price" -> book.price,
          "author" -> book.author
        )
    }
  }

  override def removeBook(id: Long): Future[Int] = Future {
    database.withConnection { implicit connection =>
      SQL("delete from book where id = {id}")
        .on("id" -> id)
    }
  }

  override def updateBook(id: Long, book: Book): Future[Int] = Future {
    database.withConnection { implicit connection =>
      SQL("update book set title = {title}, price = {price}, author = {author} where id = {id}")
        .on(
          "id" -> id,
          "title" -> book.title,
          "price" -> book.price,
          "author" -> book.author
        )
    }
  }

  implicit private[this] def executeWithPossibleException(simpleSql: SimpleSql[Row])
                                                         (implicit connection: Connection): Int = {
    try {
      simpleSql.executeUpdate()
    } catch {
      case _: SQLException => 0
    }
  }
}
