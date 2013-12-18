package squeryltest

import org.squeryl._
import org.squeryl.dsl._

import AppTypedExpressionFactories._

trait BaseDAO[T <: Entity[T]] extends BaseCRUD[T] {

  def table: Table[T]

  def findById(id: EntityId): Option[T] = inTransaction {
    table.where(_.id === id).headOption
  }

  def save(e: T): T = if (isPersisted(e)) update(e) else create(e)

  def create(e: T): T = inTransaction {
    table.insert(e.withNewId)
  }

  def update(e: T): T = inTransaction {
    table.update(e)(AppSchema.appKED)
    e
  }

  private def isPersisted(e: T): Boolean = e.id.isInitialized
}

trait BaseCRUD[T <: Entity[T]] {

  def findById(id: EntityId): Option[T]

  def save(e: T): T

  def create(e: T): T

  def update(e: T): T
}
