package squeryltest

import java.util.UUID

case class EntityId(underlying: UUID) {
  def isInitialized: Boolean = !IdUtils.isUninitialized(underlying)
  override def toString: String = underlying.toString
}
object EntityId {
  def apply(s: String): EntityId = EntityId(IdUtils.idFromString(s))
  def uninitialized: EntityId = EntityId(IdUtils.uninitializedId)
  def generate: EntityId = EntityId(IdUtils.generateId)
}

