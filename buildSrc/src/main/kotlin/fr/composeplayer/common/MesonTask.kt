package fr.composeplayer.common

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.File
import javax.inject.Inject

open class MesonTask @Inject constructor(
  @Internal val operations: ExecOperations,
) : DefaultTask() {

  @Input lateinit var dependency: Dependency
  @Input lateinit var platform: Platform
  @Input lateinit var mesonArgs: List<String>

  val sourceDir: File
    @Internal get() = File(project.rootDir, "vendor/${dependency.libName}")

  val buildDir: File
    @Internal get() = File(project.rootDir, "builds/${platform.buildDirName}/${dependency.libName}")

  val binariesDir: File
    @Internal get() = File(project.rootDir, "binaries/${platform.buildDirName}").apply(File::mkdirs)

  @TaskAction
  fun execute() {
    if (buildDir.exists()) return
    buildDir.mkdirs()
    try {
      operations.execExpectingSuccess {
        workingDir = sourceDir
        addExports(platform, binariesDir)
        environment["CC"] = null
        environment["CXX"] = null
        environment["DESTDIR"] = null
        commandLine(
          "meson", "setup",
          buildDir.absolutePath,
          "--cross-file", crossFile.absolutePath,
          "-Dprefix=/",
          *mesonArgs.toTypedArray()
        )
      }
      operations.execExpectingSuccess {
        workingDir = sourceDir
        addExports(platform, binariesDir)
        commandLine("ninja", "-C", buildDir.absolutePath, "-j${Runtime.getRuntime().availableProcessors()}")
      }
      operations.execExpectingSuccess {
        workingDir = sourceDir
        addExports(platform, binariesDir)
        environment["DESTDIR"] = binariesDir
        commandLine("ninja", "-C", buildDir.absolutePath, "install")
      }
    } catch (error: Throwable) {
      buildDir.deleteRecursively()
      throw error
    }


  }

}

