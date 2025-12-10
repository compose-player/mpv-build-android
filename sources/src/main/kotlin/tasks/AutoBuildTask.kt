package tasks

import base.*
import utils.*
import java.io.File

class AutoBuildTask(
  private val lib: Library,
  private val target: AndroidABI,
  environmentVariables: Map<String, String> = emptyMap(),
  private val arguments: Array<String>,
  private val beforeBuild: suspend AutoBuildTask.() -> Unit = {},
  private val afterBuild: suspend AutoBuildTask.() -> Unit = {},
) : Task() {

  private val sourcesDir: File
    get() = sourceDirOf(lib)

  private val intermediatesDir: File
    get() = intermediatesDirOf(lib, target)

  private val prefixDir: File
    get() = prefixDirectoryOf(lib, target)

  override val skip: Boolean
    get() = intermediatesDir.exists && prefixDir.exists

  private val environment = buildMap {
    val pkgConfigPath = let {
      val list = buildList {
        for (lib in Library.entries) {
          val pkgConfig = prefixDirectoryOf(lib, target).resolve("lib/pkgconfig")
          add(pkgConfig.absolutePath)
        }
      }
      list.joinToString(":")
    }
    this["CC"] = target.CLANG
    this["CXX"] = target.CPP
    this["PKG_CONFIG_LIBDIR"] = pkgConfigPath
    this["LDFLAGS"] = "-Wl,-z,max-page-size=16384"
    putAll(environmentVariables)
  }

  override suspend fun clean() {
    CrossFileGenerator(target, lib).file.delete()
    intermediatesDir.deleteRecursively()
    prefixDir.deleteRecursively()
  }

  override suspend fun executeInternal() {
    beforeBuild.invoke(this)

    val meson = sourcesDir.resolve("meson.build")
    val autogen = sourcesDir.resolve("autogen.sh")
    val cMakeLists = sourcesDir.resolve("CMakeLists.txt")
    val configure = sourcesDir.resolve("configure")
    val bootstrap = sourcesDir.resolve("bootstrap")

    intermediatesDir.apply(File::deleteRecursively).apply(File::mkdirs)
    prefixDir.apply(File::deleteRecursively).apply(File::mkdirs)

    val crossFile = CrossFileGenerator(target, lib).generate()

    when {
      meson.exists -> {
        println("Starting meson: [${lib.name} - ${target.name}]")
        commandLine {
          env.applyFrom(environment)
          workingDir = sourcesDir
          command = arrayOf(
            "meson", "setup", intermediatesDir.absolutePath,
            "--cross-file", crossFile.absolutePath,
            *arguments,
          )
        }
        commandLine {
          env.applyFrom(environment)
          workingDir = intermediatesDir
          command = arrayOf("meson", "compile", "--clean")
        }
        commandLine {
          env.applyFrom(environment)
          workingDir = intermediatesDir
          command = arrayOf("meson", "compile", "--verbose")
        }
        commandLine {
          env.applyFrom(environment)
          workingDir = intermediatesDir
          command = arrayOf("meson", "install")
        }
      }
      else -> {
        if (autogen.exists) {
          println("Starting autogen: [${lib.name} - ${target.name}]")
          commandLine {
            env.applyFrom(environment)
            env["NOCONFIGURE"] = "1"
            workingDir = sourcesDir
            command = arrayOf(autogen.absolutePath)
          }
        }
        when {
          cMakeLists.exists -> {
            println("Starting cmake: [${lib.name} - ${target.name}]")
            commandLine {
              env.applyFrom(environment)
              workingDir = intermediatesDir
              command = arrayOf(
                "cmake", sourcesDir.absolutePath,
                "-DCMAKE_TOOLCHAIN_FILE=${NDK_DIRECTORY.resolve("build/cmake/android.toolchain.cmake").absolutePath}",
                "-DANDROID_ABI=${target.ABI_NAME}",
                "-DANDROID_PLATFORM=android-${target.MIN_API}",
                "-DCMAKE_VERBOSE_MAKEFILE=0",
                "-DCMAKE_BUILD_TYPE=Release",
                "-DCMAKE_SYSTEM_NAME=Android",
                "-DCMAKE_SYSTEM_PROCESSOR=${target.CMAKE_SYSTEM_PROCESSOR}",
                "-DCMAKE_INSTALL_PREFIX=${prefixDir.absolutePath}",
                "-DBUILD_SHARED_LIBS=OFF",
                "-DCMAKE_POLICY_VERSION_MINIMUM=3.5",
                "-DCMAKE_POSITION_INDEPENDENT_CODE=ON",
                *arguments,
              )
            }
          }
          else -> {
            if (!configure.exists && bootstrap.exists) {
              println("Starting bootstrap: [${lib.name} - ${target.name}]")
              commandLine {
                env.applyFrom(environment)
                workingDir = sourcesDir
                command = arrayOf(bootstrap.absolutePath)
              }
            }
            if (!configure.exists) throw IllegalStateException("No build system found for dependency: [$target]")
            println("Starting configure: [${lib.name} - ${target.name}]")
            commandLine {
              env.applyFrom(environment)
              workingDir = intermediatesDir
              command = arrayOf(
                configure.absolutePath,
                "--prefix=${prefixDir.absolutePath}",
                *arguments,
              )
            }
          }
        }
        println("Starting make: [${lib.name} - ${target.name}]")
        commandLine {
          env.applyFrom(environment)
          workingDir = intermediatesDir
          command = arrayOf("make", "-j${WorkingMachine.PARALLELISM}", "V=1")
        }
        commandLine {
          env.applyFrom(environment)
          workingDir = intermediatesDir
          command = arrayOf("make", "-j${WorkingMachine.PARALLELISM}", "V=1", "install")
        }
      }
    }

    afterBuild.invoke(this)
  }

  override suspend fun onFailure(reason: Throwable) {
    intermediatesDir.deleteRecursively()
    prefixDir.deleteRecursively()
    CrossFileGenerator(target, lib).file.delete()
  }


}