package fr.composeplayer.builds.android.utils

import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.process.ExecOperations
import org.gradle.process.ExecSpec
import java.io.File
import java.util.concurrent.Executors

interface CommandScope {
  interface Environment {
    operator fun get(key: String): String?
    operator fun set(key: String, value: String?)
  }
  val env: Environment
  var command: Array<String>
  var workingDir: File?
}

fun CommandScope.Environment.applyFrom(map: Map<String, String?>) {
  for (entry in map) this[entry.key] = entry.value
}

private class ProcessBuilderScope(private val builder: ProcessBuilder) : CommandScope {

  override var workingDir: File?
    get() = builder.directory()
    set(value) { builder.directory(value) }

  override var command: Array<String>
    get() = builder.command().toTypedArray()
    set(value) {
      builder.command(
        "zsh", "-l", "-c",
        value.joinToString(" "),
      )
    }

  override val env: CommandScope.Environment = object : CommandScope.Environment {
    override fun get(key: String): String? = builder.environment()[key]
    override fun set(key: String, value: String?) {
      when {
        value == null -> builder.environment().remove(key)
        else -> builder.environment().set(key, value)
      }
    }
  }
}

fun Task.execExpectingSuccess(
  block: CommandScope.() -> Unit,
) {
  val builder = ProcessBuilder()
  ProcessBuilderScope(builder).apply(block)
  logger.lifecycle("\uD83D\uDEE0\uFE0F Executing command:")
  logger.lifecycle(builder.command().joinToString(" "))
  val process = builder.start()
  val exec = Executors.newFixedThreadPool(2)

  exec.submit {
    process.errorStream
      .bufferedReader()
      .forEachLine { logger.error(it) }
  }
  exec.submit {
    process.inputStream
      .bufferedReader()
      .forEachLine { logger.lifecycle(it) }
  }

  val resultCode = try { process.waitFor() } finally { exec.shutdownNow() }
  if (resultCode != 0) {
    throw GradleException("Command failed with result code [$resultCode]")
  }
}

fun Task.execExpectingResult(
  block: CommandScope.() -> Unit,
): String {
  val builder = ProcessBuilder()
  ProcessBuilderScope(builder).apply(block)
  logger.lifecycle("\uD83D\uDEE0\uFE0F Executing command:")
  logger.lifecycle(builder.command().joinToString(" "))
  val process = builder.start()
  val result = process.inputStream.bufferedReader().readText()
  val error = process.errorStream.bufferedReader().readText()
  val resultCode = process.waitFor()
  if (resultCode != 0) {
    logger.error(error)
    throw GradleException("Command failed with result code [$resultCode]")
  }
  return result
}

class ExecScope(
  private val execSpec: ExecSpec,
) : ExecSpec by execSpec{

  var command: Array<String>
    set(value) = setCommandLine(*value)
    get() = commandLine.orEmpty().toTypedArray()
}

fun ExecOperations.execExpectingSuccess(
  block: ExecScope.() -> Unit,
) {
  val result = exec {
    standardOutput = System.out
    errorOutput = System.err
    val scope = ExecScope(this)
    environment["PATH"] = System.getenv("PATH")
    block.invoke(scope)
  }
  if (result.exitValue != 0) throw GradleException("Exec operation failed with result code [$result]")
}