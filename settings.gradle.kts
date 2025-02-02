plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "compose-player-binaries"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("ffmpeg")
include("ffmpeg")
include("mbedtls")
include("placebo")
include("ass")
include("mpv")
include("dav1d")
