import fr.composeplayer.common.*
import fr.composeplayer.common.Dependency
import fr.composeplayer.common.Platform

plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "fr.composeplayer"
version = libs.versions.library.get()

val cloneDav1d by tasks.registering(CloneSource::class) { dependency = Dependency.Dav1d }

val androidTargets = AndroidAbi.values().toList()

configure(androidTargets) {

    val mesonDav1d by tasks.register(
        name = "mesonDav1d[android-${abiName}]",
        type = MesonTask::class,
    ) {
        this.dependsOn(cloneDav1d)
        this.dependency = Dependency.Dav1d
        this.platform = Platform.Android(this@configure)
        this.mesonArgs = listOf(
            "-Denable_tests=false",
            "-Db_lto=true",
            "-Dstack_alignment=16"
        )
    }

}

val buildAndroidBinaries by tasks.registering {
    val tasks = androidTargets.map { "mesonDav1d[android-${it.abiName}]" }
    dependsOn(*tasks.toTypedArray())
}
