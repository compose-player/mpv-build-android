import fr.composeplayer.builds.android.ProjectUtils
import fr.composeplayer.builds.android.build.BuildContext.Companion.buildContext
import fr.composeplayer.builds.android.tasks.registerGenericBuild
import fr.composeplayer.builds.android.build.Component

plugins {
  alias(libs.plugins.kotlin.jvm)
}

group = ProjectUtils.GROUP
version = ProjectUtils.VERSION

repositories { mavenCentral() }

kotlin { jvmToolchain(ProjectUtils.JAVA_VERSION) }

registerGenericBuild(
  component = Component.placebo,
  build = {
    arguments = arrayOf(
      "-Dxxhash=disabled",
      "-Dshaderc=enabled",
      "-Dvulkan=enabled",
      "-Dvk-proc-addr=enabled",
      "-Ddemos=false",
      "-Dtests=false",
    )
  },
  postBuild = {
    doLast {
      val context = buildContext(Component.placebo, it)
      val pc = context.prefixDirectory.resolve("lib/pkgconfig/libplacebo.pc")
      val newText = pc.readLines()
        .joinToString(
          separator = "\n",
          transform = {
            when {
              it.startsWith("Libs: ") -> "$it -lc++"
              else -> it
            }
          }
        )
      pc.writeText(newText)
    }
  }
)
