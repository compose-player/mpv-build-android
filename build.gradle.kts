import fr.composeplayer.builds.android.ProjectUtils
import org.gradle.kotlin.dsl.support.zipTo

plugins {
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.kotlin.dsl) apply false
}

afterEvaluate {

  val cleanEverything by tasks.registering {
    group = "mpv-build"
    doLast {
      rootDir.resolve("binaries").deleteRecursively()
      rootDir.resolve("builds").deleteRecursively()
      rootDir.resolve("crossfiles").deleteRecursively()
      rootDir.resolve("vendor").deleteRecursively()
      rootDir.resolve(".github_artifacts").deleteRecursively()
    }
  }

  val packageArtifacts by tasks.registering {
    doLast {
      val artifacts = rootDir.resolve(".github_artifacts")
        .apply(File::deleteRecursively)
        .apply(File::mkdirs)

      val archive = artifacts.resolve("archive.zip")
      val temp = artifacts.resolve("temp").apply(File::mkdirs)

      try {
        for (target in ProjectUtils.BUILD_TARGETS) {
          val components = rootDir
            .resolve("binaries")
            .listFiles().orEmpty().toList()
            .map { it.resolve(target.name) }

          for (component in components) {
            val include = component.resolve("include")
            include
              .copyRecursively(
                target = temp.resolve("${target.abiName}/include").apply(File::mkdirs)
              )
            val binaries = component.resolve("lib").listFiles()!!.toList()
            for (file in binaries) {
              if (file.extension != "so") continue
              val destination = temp.resolve("${target.abiName}/${file.name}")
              file.copyTo(destination)
            }
          }

        }
        zipTo(archive, temp)
      } finally {
        temp.deleteRecursively()
      }

    }
  }

}