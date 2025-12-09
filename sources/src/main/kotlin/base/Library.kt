@file:Suppress("EnumEntryName")

package base

enum class Library { ffmpeg, dav1d, placebo, mbedtls, mpv, freetype, harfbuzz, fribidi, ass }

val Library.branch: String
  get() = when (this) {
    Library.mbedtls -> "v3.6.5"
    Library.dav1d -> "1.5.2"
    Library.ffmpeg -> "n7.1.3"
    Library.freetype -> "VER-2-14-1"
    Library.fribidi -> "v1.0.16"
    Library.harfbuzz -> "12.2.0"
    Library.ass -> "0.17.4"
    Library.placebo -> "v7.351.0"
    Library.mpv -> "v0.40.0"
  }

val Library.repositoryUrl: String
  get() = when (this) {
    Library.mbedtls -> "https://github.com/Mbed-TLS/mbedtls.git"
    Library.dav1d -> "https://code.videolan.org/videolan/dav1d.git"
    Library.ffmpeg -> "https://github.com/FFmpeg/FFmpeg.git"
    Library.freetype -> "https://gitlab.freedesktop.org/freetype/freetype.git"
    Library.fribidi -> "https://github.com/fribidi/fribidi.git"
    Library.harfbuzz -> "https://github.com/harfbuzz/harfbuzz.git"
    Library.ass -> "https://github.com/libass/libass.git"
    Library.placebo -> "https://code.videolan.org/videolan/libplacebo.git"
    Library.mpv -> "https://github.com/mpv-player/mpv.git"
  }

val Library.recurseSubmodules: Boolean get() = this in setOf(Library.placebo, Library.mbedtls)

val Library.gitArgs: Array<String> get() = if (this.recurseSubmodules) arrayOf("--recurse-submodules") else emptyArray()