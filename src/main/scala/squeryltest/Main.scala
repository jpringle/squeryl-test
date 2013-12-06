package squeryltest

import java.util.UUID
import org.squeryl._
import org.squeryl.adapters.H2Adapter
import org.squeryl.dsl._
import scala.language.implicitConversions

object AppTypedExpressionFactories extends org.squeryl.PrimitiveTypeMode {

  implicit val coreIdTEF = new NonPrimitiveJdbcMapper[UUID, CoreId, TUUID](PrimitiveTypeSupport.uuidTEF, this) {
    def convertFromJdbc(id: UUID) = CoreId(id)
    def convertToJdbc(id: CoreId) = id.underlying
  }

  implicit val optionCoreIdTEF =
    new TypedExpressionFactory[Option[CoreId], TOptionUUID]
      with DeOptionizer[UUID, CoreId, TUUID, Option[CoreId], TOptionUUID] {
      val deOptionizer = coreIdTEF
    }

  implicit def coreIdToTE(s: CoreId) = coreIdTEF.create(s)
  implicit def optionCoreIdToTE(s: Option[CoreId]) = optionCoreIdTEF.create(s)
}
import AppTypedExpressionFactories._


object IdUtils {

  val uninitializedId: UUID = new UUID(0, 0)

  def idFromString(s: String): UUID = if (s.isEmpty) uninitializedId else UUID.fromString(s)

  def generateId: UUID = UUID.randomUUID

  def isUninitialized(id: UUID): Boolean = id == uninitializedId
}

case class CoreId(underlying: UUID) {
  def isInitialized: Boolean = !IdUtils.isUninitialized(underlying)
  override def toString: String = underlying.toString
}
object CoreId {
  def apply(s: String): CoreId = CoreId(IdUtils.idFromString(s))
  def uninitialized: CoreId = CoreId(IdUtils.uninitializedId)
  def generate: CoreId = CoreId(IdUtils.generateId)
}

// use F-bounded Type Polymorphism so we get correct subclass type
// when call site is in terms of superclass
trait Entity[T <: Entity[T]] {
  def id: CoreId
  def withNewId: T
}

case class User(id: CoreId, first: String, last: String) extends Entity[User] {
  def withNewId: User = copy(id = CoreId.generate)
}

trait BaseDAO[T <: Entity[T]] {

  def table: Table[T]

  def findById(id: CoreId): Option[T] = inTransaction {
    table.where(_.id === id).headOption
  }

  def save(e: T): T = if (isPersisted(e)) update(e) else create(e)

  def create(e: T): T = inTransaction {
    table.insert(e.withNewId)
  }

  // have to use an abstract method for the update
  def update(e: T): T

  // This gives us the following compilation error:
  //   The method requires an implicit org.squeryl.KeyedEntityDef[T, Any] in scope,
  //    or that it extends the trait KeyedEntity[Any]
  // def update(e: T): T = inTransaction {
  //   table.update(e)
  //   e
  // }

  private def isPersisted(e: T): Boolean = e.id.isInitialized
}

class UserDAO(schema: AppSchema) extends BaseDAO[User] {
  import schema._
  val table = users

  // This works, because the following is in scope:
  //   implicit object appKED extends KeyedEntityDef[Entity[_], CoreId] {...}
  def update(e: User): User = inTransaction {
    table.update(e)
    e
  }
}


class AppSchema extends Schema {

  implicit object appKED extends KeyedEntityDef[Entity[_], CoreId] {
    def getId(e: Entity[_]) = e.id
    def isPersisted(e: Entity[_]) = e.id.isInitialized
    def idPropertyName = "id"
  }

  val users = table[User]
}

object Main extends App {
    println("Hello")

    Class.forName("org.h2.Driver")
    SessionFactory.concreteFactory = Some( () =>
      Session.create(
        java.sql.DriverManager.getConnection(s"jdbc:h2:mem:test-db;DB_CLOSE_DELAY=-1", "sa", ""),
        new H2Adapter))

    val schema = new AppSchema
    import schema._

    transaction { schema.drop; schema.create; }

    val userDAO = new UserDAO(schema)

    val shouldBeNone = userDAO.findById(CoreId.generate)
    val newBob = User(CoreId.uninitialized, "Bob", "Smith")
    val savedBob  = userDAO.save(newBob)
    val foundBob  = userDAO.findById(savedBob.id).get
    val updatedBob   = userDAO.save(foundBob.copy(first = "Robert"))

    println(s"shouldBeNone -> $shouldBeNone")
    println(s"newBob     -> $newBob")
    println(s"savedBob   -> $savedBob")
    println(s"foundBob   -> $foundBob")
    println(s"updatedBob -> $updatedBob")
}