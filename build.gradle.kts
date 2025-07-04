import fr.composeplayer.builds.android.ProjectUtils
import org.gradle.kotlin.dsl.support.zipTo
import fr.composeplayer.builds.android.build.Component

plugins {
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.kotlin.dsl) apply false
}

afterEvaluate {

  val printVersion by tasks.registering {
    doLast { print(ProjectUtils.VERSION) }
  }

  val printDeps by tasks.registering {
    doLast {
      val text = buildString {
        for (dep in Component.values()) {
          appendLine("${dep.name}: ${dep.branch}")
        }
      }
      print(text)
    }
  }

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

  val assemble by tasks.registering {
    group = "mpv-build"
    val ffmpeg = tasks.getByName("assemble[ffmpeg]")
    val dav1d = tasks.getByName("assemble[dav1d]")
    val placebo = tasks.getByName("assemble[placebo]")
    val mbedtls = tasks.getByName("assemble[mbedtls]")
    val mpv = tasks.getByName("assemble[mpv]")
    val freetype = tasks.getByName("assemble[freetype]")
    val harfbuzz = tasks.getByName("assemble[harfbuzz]")
    val fribidi = tasks.getByName("assemble[fribidi]")
    val ass = tasks.getByName("assemble[ass]")

    harfbuzz.mustRunAfter(freetype)
    ass.mustRunAfter(harfbuzz, fribidi, freetype)
    placebo.mustRunAfter(dav1d)
    ffmpeg.mustRunAfter(mbedtls, ass, placebo)
    mpv.mustRunAfter(ffmpeg)
    dependsOn(ffmpeg, dav1d, placebo, mbedtls, mpv, freetype, harfbuzz, fribidi, ass)
    finalizedBy(packageArtifacts)
  }

}

operator fun TaskContainer.get(key: Component) {
  this.getByName("assemble[${key.name}]")
}