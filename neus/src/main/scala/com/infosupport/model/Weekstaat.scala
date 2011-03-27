package com.infosupport
package model

import net.liftweb.mapper._
import java.math.MathContext

class Weekstaat extends LongKeyedMapper[Weekstaat] with IdPK with OneToMany[Long, Weekstaat] {
  def getSingleton = Weekstaat

  object user       extends LongMappedMapper(this, User)
  object weekNr     extends MappedInt(this)
  object definitief extends MappedBoolean(this)
  object regels     extends MappedOneToMany(WeekstaatRegel, WeekstaatRegel.weekstaat)

  def totaalMaandag = totaalVoor(_.maandag.is)
  def totaalDinsdag = totaalVoor(_.dinsdag.is)
  def totaalWoensdag = totaalVoor(_.woensdag.is)
  def totaalDonderdag = totaalVoor(_.donderdag.is)
  def totaalVrijdag = totaalVoor(_.vrijdag.is)

  private def totaalVoor(voor: WeekstaatRegel => BigDecimal) = regels.map(voor).sum.toString
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
  object report extends MappedEnum[WeekstaatRegel, Report.type](this, Report)

  def totaal = maandag.is + dinsdag.is + woensdag.is + donderdag.is + vrijdag.is

}

object WeekstaatRegel extends WeekstaatRegel with LongKeyedMetaMapper[WeekstaatRegel] {
  override def dbTableName = "weekstaat_regel"
}


