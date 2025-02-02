package fr.composeplayer.common

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.File
import javax.inject.Inject

open class MakeTask @Inject constructor(
  @Internal val operations: ExecOperations,
) : DefaultTask() {

  @Input lateinit var dependency: Dependency
  @Input lateinit var platform: Platform
  @Input var clean = false

  @TaskAction
  fun execute() {
    val buildDir = File(project.rootDir, "builds/${platform.buildDirName}/${dependency.libName}")
    val binariesDir = File(project.rootDir, "binaries/${platform.buildDirName}").apply(File::mkdirs)

    require( buildDir.exists() ) { "Build dir does not exists, did ConfigureTask ran before ?" }

    try {
      operations.execExpectingSuccess {
        workingDir = buildDir
        addExports(platform, binariesDir)
        commandLine("make", "-j4")
      }
      operations.execExpectingSuccess {
        workingDir = buildDir
        addExports(platform, binariesDir)
        commandLine("make", "DESTDIR=${binariesDir.absolutePath}", "install")
      }
    } catch (error: Throwable) {
      buildDir.deleteRecursively()
      throw error
    }

  }

}