package fr.composeplayer.common

import java.io.File

sealed interface Platform {
  data class Android(val abi: AndroidAbi): Platform

  val buildDirName: String
    get() = when (this) {
      is Android -> when (this.abi) {
        AndroidAbi.Arm32 -> "android-armeabi-v7a"
        AndroidAbi.Arm64 -> "android-arm64-v8a"
        AndroidAbi.X86 -> "android-x86"
        AndroidAbi.X86_64 -> "android-x86_64"
      }
    }

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
      val hasEnv = System.getProperty("ANDROID_NDK_HOME", "").isNotBlank()
      return when {
        hasEnv -> System.getProperty("ANDROID_NDK_HOME").let(::File)
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