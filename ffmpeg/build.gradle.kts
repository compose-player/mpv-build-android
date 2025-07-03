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
  component = Component.ffmpeg,
  build = {
    val buildTarget = target.get()

    val cpu: String = when (buildTarget) {
      AndroidArchitecture.Arm32 -> "armv7-a"
      AndroidArchitecture.Arm64 -> "armv8-a"
      AndroidArchitecture.X86 -> "i686"
      AndroidArchitecture.X86_64 -> "x86-64"
    }

    val arch: String = when (buildTarget) {
      AndroidArchitecture.Arm32 -> "arm"
      AndroidArchitecture.Arm64 -> "aarch64"
      AndroidArchitecture.X86 -> "x86"
      AndroidArchitecture.X86_64 -> "x86_64"
    }

    val cFlags: String = buildString {
      val values = when (buildTarget) {
        AndroidArchitecture.Arm32 -> listOf("-mfpu=neon", "-mcpu-cortex-a8")
        AndroidArchitecture.Arm64, AndroidArchitecture.X86, AndroidArchitecture.X86_64 -> emptyList()
      }
      values.joinToString(separator = " ")
    }

    val extraCFlags = buildString {
      val _path = context.prefixDirectory.resolve("include").absolutePath
      append("\"")
      append( "-fPIC -I${_path} ${cFlags}".trim() )
      append("\"")
    }

    val extraLdFlags = buildString {
      val _path = context.prefixDirectory.resolve("lib").absolutePath
      append("\"")
      append("-L${_path}")
      append("\"")
    }

    arguments = arrayOf(
      // Licensing options:
      "--disable-gpl",
      "--enable-version3",
      "--disable-nonfree",

      // Configuration options:
      "--enable-static",
      "--enable-shared",
      "--enable-small",
      "--disable-runtime-cpudetect",

      // Program options:
      "--disable-programs",
      "--enable-ffmpeg",
      "--disable-ffplay",
      "--disable-ffprobe",

      // Documentation options:
      "--disable-doc",
      "--disable-htmlpages",
      "--disable-manpages",
      "--disable-podpages",
      "--disable-txtpages",

      // Component options:
      "--disable-avdevice",

      // Individual component options:
      "--disable-encoders",
      "--disable-indevs",
      "--enable-protocol=android_content,hls,rtmp,rtmps,file,http,https,data,ftp,httpproxy,fd",

      "--disable-muxers",
      "--enable-muxer=spdif",
      "--enable-muxer=ass",
      "--enable-muxer=srt",

      "--enable-bsfs",

      // External library support:
      "--disable-iconv",
      "--enable-jni",
      "--enable-libass",
      "--enable-libdav1d",
      "--enable-libfreetype",
      "--enable-libfribidi",
      "--enable-libharfbuzz",
      "--enable-libplacebo",
      "--enable-mbedtls",
      "--enable-mediacodec",
      "--disable-v4l2-m2m",
      "--disable-vulkan",

      // Toolchain options:
      "--arch=$arch",
      "--cpu=$cpu",
      //"--cross-prefix=${buildTarget.ndkTriple}-",
      "--enable-cross-compile",
      "--target-os=android",
      "--nm=${context.toolsChainDirectory.resolve("llvm-nm").absolutePath}",
      "--ar=${context.toolsChainDirectory.resolve("llvm-ar").absolutePath}",
      "--cc=${context.toolsChainDirectory.resolve(buildTarget.clang).absolutePath}",
      "--cxx=${context.toolsChainDirectory.resolve(buildTarget.cpp).absolutePath}",
      "--pkg-config=pkg-config",
      "--pkg-config-flags=--static",
      "--ranlib=${context.toolsChainDirectory.resolve("llvm-ranlib").absolutePath}",
      "--extra-cflags=$extraCFlags",
      "--extra-ldflags=$extraLdFlags",
      "--enable-pic",

      // Optimization options:
      *when (buildTarget) {
        AndroidArchitecture.Arm32, AndroidArchitecture.Arm64 -> emptyArray()
        AndroidArchitecture.X86, AndroidArchitecture.X86_64 -> arrayOf("--disable-asm")
      },

      // Developer options:
      "--enable-optimizations",
      "--disable-stripping",
    )
  },
)
