package squeryltest

case class User(id: CoreId, first: String, last: String) extends Entity[User] {
  def withNewId: User = copy(id = CoreId.generate)
}

case class Identity(id: CoreId, value: String) extends Entity[Identity] {
  def withNewId: Identity = copy(id = CoreId.generate)
}
