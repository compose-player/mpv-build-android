enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "mpv-build-android"

include("dav1d")
include("mbedtls")
include("harfbuzz")
include("fribidi")
include("freetype")
include("ass")
include("placebo")
include("ffmpeg")
include("mpv")
include("shaderc")
include("vulkan")