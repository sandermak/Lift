package com.infosupport
package model

import net.liftweb.mapper._
import java.math.MathContext

class Weekstaat extends LongKeyedMapper[Weekstaat] with IdPK with OneToMany[Long, Weekstaat] {
  def getSingleton = Weekstaat

  object weekNr     extends MappedInt(this)
  object definitief extends MappedBoolean(this)
  object regels     extends MappedOneToMany(WeekstaatRegel, WeekstaatRegel.weekstaat)
}

object Weekstaat extends Weekstaat with LongKeyedMetaMapper[Weekstaat] {
  override def dbTableName = "weekstaat"
}

class WeekstaatRegel extends LongKeyedMapper[WeekstaatRegel] with IdPK {
  def getSingleton = WeekstaatRegel

  object weekstaat extends LongMappedMapper(this, Weekstaat)
  object code      extends MappedString(this, 10)
  object maandag   extends MappedDecimal(this, MathContext.DECIMAL32, 1)
  object dinsdag   extends MappedDecimal(this, MathContext.DECIMAL32, 1)
  object woensdag  extends MappedDecimal(this, MathContext.DECIMAL32, 1)
  object donderdag extends MappedDecimal(this, MathContext.DECIMAL32, 1)
  object vrijdag   extends MappedDecimal(this, MathContext.DECIMAL32, 1)

}

object WeekstaatRegel extends WeekstaatRegel with LongKeyedMetaMapper[WeekstaatRegel] {
  override def dbTableName = "weekstaat_regel"
}


