package squeryltest

import java.util.UUID

// use F-bounded Type Polymorphism so we get correct subclass type
// when call site is in terms of superclass
trait Entity[T <: Entity[T]] {
  def id: EntityId
  def withNewId: T
}

case class EntityId(underlying: UUID) {
  def isInitialized: Boolean = !IdUtils.isUninitialized(underlying)
  override def toString: String = underlying.toString
}
object EntityId {
  def apply(s: String): EntityId = EntityId(IdUtils.idFromString(s))
  def uninitialized: EntityId = EntityId(IdUtils.uninitializedId)
  def generate: EntityId = EntityId(IdUtils.generateId)
}
