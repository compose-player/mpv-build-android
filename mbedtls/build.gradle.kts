import fr.composeplayer.common.*
import fr.composeplayer.common.Dependency
import fr.composeplayer.common.Platform

plugins {
  alias(libs.plugins.kotlin.jvm)
}

group = "fr.composeplayer"
version = libs.versions.library.get()

val cloneMbedtls by tasks.registering(CloneSource::class) { dependency = Dependency.Mbedtls }

val androidTargets = AndroidAbi.values().toList()

configure(androidTargets) {

  tasks.register("makeMbedtls[android-${abiName}]") {
    val platform = Platform.Android(this@configure)
    this.dependsOn(cloneMbedtls)
    doLast {
      val sourceDir = File(project.rootDir, "vendor/${Dependency.Mbedtls.libName}")
      val binariesDir = File(project.rootDir, "binaries/${platform.name}/${platform.buildDirName}").apply(File::mkdirs)
      exec {
        addExports(platform, binariesDir)
        workingDir = sourceDir
        commandLine("make", "clean")
      }
      exec {
        addExports(platform, binariesDir)
        workingDir = sourceDir
        commandLine("make", "-j${Runtime.getRuntime().availableProcessors()}", "no_test")
      }
      exec {
        addExports(platform, binariesDir)
        workingDir = sourceDir
        commandLine("make", "DESTDIR=${binariesDir.absolutePath}", "install")
      }
    }
  }

}

val buildAndroidBinaries by tasks.registering {
  val tasks = androidTargets.map { "makeMbedtls[android-${it.abiName}]" }
  dependsOn(*tasks.toTypedArray())
}
