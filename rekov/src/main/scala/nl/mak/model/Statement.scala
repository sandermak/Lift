package nl.mak.model

import net.liftweb.mapper._
import java.math.MathContext

class Statement extends LongKeyedMapper[Statement] with IdPK with OneToMany[Long, Statement] {
  def getSingleton = Statement

  object importFilename extends MappedString(this, 255)

  object importDate extends MappedDateTime(this)

  object transactions extends MappedOneToMany(Transaction, Transaction.statement)

}

object Statement extends Statement with LongKeyedMetaMapper[Statement] {

}

class Transaction extends LongKeyedMapper[Transaction] with IdPK {
  def getSingleton = Transaction

  object statement extends LongMappedMapper(this, Statement)

  object account extends MappedString(this, 14)

  object currency extends MappedString(this, 3)

  object date extends MappedDate(this)

  object amount extends MappedDecimal(this, MathContext.DECIMAL32, 2)

  object origin extends MappedEnum(this, Origin)

  object originalDescr extends MappedString(this, 255)

  object additionalDescr extends MappedText(this)

  object hash extends MappedString(this, 16)

}

object Transaction extends Transaction with LongKeyedMetaMapper[Transaction] {


  override def dbIndexes = UniqueIndex(hash) :: Nil

  import nl.mak.service.StatementParser.ParsedTransaction

  implicit def fromParsedTransaction(tx: ParsedTransaction) = {
    val hash = tx.hashCode.toString
    new Transaction().account(tx.account).currency(tx.currency).date(tx.date).amount(tx.amount).origin(tx.origin).originalDescr(tx.descr).hash(hash)
  }
}