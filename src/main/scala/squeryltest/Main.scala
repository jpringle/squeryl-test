package squeryltest

import org.squeryl._
import org.squeryl.adapters.H2Adapter
import AppSchema._
import AppTypedExpressionFactories._

object Main extends App {
    println("Hello")

    Class.forName("org.h2.Driver")
    SessionFactory.concreteFactory = Some( () =>
      Session.create(
        java.sql.DriverManager.getConnection(s"jdbc:h2:mem:test-db;DB_CLOSE_DELAY=-1", "sa", ""),
        new H2Adapter))

    transaction { AppSchema.drop; AppSchema.create; }

    val users: Users = new UserDAO

    val shouldBeNone = users.findById(CoreId.generate)
    val newBob = User(CoreId.uninitialized, "Bob", "Smith")
    val savedBob  = users.save(newBob)
    val foundBob  = users.findById(savedBob.id).get
    val updatedBob   = users.save(foundBob.copy(first = "Robert"))

    val id = users.save(Identity(CoreId.uninitialized, "bob@smith.com"))

    println(s"shouldBeNone -> $shouldBeNone")
    println(s"newBob     -> $newBob")
    println(s"savedBob   -> $savedBob")
    println(s"foundBob   -> $foundBob")
    println(s"updatedBob -> $updatedBob")
    println(s"id         -> $id")
}