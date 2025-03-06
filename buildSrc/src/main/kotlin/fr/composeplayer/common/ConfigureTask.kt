package fr.composeplayer.common

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.File
import javax.inject.Inject

open class ConfigureTask @Inject constructor(
  @Internal val operations: ExecOperations,
) : DefaultTask() {

  @Input lateinit var platform: Platform
  @Input lateinit var dependency: Dependency
  @Input lateinit var configureArgs: List<String>

  val binariesDir: File
    @Internal get() = File(project.rootDir, "binaries/${platform.name}/${platform.buildDirName}").apply(File::mkdirs)

  @TaskAction
  fun execute() {
    val buildDir = File(project.rootDir, "builds/${platform.name}/${platform.buildDirName}/${dependency.libName}")
    if (buildDir.exists()) {
      return
    }
    buildDir.mkdirs()
    val sourceDir = File(project.rootDir, "vendor/${dependency.libName}")
    try {
      operations.execExpectingSuccess {
        workingDir = buildDir
        addExports(platform, binariesDir)
        commandLine(
          sourceDir.resolve("configure").absolutePath,
          *configureArgs.toTypedArray(),
          "--prefix=/",
        )
      }
    } catch (error: Throwable) {
      buildDir.deleteRecursively()
      throw error
    }
  }

}