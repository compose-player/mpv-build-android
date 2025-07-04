package fr.composeplayer.builds.android

import fr.composeplayer.builds.android.build.AndroidArchitecture

object ProjectUtils {

  val MIN_API = 21
  val JAVA_VERSION: Int = 23
  val PARALLELISM: Int = 4
  val NDK_VERSION: String = "28.1.13356709"

  val BUILD_TARGETS = AndroidArchitecture.values().toList()

  val GROUP: String = "fr.composeplayer.builds.mpv"
  val VERSION: String = "1.0.0"

}