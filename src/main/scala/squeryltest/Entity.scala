package squeryltest

// use F-bounded Type Polymorphism so we get correct subclass type
// when call site is in terms of superclass
trait Entity[T <: Entity[T]] {
  def id: CoreId
  def withNewId: T
}

