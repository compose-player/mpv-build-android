package tasks

import base.*
import utils.*
import java.io.File

class CloneTask(private val lib: Library) : Task() {

  override val skip: Boolean
    get() = sourceDirOf(lib).exists

  override suspend fun clean() {
    sourceDirOf(lib).deleteRecursively()
  }

  override suspend fun executeInternal() {
    commandLine {
      workingDir = sourceDirOf(lib).parentFile.apply(File::mkdirs)
      command = arrayOf("git", "clone", "--depth", "1", "--branch", lib.branch, *lib.gitArgs, lib.repositoryUrl, lib.name)
    }

//    val patchesDir = this::class.java.classLoader.getResource("/patches")!!.toURI().let(::File)
//
//    val patches = patchesDir.resolve(lib.name)
//      .listFiles()
//      .orEmpty()
//
//    for (patch in patches) {
//      commandLine {
//        workingDir = sourceDirOf(lib)
//        command = arrayOf("git", "apply", patch.absolutePath)
//      }
//    }
  }

  override suspend fun onFailure(reason: Throwable) {
    sourceDirOf(lib).deleteRecursively()
  }

}