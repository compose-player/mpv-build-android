package fr.composeplayer.common

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.File
import javax.inject.Inject

open class AutoGenTask @Inject constructor(
  @Internal val operations: ExecOperations,
) : DefaultTask() {

  @Input
  lateinit var dependency: Dependency

  @TaskAction
  fun execute() {
    val sourceDir = File(project.rootDir, "vendor/${dependency.libName}")
    val configureScript = File(sourceDir, "configure")
    if ( !configureScript.exists() ) {
      operations.execExpectingSuccess {
        workingDir = sourceDir
        commandLine("./autogen.sh")
      }
    }
  }

}