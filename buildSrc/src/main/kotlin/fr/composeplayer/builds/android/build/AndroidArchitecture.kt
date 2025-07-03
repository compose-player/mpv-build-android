package fr.composeplayer.builds.android.build

enum class AndroidArchitecture {

  Arm32, Arm64, X86, X86_64;

  val abiName: String
    get() = when (this) {
      Arm32 -> "armeabi-v7a"
      Arm64 -> "arm64-v8a"
      X86 -> "x86"
      X86_64 -> "x86_64"
    }

  val ndkTriple: String
    get() = when (this) {
      Arm32 -> "armv7a-linux-androideabi"
      Arm64 -> "aarch64-linux-android"
      X86 -> "i686-linux-android"
      X86_64 -> "x86_64-linux-android"
    }

  val minApi: Int
    get() = 21

  val clang: String
    get() = "$ndkTriple$minApi-clang"

  val cpp: String
    get() = "$ndkTriple$minApi-clang++"


  val cpuFamily: String
    get() = when (this) {
      Arm32 -> "arm"
      Arm64 -> "aarch64"
      X86 -> "x86"
      X86_64 -> "x86_64"
    }

  val cmakeSystemProcessor: String
    get() = when (this) {
      AndroidArchitecture.Arm32 -> "armv7a"
      AndroidArchitecture.Arm64 -> "aarch64"
      AndroidArchitecture.X86 -> "x86"
      AndroidArchitecture.X86_64 -> "x86_64"
    }

}