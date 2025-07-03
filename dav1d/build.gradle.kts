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
  component = Component.dav1d,
  build = {
    arguments = arrayOf(
      "-Denable_tests=false",
      "-Db_lto=true",
      "-Dstack_alignment=16",
    )
  },
)
