@file:Suppress("EnumEntryName")

package base

enum class AndroidABI { arm32, arm64, x86, x86_64 }

val AndroidABI.ABI_NAME: String
  get() = when (this) {
    AndroidABI.arm32 -> "armeabi-v7a"
    AndroidABI.arm64 -> "arm64-v8a"
    AndroidABI.x86 -> "x86"
    AndroidABI.x86_64 -> "x86_64"
  }

val AndroidABI.NDK_TRIPLE: String
  get() = when (this) {
    AndroidABI.arm32 -> "armv7a-linux-androideabi"
    AndroidABI.arm64 -> "aarch64-linux-android"
    AndroidABI.x86 -> "i686-linux-android"
    AndroidABI.x86_64 -> "x86_64-linux-android"
  }

val AndroidABI.MIN_API: Int get() = 21
val AndroidABI.CLANG: String get() = "$NDK_TRIPLE$MIN_API-clang"
val AndroidABI.CPP: String get() = "$NDK_TRIPLE$MIN_API-clang++"

val AndroidABI.CPU_FAMILY: String
  get() = when (this) {
    AndroidABI.arm32 -> "arm"
    AndroidABI.arm64 -> "aarch64"
    AndroidABI.x86 -> "x86"
    AndroidABI.x86_64 -> "x86_64"
  }

val AndroidABI.CMAKE_SYSTEM_PROCESSOR: String
  get() = when (this) {
    AndroidABI.arm32 -> "armv7a"
    AndroidABI.arm64 -> "aarch64"
    AndroidABI.x86 -> "x86"
    AndroidABI.x86_64 -> "x86_64"
  }

