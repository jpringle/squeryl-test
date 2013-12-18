package squeryltest

import java.util.UUID

// use F-bounded Type Polymorphism so we get correct subclass type
// when call site is in terms of superclass
trait Entity[T <: Entity[T]] {
  def id: EntityId
  def withNewId: T
}

case class EntityId(underlying: UUID) {
  def isInitialized: Boolean = this != EntityId.uninitializedId
  override def toString: String = underlying.toString
}
object EntityId {
  def apply(s: String): EntityId = EntityId(idFromString(s))
  def generate: EntityId = EntityId(generateId)

  lazy val uninitializedId: EntityId = new EntityId(uninitializedUUID)

  private val uninitializedUUID = new UUID(0, 0)
  private def idFromString(s: String): UUID = if (s.isEmpty) uninitializedUUID else UUID.fromString(s)
  private def generateId: UUID = UUID.randomUUID

}
