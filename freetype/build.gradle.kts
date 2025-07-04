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
  component = Component.freetype,
  build = {
    arguments = arrayOf(
      //"-Dbrotli=disabled",
      //"-Dbzip2=disabled",
      //"-Dharfbuzz=disabled",
      //"-Dpng=disabled",
      //"-Dtests=disabled",
      //"-Dzlib=disabled",
    )
  },
)
