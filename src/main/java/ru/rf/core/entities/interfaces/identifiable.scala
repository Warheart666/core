package ru.rf.core.entities.interfaces

import javax.persistence.MappedSuperclass

@MappedSuperclass
trait identifiable {
  def getId: Long
}
