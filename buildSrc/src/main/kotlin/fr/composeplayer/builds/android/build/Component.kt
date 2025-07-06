package fr.composeplayer.builds.android.build

enum class Component {
  ffmpeg,
  //dovi,
  shaderc,
  vulkan,
  dav1d,
  placebo,
  mbedtls,
  mpv,
  freetype,
  harfbuzz,
  fribidi,
  ass;

  val branch: String
    get() = when (this) {
      mbedtls -> "v3.6.4"
      dav1d -> "1.5.1"
      ffmpeg -> "n7.1.1"
      freetype -> "VER-2-13-3"
      fribidi -> "v1.0.16"
      harfbuzz -> "11.2.1"
      ass -> "0.17.4"
      placebo -> "v7.351.0"
      mpv -> "v0.40.0"
      shaderc -> "v2025.3"
      vulkan -> error("")
    }

  val gitUrl: String
    get() = when (this) {
      mbedtls -> "https://github.com/Mbed-TLS/mbedtls.git"
      dav1d -> "https://code.videolan.org/videolan/dav1d.git"
      ffmpeg -> "https://github.com/FFmpeg/FFmpeg.git"
      freetype -> "https://gitlab.freedesktop.org/freetype/freetype.git"
      fribidi -> "https://github.com/fribidi/fribidi.git"
      harfbuzz -> "https://github.com/harfbuzz/harfbuzz.git"
      ass -> "https://github.com/libass/libass.git"
      placebo -> "https://code.videolan.org/videolan/libplacebo.git"
      mpv -> "https://github.com/mpv-player/mpv.git"
      shaderc -> "https://github.com/google/shaderc.git"
      vulkan -> error("")
    }

  val gitArgs: Array<String>
    get() = when (this) {
      ffmpeg -> emptyArray()
      //dovi -> emptyArray()
      dav1d -> emptyArray()
      placebo -> arrayOf("--recurse-submodules")
      mbedtls -> arrayOf("--recurse-submodules")
      mpv -> emptyArray()
      freetype -> emptyArray()
      harfbuzz -> emptyArray()
      fribidi -> emptyArray()
      ass -> emptyArray()
      shaderc, vulkan -> emptyArray()
    }
}