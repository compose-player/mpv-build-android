package fr.composeplayer.builds.android.build

object WorkingMachine {

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