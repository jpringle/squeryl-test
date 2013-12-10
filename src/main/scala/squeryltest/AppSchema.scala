package squeryltest

import java.util.UUID
import org.squeryl._
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

object AppSchema extends Schema {

  implicit object appKED extends KeyedEntityDef[Entity[_], CoreId] {
    def getId(e: Entity[_]) = e.id
    def isPersisted(e: Entity[_]) = e.id.isInitialized
    def idPropertyName = "id"
  }

  val users = table[User]
  val identity = table[Identity]
}

