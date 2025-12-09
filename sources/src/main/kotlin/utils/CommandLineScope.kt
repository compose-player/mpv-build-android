package utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File

interface CommandLineScope {
  interface Environment {
    operator fun get(key: String): String?
    operator fun set(key: String, value: String?)
  }
  val env: Environment
  var command: Array<String>
  var workingDir: File
  var expectSuccess: Boolean
}

fun CommandLineScope.Environment.applyFrom(values: Map<String, String>) {
  for (entry in values) this[entry.key] = entry.value
}

suspend fun commandLine(block: CommandLineScope.() -> Unit): Int {
  val builder = ProcessBuilder()
  val scope = object : CommandLineScope {
    override var expectSuccess: Boolean = true
    override var workingDir: File
      get() = builder.directory()
      set(value) { builder.directory(value) }

    override var command: Array<String>
      get() = builder.command().orEmpty().toTypedArray()
      set(value) { builder.command(*value) }

    override val env: CommandLineScope.Environment = object : CommandLineScope.Environment {
      override fun get(key: String): String? = builder.environment()[key]
      override fun set(key: String, value: String?) {
        if (value == null) builder.environment().remove(key) else builder.environment()[key] = value
      }
    }
    init {
      val userDir = System.getProperty("user.dir").let(::File)
      builder.directory(userDir)
    }
  }

  block.invoke(scope)

  val resultCode = coroutineScope {
    val process = builder.start()
    launch(Dispatchers.IO) { process.inputStream.bufferedReader().lineSequence().forEach(System.out::println) }
    launch(Dispatchers.IO) { process.errorStream.bufferedReader().lineSequence().forEach(System.err::println) }
    process.waitFor()
  }

  if (scope.expectSuccess && resultCode != 0) {
    throw IllegalStateException("Command execution failed: $resultCode")
  }

  return resultCode

}