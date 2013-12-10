package squeryltest

trait Users extends BaseCRUD[User] {

  // Identity related stuff below here....
  def findIdentityById(id: CoreId): Option[Identity]

  def save(e: Identity): Identity

  def create(e: Identity): Identity

  def update(e: Identity): Identity
}

class UserDAO extends Users with BaseDAO[User] {
  val table = AppSchema.users

  // Identity related stuff below here....
  val ids = new IdentityDAO

  def findIdentityById(id: CoreId): Option[Identity] = ids.findById(id)

  def save(e: Identity): Identity = ids.save(e)

  def create(e: Identity): Identity = ids.create(e)

  def update(e: Identity): Identity = ids.update(e)
}

class IdentityDAO extends BaseDAO[Identity] {
  val table = AppSchema.identity
}
