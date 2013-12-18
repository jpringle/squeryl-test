package squeryltest

import java.util.UUID
import org.squeryl._
import org.squeryl.dsl._
import scala.language.implicitConversions

object AppTypedExpressionFactories extends org.squeryl.PrimitiveTypeMode {

  implicit val coreIdTEF = new NonPrimitiveJdbcMapper[UUID, EntityId, TUUID](PrimitiveTypeSupport.uuidTEF, this) {
    def convertFromJdbc(id: UUID) = EntityId(id)
    def convertToJdbc(id: EntityId) = id.underlying
  }

  implicit val optionCoreIdTEF =
    new TypedExpressionFactory[Option[EntityId], TOptionUUID]
      with DeOptionizer[UUID, EntityId, TUUID, Option[EntityId], TOptionUUID] {
      val deOptionizer = coreIdTEF
    }

  implicit def coreIdToTE(s: EntityId) = coreIdTEF.create(s)
  implicit def optionCoreIdToTE(s: Option[EntityId]) = optionCoreIdTEF.create(s)
}
import AppTypedExpressionFactories._

object AppSchema extends Schema {

  implicit object appKED extends KeyedEntityDef[Entity[_], EntityId] {
    def getId(e: Entity[_]) = e.id
    def isPersisted(e: Entity[_]) = e.id.isInitialized
    def idPropertyName = "id"
  }

  val users = table[User]
  val identity = table[Identity]
}

