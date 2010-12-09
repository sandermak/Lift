package test

/**
 * Congratulations if you've made it this far :) There's some cool stuff in here, but is definitely not for
 * daily use in most situations. However, many (core) library features are built with these constructs, so
 * knowing them is always beneficial.
 */

class AdvancedConstructs {

  def implicitConversions() {
    // You may have wondered about the syntax used for constructing a map:
    val map1 = Map("a" -> "b")
    val map2 = Map("a".->("b")) // equivalent
    // How can this be? Is it 'special' map syntax? Actually, it is not, it is based on a Scala feature as we will see.
    // The String has type java.lang.String (try and enter a string literal in the REPL and hit enter)
    // but still we can invoke the -> method on it, which turns the elements into a pair that is accepted by the Map.
    // But why is this possible? Answer: implicit conversions. The compiler sees that -> is invoked on a String, which
    // does not typecheck. However, the compiler then checks if there are any conversion functions *in scope* (very
    // important) that may convert the String into a type that knows the -> method. And indeed, the scala.Predef object
    // (which is automatically imported into each Scala sourcefile, just like java.lang.* is automatically imported
    // for each Java source file) contains a conversion method which turns strings (and in fact any class) into
    // an ArrowAssoc instance. ArrowAssoc defines the -> method.

    // Let's try this for ourselves. Let's we want to add a method for getting the ROT-13 value of as string:
    // "s".rot13
    // currently this of course fails to compile. Let's create the wrapper class and implicit conversion
    final class Rot13Wrapper(s: String) {
      def rot13 = {
        s.map(c => (c + 13).toChar) // By the way, the fact that we can map over a String is thanks to an implicit conversion as well...
      }
    }
    implicit def stringToRot13(s: String) = new Rot13Wrapper(s)
    // Now the following compiles and prints "nop". Also note how IntelliJ underlines the method invocation with
    // a grey line to indicate it is not defined on the class of String, but is introduced through an implicit conv.
    println("abc".rot13)

    // Note that this looks like 'monkey-patching' or open classes like you can do in dynamic languages. Only now
    // it is typesafe and enforced by the compiler. Yay!

    // People used to worry about performance (all those wrapper classes! Arrgh!), but it turns out our modern JVMs
    // can cope very well with the instantiantion of these (small and short-lived) objects at high rates.
    // Escape analysis sometimes even prevents them from being constructed at all, long live HotSpot!

    // Obviously this is a powerful feature that can be abused if you are not careful. Luckily, the conversions are
    // scoped (we are not introducing some global compiler magic). So only if you choose to bring implicit conversions
    // into scope (by defining or importing them) you allow them to be applied by the compiler. Implicit conversions
    // are great for defining DSL-like API's.
  }

  def multipleParamListAndCurrying() {
    // Observe the following definitions:
    def addSimple(i: Int, j: Int) = { i + j }
    addSimple(1,2) == 3
    // Note that we can have two parameterlists for the def:
    def addCurried(i: Int)(j: Int) = { i + j }
    // We can supply just the first parameter, and get a function with only the remaining parameter as result:
    val twoAdder = addCurried(2) _ // Has type (Int) => Int, underscore signals that remaining parameter(s) are not provided
    // Now we can use the *partially applied* function as often as we want with different remaining arguments.
    twoAdder(1) == 3
    twoAdder(2) == 4

    // Another alternative would be to make addCurried return a function directly, which is equivalent to what we did above:
    def addCurriedAlt(i: Int) = (j:Int) => i + j
    val twoAdderAlt = addCurriedAlt(2) // Note that we can omit the underscore since we are not referring to a partially applied def.

    // A bit unrelated to currying, but relevant for reading Scala: parameter lists may be enclosed in parens ()\
    // OR in curly braces {}
    twoAdder{1} == 3
    addCurried(1){ 2 }

    // This may seem a bit strange, but when we start passing around functions and blocks of code, this feature will
    // start to make sense. Stay tuned...
  }

  def controlStructures() {
    // You know how they keep talking about adding 'Automatic Resource Management' to Java? Well, in Scala we roll
    // our own using the standard features of the language. Since we have higher order functions and other niceties,
    // we can create our own control structures!

    // We first define that Closeable is any type which defines a close() method. In Scala you can use structural types for this:
    type Closeable = { def close() }
    // generic method, T must be a subtype of Closeable (i.e. it must have close() method)
    // Second parameter block accepts a function that, given the resource, executes the code.
    def using[T <: Closeable](resource: T)(code: T => Unit) = {
      try {
        code(resource)
      } finally {
        if(resource != null)
          resource.close()
      }
    }
    // Advantages: automatically closing your resources (obviously), but also: no need to retrofit some Closeable interface
    // on any resource you want to support. Just the fact that an object has a close() method is enough.

    // Example usage:
    import java.io._
    using(new FileWriter(File.createTempFile("test","txt"))) {
      stream => stream.write("Hello world!")
    }
    // Note that the closure which does the writing is the second parameter of using(), but enclosed in curly
    // braces to signify it is a block of code to be executed. Purely cosmetic since we could have used parentheses
    // as well, but this does look more visually appealling.

    // One last trick is that we can also pass a block of code to a function, where the block is not executed. A block
    // of code in this case is a parameterless closure:
    def executeLater(block: => Any) = {
      println("executing block NOW")
      block
      println("block executed")
    }

    executeLater {
      println("working...")
    } // Think about the result you expect and run it in the REPL to check


    // Just for fun let's build our own control structure :)
    // (now you can also see why Scala doesn't need a ton of them builtin to the language)
    def doWhile(work: => Any)(test: => Boolean): Any = {
      if(test) {
        work
        doWhile(work)(test)
      }
    }

    // and use it:
    var a = 0
    doWhile {
      // Code here is not executed immediately, but is passed to doWhile
      a += 1
      println("a=" + a)
    } (a < 5)
    // The same goes for the test a < 5, it is not immediately evaluated to a boolean, but passed as test to doWhile.
    // In doWhile, the test is performed, and if it evaluates to true, the work is executed and doWhile is recursively
    // called.
  }

  def actors() {
    // Actors provide an alternative concurrency model to threading+locks. It is based on asynchronous exchange
    // of messages between actors. These messages must be immutable to benefit from the actor model.

    // An actor is an object with a mailbox (in which the messages are placed) that
    // acts on each message in turn (serialized). Actors can scale to millions in a single VM, this is way beyond
    // the few thousand threads a VM can support. This scale allows for fine-grained parallelism. Actors are executed
    // using the fork-join framework, usually with the backing of a threadpool.

    object ChatExampleSandbox {
      // As an example, let's model a chatserver and clients. First we define the messages that can be passed around:
      case class ChatMessage(text: String)
      case class Register(c: ChatClient)

      import actors.Actor
      import collection.mutable.ListBuffer

      object ChatServer extends Actor {
        // An actor may have mutable internal state
        private var clients: ListBuffer[ChatClient] = new ListBuffer()

        // But it only acts on immutable messages from it's inbox. The following construct loops forever:
        def act() {
          loop {
            react {
              case Register(client)    => clients += client             // add the client to our list
              case m@ChatMessage(text) => clients.foreach(_ ! m)        // send the message to all clients
              case _                   => println("Unknown message!")   // anything could come in, trap unknown messages
            }
          }
        }
      }

      // ChatClient is a class since we can have many clients. The current implementation just prints messages
      // as they arrive from the ChatServer.
      class ChatClient(name: String) extends Actor {
        // First, register with the server. The ! operator sends the message async.
        ChatServer ! Register(this)

        def act() {
          loop {
            react {
              case ChatMessage(text) => println("[" + name + "] Received message: " + text + " @ " + System.currentTimeMillis)
            }
          }
        }
      }

      val chatClient1 = new ChatClient("client1")
      val chatClient2 = new ChatClient("client2")
    }

    // now use our example:
    import ChatExampleSandbox._
    // Actors must be started before they process messages
    ChatServer.start
    chatClient1.start
    chatClient2.start
    ChatServer ! ChatMessage("test")

    // see http://www.scala-lang.org/node/242 for a more complete introduction
  }

}
