package fr.composeplayer.builds.android

import java.lang.Runtime
import fr.composeplayer.builds.android.build.AndroidArchitecture

object ProjectUtils {

  val JAVA_VERSION: Int = 23
  val PARALLELISM: Int = Runtime.getRuntime().availableProcessors() - 2
  val NDK_VERSION: String = "28.1.13356709"

  val BUILD_TARGETS = AndroidArchitecture.values().toList()

  val GROUP: String = "fr.composeplayer.builds.mpv"
  val VERSION: String = "1.0.0"

}