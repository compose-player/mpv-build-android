import fr.composeplayer.builds.android.ProjectUtils
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
  component = Component.ass,
  build = {
    env.put("ASFLAGS", "-DPIC=1")
    arguments = arrayOf(
      "-Dtest=disabled",
      "-Dasm=disabled",
      "-Dlibunibreak=disabled",
      "-Dfontconfig=disabled",
      "-Drequire-system-font-provider=false",
      "-Dlarge-tiles=true",
      "-Db_pie=false"
    )
  },
)
