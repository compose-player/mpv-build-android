import fr.composeplayer.builds.android.ProjectUtils
import fr.composeplayer.builds.android.build.AndroidArchitecture
import fr.composeplayer.builds.android.build.BuildContext.Companion.buildContext
import fr.composeplayer.builds.android.tasks.registerGenericBuild
import fr.composeplayer.builds.android.build.Component
import fr.composeplayer.builds.android.utils.execExpectingSuccess

plugins {
  alias(libs.plugins.kotlin.jvm)
}

group = ProjectUtils.GROUP
version = ProjectUtils.VERSION

repositories { mavenCentral() }

kotlin { jvmToolchain(ProjectUtils.JAVA_VERSION) }

registerGenericBuild(
  component = Component.mbedtls,
  prebuild = {
    doLast {
      val context = buildContext(Component.mbedtls, it)
      this.execExpectingSuccess {
        val config = context.sourceDirectory.resolve("scripts/config.py").absolutePath
        command = when (it) {
          AndroidArchitecture.X86 -> arrayOf(config, "unset", "MBEDTLS_AESNI_C")
          else -> arrayOf(config, "set", "MBEDTLS_AESNI_C")
        }
      }
    }
  },
  build = {
    arguments = arrayOf(
      "-DUSE_SHARED_MBEDTLS_LIBRARY=OFF",
      "-DUSE_STATIC_MBEDTLS_LIBRARY=ON",
    )
  },
)
