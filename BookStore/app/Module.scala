import com.google.inject.AbstractModule
import repository.{BooksRepository, BooksRepositoryDatabase}
import services._

class Module extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[BooksRepository]).to(classOf[BooksRepositoryDatabase])
    bind(classOf[BooksService]).to(classOf[BooksServiceImpl])
  }
}
