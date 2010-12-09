package test

class IntermediateConstructs  {

  def caseClasses() {
    // Case classes are immutable classes containing values that are only readable
    case class Person(name: String, age: Int)

    // Construct a Person without the new keyword
    val sander = Person("Sander", Int.MaxValue)
    // We can do this because under the hood Scala creates a Person class and a companion
    // object which has an apply function that takes the values of the fields. I.e. this works too:
    val sander2 = Person.apply("Sander2", Int.MinValue)
    // Furthermore, Scala generates an equals, toString and hashcode method based on the fields for the case class:
    sander.toString == "Person(Sander,2147483647)"

    // We can refer to the fields, but we cannot reassign them.
    println(sander.name + "=" + sander.age)
    // illegal: sander.name = "new name"

    // We can relax the immutability by defining the fields as var:
    case class MutablePerson(var name: String, var age: Int)
    val test = MutablePerson("name", 101)
    test.age += 1

    // In effect, case classes are lightweight syntax for POJOs! We can even make them compatible with the Java
    // getter/setter idiom, if necessary, using an annotation:
    import scala.reflect.BeanProperty
    case class BeanPerson(@BeanProperty var name: String, @BeanProperty var age: Int)
    val bean = new BeanPerson("Mr. Bean", 55)
    bean.setName("Mr. ScalaBean")
    bean.getName() == "Mr. ScalaBean"

  }

  // We can create a *sealed* hierarchy of case classes. This means Message cannot be extended anywhere but in this
  // source file.
  sealed abstract class Message
  case class EmailMessage(text: String, to: String, cc: List[String]) extends Message
  case class SMSMessage(text: String, phonenumber: String) extends Message

  def patternMatching() {
    // A very powerful feature of Scala is pattern matching. You could call it a sort of switch on steroids.
    val x = "test"

    // We match a value based on its type, and have a catch-all (with underscore) if type doesn't match
    x match {
      case s: String => println("String: " + s)
      case _         => println("not a String")
    }

    // Of course just matching on types wouldn't be very exciting. However, we can combine case classes and pattern matching.
    // This gives us both a test for the correct value, and the extraction of the information we are interested in in a
    // single step.
    val email: Message = SMSMessage("what's up?", "00313234231235")
    val text = email match {
      case EmailMessage(text, _, _) => "Email: " + text
      case SMSMessage(text, _)      => "SMS: " + text
    }
    text == "SMS: what's up?"
    // In this example, we extracted the text from a message, based on whether it's Email or SMS message. The identifiers used
    // in a pattern match (e.g. text in EmailMessage(text, _, _) are bound and can be used in the righthandside of the case.
    // Underscores in a pattern mean 'this can be any value, we don't care'.
    // Since Message is a sealed class, the compiler knows that EmailMessage and SMSMessage form the complete inheritance
    // hierarchy. Therefore, if we would omit the SMSMessage case in the previous example, the compiler would warn about
    // a 'non-exhaustive' pattern matching! This is a potential runtime error, so it is nice that the compiler tells us
    // this. However, you can suppress this error if you know that at runtime SMSMessage instances are never passed into
    // the match statement.

    // The nice thing is that List is a case class too. And patterns matches may be nested. So let's try to extract text
    // only for email messages with a single cc address. Just for fun, we'll encapsulate the matching in a function so
    // we can reuse it.

    val matchFunction = (msg:Message) => msg match {
      case EmailMessage(text, _, List(cc)) => text + " cc'ed to: " + cc
      case SMSMessage(text, _)             => text
      case _                               => "No match!"
    }

    // Use our matching function (IntelliJ flags this as error, but it is actually correct... Try it in the REPL!)
    val message1 = EmailMessage("This won't match ", "me@me.com", Nil)
    matchFunction(message1) == "No match!"
    val message2 = EmailMessage("But this will", "me@me.com", List("cc@me.com"))
    matchFunction(message2) == "But this will cc'ed to: cc@me.com"
    val message3 = SMSMessage("text", "0213123")
    matchFunction(message3) == "text"

  }

  def optionType() {
    // Though not really a language feature (it is defined in the core Scala libs), the Option type is a really
    // useful feature. It is used in Scala to eradicate nullpointer exceptions. Rather than returning null if
    // no answer can be given, and having your clients check for null (if they don't forget it...), you can encode
    // the optionality of a value in the type.

    // First, let's reproduce how Option is (approx.) declared as case class in the Scala libs
    // In order not to override the library implementation, the definition is given in comments:
    //    sealed abstract class Option[+A]
    //    case class Some[A](something: A)
    //    case class None[Nothing]
    // So either we have a value (wrapped in Some) or no value (as indicated by None)

    // First example. A map in Scala returns an option from its get method:
    val map = Map("a" -> List("a", "b"),
                  "b" -> List("b", "c"),
                  "c" -> List("c", "d"))

    map.get("a") == Some(List("a", "b"))
    map.get("z") == None

    // We could use a pattern match to extract the value and give a default if nothing is found:
    map.get("a") match {
      case Some(list) => list
      case None       => List()
    }
    // However since that's a common usecase, this is also a convenience function on Option:
    map.get("a").getOrElse(List()) == List("a", "b")
    map.get("z").getOrElse(List()) == List()

    // You could also use an unsafe get without default, but that brings you right back to exception-land... so not recommended:
    map.get("a").get == List("a", "b")
    map.get("z").get // Ooops! NoSuchElementException from the None option value!

    // A last nice touch is that Option implements the methods necessary for use in a for-comprehension. Naturally, an
    // option always produces a single value or no value. Simplest example, which prints the result of the get action,
    // or nothing if the get call results in a none:
    for (listValue <- map.get("a")) {
      println(listValue)
    }
    // To be exact, the println statement is never executed if the map.get would result in a None value.

    // Example of multiple generators in a for-comprehension, including the option from before:
    for (listValue <- map.get("a");      // Get the value from the map, results in: List("a", "b")
         elem      <- listValue;         // Iterate over each elem in the list, results in elem = "a" en elem = "b" for the previous list
         char      <- elem.lastOption) { // elem.lastOption returns an option containing the last character, or None if the string is empty
      println("Char: " + char)
    }
    // Try to find out what this should print, then verify it in the REPL.
    // Then also note that the code does not crash when the key is not found in the map, or if the returned list from
    // map lookup is empty, or if the strings in the list do not have a last character. It all nicely composes.

    // The last example clearly shows the power of combining the features of Scala. Think of the many nested null checks
    // you'd otherwise have to write, to chain these operations in such a robust way.

  }

  def traits() {
    // Last topic for the intermediate constructs: Traits (also called mixins by some). What are traits? You can think
    // of them as being similar to Java interfaces. But, the difference is that traits may provide default implementations
    // for methods. This gives Scala a limited form of multiple inheritance. Traits themselves cannot be instantiated
    // directly.

    // First let's use traits just like we would use Java interfaces, with just abstract methods that must be implemented later:
    trait Employee { def doWork }
    trait ScalaEnthusiast { def doWorkInFreeTime }
    class Person extends Employee with ScalaEnthusiast {
      def doWork() = { println("Working") }
      def doWorkInFreeTime() = { println("Working even harder!") }
    }

    // Traits can define an interface, while *also* providing default implementations of methods based on this interface.
    // This behavior is analogue to abstract classes in Java. However, concrete classes may mixin multiple traits, whereas
    // in Java you can only extend from a single (abstract) class. This use of traits is called the 'Rich interface' pattern
    // Example taken from Scala standard library:
    trait Ordered[A] extends java.lang.Comparable[A] {

      /** Result of comparing <code>this</code> with operand <code>that</code>.
       *  returns <code>x</code> where
       *  <code>x &lt; 0</code>    iff    <code>this &lt; that</code>
       *  <code>x == 0</code>   iff    <code>this == that</code>
       *  <code>x &gt; 0</code>    iff    <code>this &gt; that</code>
       */
      def compare(that: A): Int

      def <  (that: A): Boolean = (this compare that) <  0
      def >  (that: A): Boolean = (this compare that) >  0
      def <= (that: A): Boolean = (this compare that) <= 0
      def >= (that: A): Boolean = (this compare that) >= 0
      def compareTo(that: A): Int = compare(that)
    }

    // Mixin the Ordered trait with a case class, and order on the age field
    case class OrderedPerson(name: String, age: Int) extends Ordered[OrderedPerson] {
      def compare(that: OrderedPerson) = this.age compare that.age // Just use compare method of the Int
    }
    val person1 = OrderedPerson("old", 101)
    val person2 = OrderedPerson("you", 1)
    person1 > person2 == true
    person1 <= person2 == false

    // Another usecase for traits is to see them as stackable modifications. The essential point is that a trait
    // may use a super call, even though it is not in advance clear where this trait is being mixed in. This means
    // that super calls are dynamically/late-bound. An example is in order:
    trait IgnoreCaseSet extends java.util.Set[String] {
      abstract override def add(e: String) = { super.add(e.toLowerCase) }
      // Curiously, the Set is not generic for the following methods:
      import java.lang.Object
      abstract override def contains(e: Object) = { super.contains(e.asInstanceOf[String].toLowerCase) }
      abstract override def remove(e: Object) = { super.remove(e.asInstanceOf[String].toLowerCase) }
    }

    // Now mixin the behavior on top of Java's HashSet
    import java.util.HashSet
    class IgnoreCaseSetImpl extends HashSet[String] with IgnoreCaseSet
    val set = new IgnoreCaseSetImpl()
    set.add("Aha!")
    set.contains("aha!") == true

    // We can even mixin the IgnoreCaseSet at instantiation time rather than defining a class like we did above:
    val adhocSet = new HashSet[String] with IgnoreCaseSet
    adhocSet.add("I SEE...")
    adhocSet.remove("i see...")
    adhocSet.isEmpty == true

    // Let's create another trait that adds behavior to a set.
    trait LoggingSet extends java.util.Set[String] {
      abstract override def add(e: String) = { println("Adding: " + e); super.add(e) }
      import java.lang.Object
      abstract override def remove(e: Object) = { println("Removing: " + e); super.remove(e) }
    }

    // Now we mixin both traits
    val compositeSet = new HashSet[String] with LoggingSet with IgnoreCaseSet
    compositeSet.add("This is logged and then added to the set")
    // The order of mixing in is significant, since both traits override the same methods. The super calls follow
    // the order as given when mixing in. So, for an implementation which adds the value and then logs it:
    val otherWayAroundSet = new HashSet[String] with IgnoreCaseSet with LoggingSet

    // As bonus, traits can have so-called 'self-types'. There you can explicitly set the type of the object we expect
    // the trait to be mixed in with.
    trait ASelfTypedTrait {
      this: OrderedPerson =>

      def onlyOnPersons = { println("I'm a person with age " + this.age)}
    }
    val testPerson = new OrderedPerson("p", 12) with ASelfTypedTrait
    testPerson.onlyOnPersons == "I'm a person with age 12"
    // The self type ensures that the trait can only be mixed in with a class/object that has type Set[String]

    // This feature makes it possible to use traits for dependency injection (think about it for a second). A
    // nice description of how this works @ http://jonasboner.com/2008/10/06/real-world-scala-dependency-injection-di.html

  }

}