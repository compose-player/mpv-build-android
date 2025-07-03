package fr.composeplayer.builds.android.tasks

import fr.composeplayer.builds.android.ProjectUtils
import fr.composeplayer.builds.android.build.AndroidArchitecture
import fr.composeplayer.builds.android.build.BuildContext
import fr.composeplayer.builds.android.build.BuildContext.Companion.buildContext
import fr.composeplayer.builds.android.build.Component
import fr.composeplayer.builds.android.utils.CrossfileGenerator
import fr.composeplayer.builds.android.utils.applyFrom
import fr.composeplayer.builds.android.utils.execExpectingResult
import fr.composeplayer.builds.android.utils.execExpectingSuccess
import fr.composeplayer.builds.android.utils.exists
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.provideDelegate

abstract class AutoBuild : DefaultTask() {

  @get:Input
  @get:Optional
  abstract val skip: Property<Boolean>

  @get:Input
  abstract val target: Property<AndroidArchitecture>

  @get:Input
  abstract val component: Property<Component>

  @get:Input
  abstract val arguments: Property<Array<String>>

  private val environement: Map<String, String?> by lazy {
    val cFlags = context.cFlags.joinToString(separator = " ")
    val ldFlags = context.ldFlags.joinToString(separator = " ")
    val pkgConfigPath = let {
      val list = buildList {
        for (comp in Component.values()) {
          val _context = buildContext(comp, target.get())
          val pkgConfig = _context.prefixDirectory.resolve("lib/pkgconfig")
          add(pkgConfig.absolutePath)
        }
      }
      list.joinToString(":")
    }
    val pkgConfigPathDefault = execExpectingResult { command = arrayOf("pkg-config", "--variable", "pc_path", "pkg-config") }
    mutableMapOf(
      "CC" to context.target.clang, //fixme: Set absolute path
      "CXX" to context.target.cpp, //fixme: Set absolute path
      "PKG_CONFIG_LIBDIR" to "${pkgConfigPath}",//:${pkgConfigPathDefault}",
    )
  }

  @TaskAction
  fun execute() {
    if (skip.isPresent && skip.get()) return

    val meson = context.sourceDirectory.resolve("meson.build")
    val autogen = context.sourceDirectory.resolve("autogen")
    val cMakeLists = context.sourceDirectory.resolve("CMakeLists.txt")
    val configure = context.sourceDirectory.resolve("configure")
    val bootstrap = context.sourceDirectory.resolve("bootstrap")

    if (!context.sourceDirectory.exists) throw GradleException("No source found for component $component")

    try {
      context.buildDirectory.mkdirs()
      context.prefixDirectory.mkdirs()

      val crossfile = CrossfileGenerator(context).generate()

      when {
        meson.exists() -> {
          logger.info("Building component [$component] with meson")
          execExpectingSuccess {
            env.applyFrom(environement)
            workingDir = context.sourceDirectory
            command = arrayOf(
              "meson", "setup", context.buildDirectory.absolutePath,
              "--default-library=both",
              "--cross-file", crossfile.absolutePath,
              *arguments.get(),
            )
          }
          execExpectingSuccess {
            env.applyFrom(environement)
            workingDir = context.buildDirectory
            command = arrayOf("meson", "compile", "--clean")
          }
          execExpectingSuccess {
            env.applyFrom(environement)
            workingDir = context.buildDirectory
            command = arrayOf("meson", "compile", "--verbose")
          }
          execExpectingSuccess {
            env.applyFrom(environement)
            workingDir = context.buildDirectory
            command = arrayOf("meson", "install")
          }
        }
        else -> {
          if (autogen.exists()) {
            logger.info("Running autogen for component [$component]")
            execExpectingSuccess {
              env.applyFrom(environement)
              env["NOCONFIGURE"] = "1"
              workingDir = context.sourceDirectory
              command = arrayOf(autogen.absolutePath)
            }
          }
          when {
            cMakeLists.exists() -> {
              logger.info("Runing cmake for component [$component]")
              execExpectingSuccess {
                env.applyFrom(environement)
                workingDir = context.buildDirectory
                command = arrayOf(
                  "cmake", context.sourceDirectory.absolutePath,
                  "-DCMAKE_TOOLCHAIN_FILE=${context.ndkDirectory.resolve("build/cmake/android.toolchain.cmake").absolutePath}",
                  "-DANDROID_ABI=${context.target.abiName}",
                  "-DANDROID_PLATFORM=android-${context.target.minApi}",
                  "-DCMAKE_VERBOSE_MAKEFILE=0",
                  "-DCMAKE_BUILD_TYPE=Release",
                  "-DCMAKE_SYSTEM_NAME=Android",
                  "-DCMAKE_SYSTEM_PROCESSOR=${target.get().cmakeSystemProcessor}",
                  "-DCMAKE_INSTALL_PREFIX=${context.prefixDirectory.absolutePath}",
                  "-DBUILD_SHARED_LIBS=ON",
                  "-DCMAKE_POLICY_VERSION_MINIMUM=3.5",
                  *arguments.get(),
                )
              }
            }
            else -> {
              if (!configure.exists() && bootstrap.exists()) {
                logger.info("Runing bootstrap for component [$component]")
                execExpectingSuccess {
                  env.applyFrom(environement)
                  workingDir = context.sourceDirectory
                  command = arrayOf(bootstrap.absolutePath)
                }
              }
              if (!configure.exists()) {
                throw GradleException("No build system found for dependency: ${component}")
              }
              logger.info("Runing configure for component [$component]")
              execExpectingSuccess {
                env.applyFrom(environement)
                workingDir = context.buildDirectory
                command = arrayOf(
                  configure.absolutePath,
                  "--prefix=${context.prefixDirectory.absolutePath}",
                  *arguments.get(),
                )
              }
            }
          }
          logger.info("Runing make for component [$component]")
          execExpectingSuccess {
            env.applyFrom(environement)
            workingDir = context.buildDirectory
            command = arrayOf("make", "-j${ProjectUtils.PARALLELISM}", "V=1")
          }
          execExpectingSuccess {
            env.applyFrom(environement)
            workingDir = context.buildDirectory
            command = arrayOf("make", "-j${ProjectUtils.PARALLELISM}", "V=1", "install")
          }
        }
      }

    } catch (error: Throwable) {
      context.buildDirectory.deleteRecursively()
      context.prefixDirectory.deleteRecursively()
      CrossfileGenerator(context).delete()
      when {
        error is GradleException -> throw error
        else -> throw GradleException("Task failed", error)
      }
    }
  }

}

public val AutoBuild.context: BuildContext
  get() = buildContext(component.get(), target.get())