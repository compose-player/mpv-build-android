package fr.composeplayer.common

enum class Dependency {
  FFMpeg,
  Mpv,
  Mbedtls,
  Fribidi,
  Harfbuzz,
  Freetype,
  Placebo,
  Ass,
  Dav1d,
}

val Dependency.gitUrl: String
  get() = when (this) {
    Dependency.Mbedtls -> "https://github.com/Mbed-TLS/mbedtls.git"
    Dependency.Dav1d -> "https://code.videolan.org/videolan/dav1d.git"
    Dependency.FFMpeg -> "https://github.com/FFmpeg/FFmpeg.git"
    Dependency.Freetype -> "https://gitlab.freedesktop.org/freetype/freetype.git"
    Dependency.Fribidi -> "https://github.com/fribidi/fribidi.git"
    Dependency.Harfbuzz -> "https://github.com/harfbuzz/harfbuzz.git"
    Dependency.Ass -> "https://github.com/libass/libass.git"
    Dependency.Placebo -> "https://code.videolan.org/videolan/libplacebo.git"
    Dependency.Mpv -> "https://github.com/mpv-player/mpv.git"
  }

val Dependency.libName: String
  get() = when (this) {
    Dependency.FFMpeg -> "ffmpeg"
    Dependency.Mpv -> "mpv"
    Dependency.Mbedtls -> "mbedtls"
    Dependency.Fribidi -> "fribidi"
    Dependency.Harfbuzz -> "hardbuzz"
    Dependency.Freetype -> "freetype"
    Dependency.Placebo -> "libplacebo"
    Dependency.Ass -> "libass"
    Dependency.Dav1d -> "dav1d"
  }

val Dependency.branch: String
  get() = when (this) {
    Dependency.Mbedtls -> "v3.6.1"
    Dependency.Dav1d -> "1.4.3"
    Dependency.FFMpeg -> "n7.1"
    Dependency.Freetype -> "VER-2-13-3"
    Dependency.Fribidi -> "v1.0.16"
    Dependency.Harfbuzz -> "10.0.1"
    Dependency.Ass -> "0.17.3"
    Dependency.Placebo -> "v7.349.0"
    Dependency.Mpv -> "v0.39.0"
  }

val Dependency.gitArgs: List<String>
  get() = when (this) {
    Dependency.Mbedtls -> listOf("--recurse-submodules")
    Dependency.Dav1d -> emptyList()
    Dependency.FFMpeg -> emptyList()
    Dependency.Freetype -> emptyList()
    Dependency.Fribidi -> emptyList()
    Dependency.Harfbuzz -> emptyList()
    Dependency.Ass -> emptyList()
    Dependency.Placebo -> listOf("--recurse-submodules")
    Dependency.Mpv -> emptyList()
  }