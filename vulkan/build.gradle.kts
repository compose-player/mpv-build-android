import fr.composeplayer.builds.android.ProjectUtils
import fr.composeplayer.builds.android.build.AndroidArchitecture
import fr.composeplayer.builds.android.tasks.registerGenericBuild
import fr.composeplayer.builds.android.build.Component
import fr.composeplayer.builds.android.tasks.context

plugins {
  alias(libs.plugins.kotlin.jvm)
}

group = ProjectUtils.GROUP
version = ProjectUtils.VERSION

repositories { mavenCentral() }

kotlin { jvmToolchain(ProjectUtils.JAVA_VERSION) }

registerGenericBuild(
  component = Component.vulkan,
  clone = { enabled = false },
  build = {
    skip = true
    arguments = emptyArray()
    doLast {
      val destination = context.prefixDirectory.resolve("lib/pkgconfig/vulkan.pc")
      val prefix = context.toolsChainDirectory.parentFile.resolve("sysroot")
      destination.parentFile.mkdirs()

      val ndkTriple = when (context.target) {
        AndroidArchitecture.Arm32 -> "arm-linux-androideabi"
        else -> context.target.ndkTriple
      }
      val version = buildString {
        val file = prefix.resolve("usr/include/vulkan/vulkan_core.h")
        val (_, _, headerVersion) = file.readLines()
          .first { it.startsWith("#define VK_HEADER_VERSION") }
          .split(" ")
        val (_, major, minor) = file.readLines()
          .first { it.startsWith("#define VK_HEADER_VERSION_COMPLETE") }
          .filter(Char::isDigit)
          .toCharArray()
        append("$major.$minor.$headerVersion")
      }
      val content = """
        prefix=${prefix.absolutePath}
        exec_prefix=${'$'}{prefix}
        libdir=${'$'}{prefix}/usr/lib/$ndkTriple/${context.target.minApi}
        includedir=${'$'}{prefix}/usr/include

        Name: Vulkan
        Description: Vulkan loader for Android
        Version: $version
        Libs: -L${'$'}{libdir} -lvulkan
        Cflags: -I${'$'}{includedir}/vulkan
      """.trimIndent()
      destination.writeText(content)
    }
  },
)
