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
  component = Component.harfbuzz,
  build = {
    arguments = arrayOf(
      "-Dtests=disabled",
      "-Ddocs=disabled",
      "-Dglib=disabled",
      "-Dcairo=disabled",
      "-Dfreetype=enabled",
    )
  },
)
