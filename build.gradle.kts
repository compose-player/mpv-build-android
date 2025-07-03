import fr.composeplayer.builds.android.ProjectUtils
import fr.composeplayer.builds.android.build.AndroidArchitecture
import org.gradle.kotlin.dsl.support.zipTo

plugins {
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.kotlin.dsl) apply false
}

afterEvaluate {

  val cleanEverything by tasks.registering {
    doLast {
      rootDir.resolve("binaries").deleteRecursively()
      rootDir.resolve("builds").deleteRecursively()
      rootDir.resolve("crossfiles").deleteRecursively()
      rootDir.resolve("vendor").deleteRecursively()
      rootDir.resolve("zips").deleteRecursively()
    }
  }

  val packageArtifacts by tasks.registering {
    doLast {
      val zips = rootDir.resolve("zips")
        .apply(File::deleteRecursively)
        .apply(File::mkdirs)
      try {
        val components = rootDir.resolve("binaries").listFiles().orEmpty()

        for (component in components) {
          for (target in ProjectUtils.BUILD_TARGETS) {
            val staticDest = zips.resolve("static/${target.abiName}").apply(File::mkdirs)
            val sharedDest = zips.resolve("shared/${target.abiName}").apply(File::mkdirs)
            val componentDir = component.resolve(target.name)
            val binaries = componentDir.resolve("lib").listFiles().orEmpty()
            val include = componentDir.resolve("include").listFiles().orEmpty()

            for (item in include) {
              val types = setOf(staticDest, sharedDest)
              for (type in types) {
                val destination = type.resolve("include/${item.name}").apply { parentFile.mkdirs() }
                item.copyRecursively(destination, true)
              }
            }

            for (binary in binaries) {
              val dest = when (binary.extension) {
                "so" -> sharedDest
                "a" -> staticDest
                else -> continue
              }
              binary.copyTo(
                target = dest.resolve(binary.name),
                overwrite = true,
              )
            }
          }
        }

        zipTo(
          zipFile = zips.resolve("static.zip"),
          baseDir = zips.resolve("static"),
        )
        zipTo(
          zipFile = zips.resolve("shared.zip"),
          baseDir = zips.resolve("shared"),
        )
      } catch (error: Throwable) {
        zips.deleteRecursively()
        if (error is GradleException) throw error
        throw GradleException("Artifacts packaging failed", error)
      }
    }
  }

}