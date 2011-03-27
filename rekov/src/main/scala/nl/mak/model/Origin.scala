package nl.mak.model


object Origin extends Enumeration {
  type Origin = Value

  val GEA = Value(1, "Geldautomaat")
  val BEA = Value(2, "Betaalautomaat")
  val GIRO = Value(3, "Girobetaling")
  val OTHER = Value(4, "Onbekend")

  def apply(input: String) = input.substring(0, 4) match {
    case "GEA" => GEA
    case "BEA" => BEA
    case "GIRO" => GIRO
    case _ => OTHER
  }
}