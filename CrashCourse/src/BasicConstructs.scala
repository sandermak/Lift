package test

/**
 * Crash course Scala syntax and semantics
 */

class BasicConstructs {

  def basicConstructs() = {
    // Variable declaration, identifier: Type = expression
    var int: Int = 1;
    // Types are optional, local type inference takes over if types are absent. int and inferredInt have the same type!
    var inferredInt = 1;
    // Immutable variables, cannot re-assign (compare with final in Java)
    // Also: ; is optional
    val immutableInt = 1
    // Ofcourse we have if-then-else as well, although it is an expression rather than a statement in Scala. So we
    // can assign it directly to a variable. Similar to the ternary operator in Java: test ? "yes" : "no"
    val value = if(inferredInt == 1) "yes" else "no"

    // Construct lists directly (by default Scala provides immutable datastructures)
    val list1 = List("a", "b", "c")
    // Therefore, adding an element will result in a *new* list with the added element.
    val list2 = list1.+("d")
    // Note how the previous example used an operator ('+') as method name. Scala allows this, and also allows you to
    // write the operator (or method name) infix. So remember: methods with a single parameter can be used infix.
    val list3 = list1 + "d"

    // Ah, Java interop. Just import the class and use it. It's that simple.
    java.lang.Math.max(1,2)
    // Oh yeah, you can import at any level. Keep your namespaces clean!
    import java.util._
    val mutableJavaList = new ArrayList[String]()
    mutableJavaList.add("changed_the_list")
  }

  def classesAndObjects() {
    // In Scala we can have classes and objects. Here we define a class (notice how we can nest it in our current
    // method, in Scala almost anything can be nested):
    class MyClass {
      // classes can have fields, public by default. However, access happens through accessor/mutator methods (which
      // are implicit if we do not provide them like in this example). Therefore, field access and method calls are
      // essentially equivalent!
      val field = "field"

      // private also possible
      private val privateField = "don't touch!"

      // You must always provide the types of method parameters, but the return type can be inferred and is therefore optional
      def method(param: Int) = { param + 1 }
    }

    // Using a class, unsurprisingly requires new:
    new MyClass().method(1)

    // This looks like Java field access, but rather you are calling the accessor method, which could be provided by
    // the class. For example if some validation beyond mere getting/setting is necessary.
    var fieldValue = new MyClass().field

    // We can also define objects. Objects are *singletons*, i.e. there is only a single instance for a *given scope*.
    // This means if we define an object toplevel, there is only one instance per JVM. However, if we define it nested
    // in this method, it is a singleton with respect to each execution this method.
    object MyObject {
      def someMethod() = { println("hi there from MyObject!") }
    }

    // No instantiation required to use the singleton object. Also note that () are optional for no-arg method calls.
    // This forms the basis for the uniform access principle: method calls and field accesses can be switched transparently.
    MyObject.someMethod

    // If we define an object with the same name as a class in the same sourcefile, the object gains some special privileges.
    // It becomes a so-called companion object to a class. This pattern is mostly used for builder-like constructs. For
    // example, in line 22 we used the List object to construct an instance of the List class!
    object MyClass {
      // A companion object has access to the private members of the companion class
      val secretInfo = new MyClass().privateField
    }
  }

  def functions() : Unit = {
    // We can define anonymous functions/lambdas and assign them to a variable. Again, the type on the val is optional.
    // We provided it here for clarity. A simple single parameter function that adds one to the integer parameter.
    val function: Int => Int = (x:Int) => x + 1

    // Use the stored anonymous function:
    function.apply(1) == 2 // Apply is the default method to invoke an anonymous function
    function(2) == 3 // Since you would use it all the time, you can omit it.

    // Actually, anonymous functions are just objects! The following definition is equivalent, and in fact the compiler
    // translates our previous function variable to such an instance under the hood:
    val functionObject = new Function1[Int, Int] {
      def apply(x: Int) = { x + 1}
    }

    // Usage is the same, but the apply might make more sense now:
    functionObject.apply(1) == 2
    functionObject(2) == 3

    // Since functions are first-class, we can just pass them around! Let's define a method which accepts our function:
    def adder(f: Int => Int, value: Int): Int = {
      // We apply the function that is passed in (f) to our value that was passed in (value). Notice we do not need a
      // return statement: the last expression in a method is its return value.
      f(value)
    }

    // Now use our fresh added method:
    adder(function, 1) == 2
    // And to stress the equivalence between anonymous functions and function objects, this works exactly the same:
    adder(functionObject, 2) == 3

    // Last, we can use existing methods as functions, too. We capture a reference to the method we are currently in:
    val refExistingMethod : () => Unit = functions _
    // The underscore is used to differentiate between calling the function (since it has zero parameters, and the
    // parenthesis are optional for calling zero-arg functions) and referring to it as a method. Now we can invoke it,
    // however if we would run this this would ofcourse loop until a stackoverflow is hit...
    refExistingMethod()

  }

  def higherOrderFunctions() = {
    // We introduced methods/functions that accept functions. Welcome to the core of functional programming! This concept
    // is also called higher order functions (HOFs). Of course we can have much more fun with them than the simple adder-example
    // we saw. Let's turn to list manipulation, on of the strongest examples HOFs that are practical in day-to-day use:
    val list1 = List("green", "blue", "red", "yellow", "brown")

    // Now let's say we want all colors that start with a "b". We pass an evaluation function to filter, which keeps
    // all elements that evaluate to true:
    val bList1 = list1.filter((x: String) => x.startsWith("b"))
    // By the way, == behaves in Scala as equals would in Java, and is *not* reference comparison. This evaluates to true.
    bList1 == List("blue", "brown")

    // We can do even better, the type of x can be inferred since the compiler knows we have a List[String]:
    val bList2 = list1.filter(x => x.startsWith("b"))
    // Almost there now. If a parameter is used only once in the expression, we can omit the parameter declaration
    // and avoid naming it by using the underscore notation. Naming that parameter was awkward anyway, right?
    val bList3 = list1.filter(_.startsWith("b"))

    // Finally, let's exercise some mindboggling functional programming magic:
    val intList = List(1,2,3,4,5)
    val longerSum = intList.foldLeft(0)((x,y) => x + y)
    // By the way, as you can see you can use multiple underscores if each parameter is used only once in the expression, i.e.:
    val sum = intList.foldLeft(0)(_ + _)
    sum == 15

    // The foldleft is a recursive function which applies a binary operator (+ in this case) to all elements of the
    // list and to a starting value (0 in this case). This yields the sum of all values in the list. So, effectively,
    // this computation 'unfolds' to:
    val manualSum = (((((0 + 1) + 2) + 3) + 4) + 5)

    // Just to show how generic the foldLeft function is, let's do the product of the list instead.
    val product = intList.foldLeft(1)(_ * _)
    product == 120
  }

  def xmlInTheLanguage() {
    // In hindsight Martin Odersky, the creator of Scala, found it a bit of a mistake to include XML literals in the
    // the language. However, they are not going anywhere anytime soon, since that would break backwards compatibility
    // in a major way. So, might as well use it!

    // Notice that we do not have to have a single root tag. The result type of an XML literal expression is always
    // NodeSeq, i.e. a sequence of nodes. Also, the Scala compiler enforces the well-formedness of the XML! (Try and
    // change it to invalid XML, and IntelliJ will complain.
    val someXML = <tag><nestedtag id="ntag1"/></tag><tag><nestedtag id="ntag2"/></tag>

    // The NodeSeq type has some useful methods on it, which provide xpath like operations. The following expression
    // returns arbitrarily nested <nestedtag /> instances. In this case, nestedTags will contain two tags.
    val nestedTags = someXML \\ "nestedtag"

    // Note that you can escape to Scala again by using curly braces, to create dynamic structures:
    val id = "some dynamically calculated value"
    val idXml = <tag>{id}</tag>
  }

  def theForLoop() {
    // Scala has two looping constructs: for and while. The while construct is similar to while in Java, a low-level
    // looping construct. However, the for loop is quite a bit more advanced. Let's build it up step-by-step.

    // So far, nothing strange, looks like Java's enhanced-for-loop. Only in Scala we call it a for-comprehension
    for(i <- List(1, 2, 3, 4)) {
      println("Value: " + i)
    }

    // But, we can have multiple *generators* in the for clause. In this case, this loop will print all combinations of
    // the two lists (i.e. (1,1) (1,2) (1,3) (1,4) (2,1) (2,2) (2,3) (2,4) (3,1) (3,2) (3,3) (3,4) (4,1) (4,2) (4,3) (4,4))
    for(i <- List(1, 2, 3, 4);
        j <- List(1, 2, 3, 4)
       ) {
      println("(" + i + "," + j +")")
    }

    // Unlike Java's for-loops, in Scala for-loops are expressions (i.e. they return a value). Let's rewrite the previous
    // loop to not print the values, but rather but them as tuples in a list, using the yield keyword:
    val tupledList = for(i <- List(1, 2, 3, 4);
                         j <- List(1, 2, 3, 4)
                        ) yield (i,j)
    tupledList == List((1,1), (1,2), (1,3), (1,4), (2,1), (2,2), (2,3), (2,4), (3,1), (3,2), (3,3), (3,4), (4,1), (4,2), (4,3), (4,4))

    // We can also impose guards on the generators, using the if construct.
    val evenNumbers = for(i <- List(1, 2, 3, 4) if i % 2 == 0) yield i
    evenNumbers == List(2, 4)

    // It might seem like we can only use lists in a for-comprehension. Fortunately, that is not the case! Every
    // type that supports the higher-order-functions map, filter and flatMap can be used in the for-comprehension.
    // For example, the NodeSeq type which we encountered in the XML examples implements these. So let's try that.

    // The idea is to load two Maven pom.xml files, and report identical dependencies (with possibly different versions)
    // in the files. Think a bit how you would code this in Java. Now let's check out the Scala implementation
    // using XML features and the for-comprehension:
    val pom1 = xml.XML.loadFile("pom1.xml")
    val pom2 = xml.XML.loadFile("pom2.xml") // These files don't actually exist, but this is how you would load them

    for {
        dep1 <- pom1 \ "dependencies" \ "dependency"
        dep2 <- pom2 \ "dependencies" \ "dependency"
          if (dep1 \ "groupId") == (dep2 \ "groupId")
          if (dep1 \ "artifactId") == (dep2 \ "artifactId")
    } {
      println("groupId: " + dep1 \ "groupId")
      println("artifactId: " + dep1 \ "artifactId")
      println("versions: " + (dep1 \ "version" text) + ", " + (dep2 \ "version" text))
    }

  }
}