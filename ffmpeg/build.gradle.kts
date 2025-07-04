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

    val extraCFlags = when (buildTarget) {
      AndroidArchitecture.Arm32 -> "-fPIC -I${context.prefixDirectory.resolve("include").absolutePath} -mfpu=neon -march=armv7-a"
      AndroidArchitecture.Arm64,
      AndroidArchitecture.X86,
      AndroidArchitecture.X86_64 -> { "-fPIC -I${context.prefixDirectory.resolve("include").absolutePath}" }
    }
    val extraLdFlags = "-L${context.prefixDirectory.resolve("lib").absolutePath}"

    env.put("ASFLAGS", "-fPIC")
    env.put("CFLAGS", "-fPIC")

    arguments = arrayOf(
      // Licensing options:
      "--disable-gpl",
      "--enable-version3",
      "--disable-nonfree",

      // Configuration options:
      "--disable-static",
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
      //todo: "--enable-libass",
      //"--enable-libdav1d",
      //todo: "--enable-libfreetype",
      //todo: "--enable-libfribidi",
      //todo: "--enable-libharfbuzz",
      //todo: "--enable-libplacebo",
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
      "--as=${context.toolsChainDirectory.resolve(buildTarget.clang).absolutePath}",
      "--cxx=${context.toolsChainDirectory.resolve(buildTarget.cpp).absolutePath}",
      "--pkg-config=pkg-config",
      "--pkg-config-flags=--static",
      "--ranlib=${context.toolsChainDirectory.resolve("llvm-ranlib").absolutePath}",
      "--extra-cflags=\"$extraCFlags\"",
      "--extra-ldflags=\"$extraLdFlags\"",
      "--enable-pic",
      //"--sysroot=${context.toolsChainDirectory.parentFile.resolve("sysroot").absolutePath}",

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
