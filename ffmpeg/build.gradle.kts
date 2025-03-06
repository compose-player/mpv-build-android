import fr.composeplayer.common.*
import fr.composeplayer.common.Dependency
import fr.composeplayer.common.Platform

plugins {
  alias(libs.plugins.kotlin.jvm)
}

group = "fr.composeplayer"
version = libs.versions.library.get()

repositories {
  mavenCentral()
}

dependencies {
}

kotlin {
  jvmToolchain(23)
}

val clone by tasks.registering(CloneSource::class) {
  dependency = Dependency.FFMpeg
}

val androidTargets = AndroidAbi.values().toList()

configure(androidTargets) {

  val autogen by tasks.register(
    name = "autogenFFmpeg[android-${abiName}]",
    type = AutoGenTask::class,
  ) {
    this.dependency = Dependency.FFMpeg
  }

  val configure by tasks.register(
    name = "configureFFmpeg[android-$abiName]",
    type = ConfigureTask::class
  ) {
    val abi = this@configure
    val cFlags = when (abi) {
      AndroidAbi.Arm64 -> ""
      AndroidAbi.Arm32 -> "-mfpu=neon -mcpu=cortex-a8"
      AndroidAbi.X86 -> ""
      AndroidAbi.X86_64 -> ""
    }
    val ldFlags = when (abi) {
      AndroidAbi.Arm64 -> ""
      AndroidAbi.Arm32 -> ""
      AndroidAbi.X86 -> ""
      AndroidAbi.X86_64 -> ""
    }
    val arch = when (abi) {
      AndroidAbi.Arm64 -> "aarch64"
      AndroidAbi.Arm32 -> "arm"
      AndroidAbi.X86 -> "x86"
      AndroidAbi.X86_64 -> "x86_64"
    }
    val cpu = when (abi) {
      AndroidAbi.Arm64 -> "armv8-a"
      AndroidAbi.Arm32 -> "armv7-a"
      AndroidAbi.X86 -> "i686"
      AndroidAbi.X86_64 -> "x86-64"
    }
    val additionalOptions = when (abi) {
      AndroidAbi.Arm64 -> emptyList()
      AndroidAbi.Arm32 -> emptyList()
      AndroidAbi.X86 -> listOf("--disable-asm")
      AndroidAbi.X86_64 -> listOf("--disable-asm")
    }
    this.dependency = Dependency.FFMpeg
    this.platform = Platform.Android(abi)
    this.configureArgs = listOf(
      "--target-os=android",
      "--enable-cross-compile",
      "--cross-prefix=${abi.ndkTriple}-",
      "--enable-pic",

      "--ar=llvm-ar",
      "--cc=${File(platform.toolsChainDir, platform.CC).absolutePath}",
      "--ranlib=llvm-ranlib",
      "--nm=llvm-nm",
      "--arch=$arch",
      "--cpu=$cpu",
      "--pkg-config=pkg-config",
      "--pkg-config-flags=--static",
      "--extra-cflags=\"-fPIC -I${binariesDir.absolutePath}/include $cFlags".trimEnd() + '"',
      "--extra-ldflags=\"-L${binariesDir.absolutePath}/lib $ldFlags".trimEnd() + '"',

      //Licensing
      "--disable-gpl",
      "--disable-nonfree",
      "--enable-version3",

      //Configuration
      "--disable-shared",
      "--enable-static",
      "--enable-small",
      "--disable-runtime-cpudetect",

      //Programs
      "--disable-programs",
      "--disable-ffprobe",
      "--disable-ffplay",

      //Documentation
      "--disable-doc",
      "--disable-htmlpages",
      "--disable-manpages",
      "--disable-podpages",
      "--disable-txtpages",

      //Components
      "--disable-avdevice",
      "--disable-postproc",

      //Individual components
      "--disable-muxers",
      "--disable-encoders",
      "--disable-devices",
      "--disable-protocols",
      "--disable-indevs",

      "--enable-muxer=spdif",
      "--enable-protocol=file,http,https,rtmp,rtmps",

      //External library support
      "--enable-jni",
      "--enable-libdav1d",
      "--enable-mediacodec",
      "--enable-mbedtls",
      "--disable-vulkan",
      "--disable-v4l2-m2m",

      //Developer options
      "--enable-optimizations",
      "--disable-stripping",


      *additionalOptions.toTypedArray()
    )
    this.dependsOn(autogen)
  }


  val make by tasks.register(
    name = "makeFFmpeg[android-$abiName]",
    type = MakeTask::class,
  ) {
    this.dependency = Dependency.FFMpeg
    this.platform = Platform.Android(this@configure)
    this.dependsOn(configure)
  }

}

val buildAndroidBinaries by tasks.registering {

  val tasks = androidTargets.map { "makeFFmpeg[android-${it.abiName}]" }
  dependsOn(clone, *tasks.toTypedArray())
}
