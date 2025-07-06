import fr.composeplayer.builds.android.ProjectUtils
import fr.composeplayer.builds.android.build.BuildContext.Companion.buildContext
import fr.composeplayer.builds.android.tasks.registerGenericBuild
import fr.composeplayer.builds.android.build.Component
import fr.composeplayer.builds.android.tasks.context
import fr.composeplayer.builds.android.utils.execExpectingSuccess
import fr.composeplayer.builds.android.utils.exists

plugins {
  alias(libs.plugins.kotlin.jvm)
}

group = ProjectUtils.GROUP
version = ProjectUtils.VERSION

repositories { mavenCentral() }

kotlin { jvmToolchain(ProjectUtils.JAVA_VERSION) }

registerGenericBuild(
  component = Component.shaderc,
  prebuild = {
    val context = buildContext(Component.shaderc, it)
    enabled = !context.sourceDirectory.resolve("third_party/spirv-tools").exists
    doLast {
      execExpectingSuccess {
        workingDir = context.sourceDirectory.resolve("utils")
        command = arrayOf("python3", "git-sync-deps")
      }
    }
  },
  build = {
    arguments = arrayOf(
      "-DSHADERC_SKIP_TESTS=ON",
      "-DSHADERC_SKIP_EXAMPLES=ON",
      "-DSHADERC_SKIP_COPYRIGHT_CHECK=ON",

      "-DENABLE_EXCEPTIONS=ON",
      "-DENABLE_GLSLANG_BINARIES=OFF",
      "-DSPIRV_SKIP_EXECUTABLES=ON",
      "-DSPIRV_TOOLS_BUILD_STATIC=ON",
      "-DBUILD_SHARED_LIBS=OFF",
    )
  },
  postBuild = {
    doLast {
      val context = buildContext(Component.shaderc, it)
      val shaderc = context.prefixDirectory.resolve("lib/pkgconfig/shaderc.pc")
      shaderc.delete()
      context.prefixDirectory.resolve("lib/pkgconfig/shaderc_combined.pc").renameTo(shaderc)
    }
  }
)
