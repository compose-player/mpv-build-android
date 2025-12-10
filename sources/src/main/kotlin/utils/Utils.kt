package utils

import base.*
import tasks.Task
import java.io.File

val File.exists: Boolean get() = exists()

fun sourceDirOf(library: Library): File = Task.productsDir.resolve("sources/${library.name}")

fun intermediatesDirOf(library: Library, target: AndroidABI): File = Task.productsDir.resolve("intermediates/${target.ABI_NAME}/${library.name}")

fun prefixDirectoryOf(library: Library, target: AndroidABI): File = Task.productsDir.resolve("binaries/${target.ABI_NAME}/${library.name}")

val NDK_DIRECTORY: File
  get() {
    val path = System.getenv("ANDROID_NDK_HOME")
    return when {
      path.isNullOrBlank() -> {
        val user = System.getProperty("user.name")
        File("/Users/$user/Library/Android/sdk/ndk/29.0.14206865")
      }
      else -> File(path)
    }
  }

val TOOLCHAINS_DIR: File
  get() = NDK_DIRECTORY.resolve("toolchains/llvm/prebuilt/${WorkingMachine.arch}/bin")

