package cli

import base.*
import builders.BaseBuilder
import kotlinx.coroutines.runBlocking
import net.lingala.zip4j.ZipFile
import picocli.CommandLine
import picocli.CommandLine.*
import java.io.File
import java.util.concurrent.Callable
import kotlin.system.exitProcess

@Command(
  name = "mpv-builder-cli",
  mixinStandardHelpOptions = true,
  description = ["Example Picocli app"]
)
class MpvCliBuilder : Callable<Int> {

  @Option(
    names = ["--clean"],
    description = ["Libraries to clean, or 'all' to clean everything"]
  )
  var _clean: List<String> = emptyList()

  @Option(
    names = ["--libraries"],
    description = ["List of libraries to include build"]
  )
  var _libraries: List<String> = emptyList()

  @Option(
    names = ["--targets"],
    description = ["List of Android ABI to build for"]
  )
  var _targets: List<String> = emptyList()

  @Option(
    names = ["--bundle-artifacts"],
    description = ["Bundle built artifacts into a final package"]
  )
  var bundleArtifacts: Boolean = false

  @Option(names = ["--print-libraries"])
  var printLibraries: Boolean = false

  @Option(names = ["--print-version"])
  var printVersion: Boolean = false

  val clean: List<Library>
    get() = when {
      _clean.size == 1 && _clean[0] == "all" -> Library.entries.toList()
      else -> _clean.map(::enumValueOf)
    }

  val libraries: List<Library>
    get() = when {
      _libraries.size == 1 && _libraries[0] == "all" -> Library.entries.toList()
      else -> _libraries.map(::enumValueOf)
    }

  val targets: List<AndroidABI>
    get() = when {
      _targets.size == 1 && _targets[0] == "all" -> AndroidABI.entries.toList()
      else -> _targets.map(::enumValueOf)
    }

  override fun call(): Int = runBlocking {
    if (printLibraries) {
      for (lib in Library.entries) println("[${lib.name}](${lib.repositoryUrl}): ${lib.branch}")
      return@runBlocking 0
    }
    if (printVersion) {
      println("1.1.0")
      return@runBlocking 0
    }
    val build = libraries.distinct().sortedBy(Library::buildOrder)
    for (arch in targets) {
      for (lib in clean) BaseBuilder.forLib(lib, arch).cleanBuilds()
      for (lib in build) BaseBuilder.forLib(lib, arch).build()
    }

    if (bundleArtifacts) {
      val cd = System.getProperty("user.dir").let(::File)
      val artifactsDir = cd.resolve("artifacts")
      val builtTargets = cd.resolve(".products/binaries").listFiles().orEmpty()
      val archive = cd.resolve("artifacts.zip")
      for (target in builtTargets) {
        target
          .resolve("ffmpeg/include/libavcodec/jni.h")
          .copyTo( artifactsDir.resolve("${target.name}/include/libavcodec/jni.h"), true )

        target
          .resolve("mpv/include/mpv")
          .copyRecursively( artifactsDir.resolve("${target.name}/include/mpv"), true )

        target
          .resolve("ass/lib/libass.so")
          .copyRecursively( artifactsDir.resolve("${target.name}/libass.so"), true )

        target
          .resolve("mpv/lib/libmpv.so")
          .copyRecursively( artifactsDir.resolve("${target.name}/libmpv.so"), true )

        val binaries = setOf("libavcodec.so", "libavfilter.so", "libavformat.so", "libavutil.so", "libswresample.so", "libswscale.so")
        for (bin in binaries) {
          val source = target.resolve("ffmpeg/lib/$bin")
          val dst = artifactsDir.resolve(target.name).resolve(bin)
          source.copyTo(dst, true)
        }
      }
      ZipFile(archive).addFolder(artifactsDir)
    }

    return@runBlocking 0
  }

  companion object {

    @JvmStatic
    fun main(args: Array<String>) {
      val status = CommandLine(MpvCliBuilder()).execute(*args)
      exitProcess(status)
    }

  }

}



val Library.buildOrder: Int
  get() = when (this) {
    Library.ffmpeg -> 7
    Library.dav1d -> 0
    Library.placebo -> 5
    Library.mbedtls -> 6
    Library.mpv -> 8
    Library.freetype -> 1
    Library.harfbuzz -> 2
    Library.fribidi -> 3
    Library.ass -> 4
  }