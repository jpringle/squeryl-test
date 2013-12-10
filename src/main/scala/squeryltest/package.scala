package squeryltest

import java.util.UUID

object IdUtils {

  val uninitializedId: UUID = new UUID(0, 0)

  def idFromString(s: String): UUID = if (s.isEmpty) uninitializedId else UUID.fromString(s)

  def generateId: UUID = UUID.randomUUID

  def isUninitialized(id: UUID): Boolean = id == uninitializedId
}

