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
      val computerArch = "darwin-x86_64"
      return File(ndkDir, "toolchains/llvm/prebuilt/$computerArch/bin")
    }

}