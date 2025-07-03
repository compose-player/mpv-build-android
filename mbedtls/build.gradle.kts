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
  component = Component.mbedtls,
  build = {
    arguments = arrayOf(
      "-DUSE_SHARED_MBEDTLS_LIBRARY=ON",
      "-DUSE_STATIC_MBEDTLS_LIBRARY=ON",
    )
  },
)
