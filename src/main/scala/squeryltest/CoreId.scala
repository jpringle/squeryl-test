package squeryltest

import java.util.UUID

case class CoreId(underlying: UUID) {
  def isInitialized: Boolean = !IdUtils.isUninitialized(underlying)
  override def toString: String = underlying.toString
}
object CoreId {
  def apply(s: String): CoreId = CoreId(IdUtils.idFromString(s))
  def uninitialized: CoreId = CoreId(IdUtils.uninitializedId)
  def generate: CoreId = CoreId(IdUtils.generateId)
}

