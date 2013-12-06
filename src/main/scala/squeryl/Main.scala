package squeryl

import java.util.UUID
import org.squeryl._
import org.squeryl.adapters.H2Adapter
import org.squeryl.dsl._
import scala.language.implicitConversions

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

trait Entity {
  def id: CoreId
}

case class User(id: CoreId, first: String, last: String) extends Entity

case class Identity(id: CoreId, identity: String, status: String)

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

class UserDAO(schema: AppSchema) {
  import schema._

  def findById(id: CoreId): Option[User] = inTransaction {
    users.where(_.id === id).headOption
  }

  def create(e: User): User = inTransaction {
    users.insert(e.copy(id = CoreId.generate))
  }

  def update(e: User): User = inTransaction {
    users.update(e)
    e
  }
}


class AppSchema extends Schema {

  implicit object appKED extends KeyedEntityDef[Entity, CoreId] {
    def getId(e: Entity) = e.id
    def isPersisted(e: Entity) = e.id.isInitialized
    def idPropertyName = "id"
  }

  val users = table[User]
  val identities = table[Identity]
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
    val savedBob  = userDAO.create(newBob)
    val foundBob  = userDAO.findById(savedBob.id).get
    val updatedBob   = userDAO.update(foundBob.copy(first = "Robert"))

    println(s"shouldBeNone -> $shouldBeNone")
    println(s"newBob     -> $newBob")
    println(s"savedBob   -> $savedBob")
    println(s"foundBob   -> $foundBob")
    println(s"updatedBob -> $updatedBob")
}