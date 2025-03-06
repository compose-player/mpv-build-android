import fr.composeplayer.common.*
import fr.composeplayer.common.Dependency
import fr.composeplayer.common.Platform

plugins {
  alias(libs.plugins.kotlin.jvm)
}

group = "fr.composeplayer"
version = libs.versions.library.get()

repositories { mavenCentral() }
dependencies {}
kotlin { jvmToolchain(23) }

val androidTargets = AndroidAbi.values().toList()

val clone by tasks.registering(CloneSource::class) {
  dependency = Dependency.Placebo
}


configure(androidTargets) {

  val meson by tasks.register(
    name = "mesonPlacebo[android-$abiName]",
    type = MesonTask::class,
  ) {
    this.platform = Platform.Android(this@configure)
    this.dependency = Dependency.Placebo
    this.mesonArgs = listOf("-Dvulkan=disabled", "-Ddemos=false")
    this.doLast {
      val pkgConfigFile = File(binariesDir, "lib/pkgconfig/libplacebo.pc")
      val lines = pkgConfigFile
        .readLines()
        .map {
          when {
            it.startsWith(prefix = "Libs:", ignoreCase = false) -> "$it -lc++"
            else -> it
          }
        }
        .joinToString(separator = "\n")
      pkgConfigFile.writeText(lines)
    }
  }

}

val buildAndroidBinaries by tasks.registering {
  val tasks = androidTargets.map { "mesonPlacebo[android-${it.abiName}]" }
  dependsOn(clone, *tasks.toTypedArray())
}
