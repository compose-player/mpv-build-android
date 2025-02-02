import fr.composeplayer.common.*
import fr.composeplayer.common.Dependency
import fr.composeplayer.common.Platform

plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "fr.composeplayer"
version = libs.versions.library.get()

val cloneMpv by tasks.registering(CloneSource::class) { dependency = Dependency.Mpv }

val androidTargets = AndroidAbi.values().toList()

configure(androidTargets) {

    val mesonMpv by tasks.register(
        name = "mesonMpv[android-${abiName}]",
        type = MesonTask::class,
    ) {
        this.dependsOn(cloneMpv)
        this.dependency = Dependency.Mpv
        this.platform = Platform.Android(this@configure)
        this.mesonArgs = listOf(
            "--prefer-static",
            "--default-library", "static",
            "-Dgpl=false",
            "-Dlibmpv=true",
            "-Dlibavdevice=disabled",
            "-Dcplayer=false",
            "-Dlua=disabled",
            "-Diconv=disabled",
            "-Djavascript=disabled",
            "-Dmanpage-build=disabled",
        )
    }

}

val buildAndroidBinaries by tasks.registering {
    val tasks = androidTargets.map { "mesonMpv[android-${it.abiName}]" }
    dependsOn(*tasks.toTypedArray())
}
