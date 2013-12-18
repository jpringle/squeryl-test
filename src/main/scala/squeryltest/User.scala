package squeryltest

case class User(id: EntityId, first: String, last: String) extends Entity[User] {
  def withNewId: User = copy(id = EntityId.generate)
}

case class Identity(id: EntityId, value: String) extends Entity[Identity] {
  def withNewId: Identity = copy(id = EntityId.generate)
}
