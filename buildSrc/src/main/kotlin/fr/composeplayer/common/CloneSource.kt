package fr.composeplayer.common

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.File
import javax.inject.Inject

open class CloneSource @Inject constructor(
  @Internal val ops: ExecOperations,
) : DefaultTask() {

  @Input lateinit var dependency: Dependency

  @TaskAction
  fun execute() {
    val vendorDir = File(project.rootDir, "vendor").apply(File::mkdirs)
    val exists = File(vendorDir, dependency.libName).exists()
    if (exists) return
    ops.execExpectingSuccess {
      workingDir = vendorDir
      commandLine(
        "git",
        "clone",
        "--depth",
        "1",
        "--branch",
        dependency.branch,
        *dependency.gitArgs.toTypedArray(),
        dependency.gitUrl,
        dependency.libName,
      )
    }
  }

}