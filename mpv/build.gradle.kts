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
  component = Component.mpv,
  build = {
    arguments = arrayOf(
      "-Dgpl=false",
      "-Dcplayer=false",
      "-Dlibmpv=true",
      "-Dtests=false",

      "-Dcdda=disabled",
      "-Dcplugins=disabled",
      "-Ddvbin=disabled",
      "-Ddvdnav=disabled",
      "-Diconv=disabled",
      "-Djavascript=disabled",
      "-Djpeg=disabled",
      "-Dlcms2=disabled",
      "-Dlibarchive=disabled",
      "-Dlibavdevice=disabled",
      "-Dlibbluray=disabled",
      "-Dlua=disabled",

      "-Dhtml-build=disabled",
      "-Dmanpage-build=disabled",
      "-Dpdf-build=disabled",
    )
  },
)
