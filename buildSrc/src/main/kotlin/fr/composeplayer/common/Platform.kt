package fr.composeplayer.common

import java.io.File


sealed interface Platform {

  val name: String
    get() = when (this) {
      is Android -> "android"
    }

  val buildDirName: String
    get() = when (this) {
      is Android -> this.abi.abiName
    }

  data class Android(val abi: AndroidAbi): Platform

  val CXX: String
    get() = when (this) {
      is Android -> abi.cpp
    }

  val CC: String
    get() = when (this) {
      is Android -> abi.clang
    }

  val ndkDir: File
    get() {
      val hasEnv = System.getenv("ANDROID_NDK_HOME").isNotBlank()
      return when {
        hasEnv -> System.getenv("ANDROID_NDK_HOME").let(::File)
        else -> {
          val userName = System.getProperty("user.name")
          val ndks = File("/Users/${userName}/Library/Android/sdk", "ndk")
          return ndks.listFiles()!!.first(File::isDirectory)
        }
      }
    }

  val toolsChainDir: File
    get() {
      return File(ndkDir, "toolchains/llvm/prebuilt/${ArchDetector.arch}/bin")
    }

}

object ArchDetector {

  val arch: String
    get() {
      val os = System.getProperty("os.name").lowercase()
      val arch = System.getProperty("os.arch").lowercase()

      return when {
        os.contains("mac") -> "darwin-x86_64"
        os.contains("win") -> error("")
        else -> when {
          arch.contains("amd64") || arch.contains("x86_64") -> "linux-x86_64"
          arch.contains("aarch64") || arch.contains("arm64") -> "linux-aarch64"
          arch.contains("arm") -> "linux-arm"
          arch.contains("i386") || arch.contains("x86") -> "linux-x86"
          else -> "linux-unknown"
        }
      }

    }


}