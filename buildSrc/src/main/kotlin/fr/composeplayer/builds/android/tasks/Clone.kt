package fr.composeplayer.builds.android.tasks

import fr.composeplayer.builds.android.build.Component
import fr.composeplayer.builds.android.utils.execExpectingSuccess
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.File
import javax.inject.Inject

abstract class Clone @Inject constructor(
  private val operations: ExecOperations,
) : DefaultTask() {


  @Input lateinit var dirName: String
  @Input lateinit var url: String
  @Input lateinit var branch: String
  @Input lateinit var gitArgs: Array<String>

  @TaskAction
  fun execute() {
    val vendorDir = project.rootDir.resolve("vendor").apply(File::mkdirs)
    try {
      val exists = vendorDir.resolve(dirName).exists()
      if (exists) {
        logger.lifecycle("Skipping clone for component [$dirName]")
        return
      }
      logger.lifecycle("Cloning component [$dirName]")
      operations.execExpectingSuccess {
        workingDir = vendorDir
        command = arrayOf("git", "clone", "--depth", "1", "--branch", branch, *gitArgs, url, dirName)
      }
      val patchDir = project.rootDir.resolve("patches/$dirName")
      if ( !patchDir.exists() ) {
        return
      }
      val patches = patchDir.listFiles()!!
      for (patch in patches) {
        if (patch.extension != "patch") continue
        logger.lifecycle("Adding patch $patch to component [$dirName]")
        execExpectingSuccess {
          workingDir = vendorDir.resolve(dirName)
          command = arrayOf("git", "apply", patch.absolutePath)
        }
      }
    } catch (error: Throwable) {
      vendorDir.resolve(dirName).deleteRecursively()
      if (error is GradleException) throw error else throw GradleException("Failed to clone $dirName", error)
    }

  }


}

fun Clone.applyFrom(dep: Component) {
  this.dirName = dep.name
  this.url = dep.gitUrl
  this.branch = dep.branch
  this.gitArgs = dep.gitArgs
}