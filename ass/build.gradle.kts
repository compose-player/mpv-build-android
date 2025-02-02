import fr.composeplayer.common.*
import fr.composeplayer.common.Dependency
import fr.composeplayer.common.Platform

plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "fr.composeplayer"
version = libs.versions.library.get()

val cloneFribidi by tasks.registering(CloneSource::class) { dependency = Dependency.Fribidi }
val cloneHarfbuzz by tasks.registering(CloneSource::class) { dependency = Dependency.Harfbuzz }
val cloneFreetype by tasks.registering(CloneSource::class) { dependency = Dependency.Freetype }
val cloneAss by tasks.registering(CloneSource::class) { dependency = Dependency.Ass }

val androidTargets = AndroidAbi.values().toList()

configure(androidTargets) {

    val mesonFreetype by tasks.register(
        name = "mesonFreetype[android-$abiName]",
        type = MesonTask::class,
    ) {
        this.dependsOn(cloneFreetype)
        this.platform = Platform.Android(this@configure)
        this.dependency = Dependency.Freetype
        this.mesonArgs = listOf()
    }

    val mesonFribidi by tasks.register(
        name = "mesonFribidi[android-$abiName]",
        type = MesonTask::class,
    ) {
        this.dependsOn(cloneFribidi)
        this.platform = Platform.Android(this@configure)
        this.dependency = Dependency.Fribidi
        this.mesonArgs = listOf("-Dtests=false", "-Ddocs=false")
    }

    val mesonHarfbuzz by tasks.register(
        name = "mesonHarfbuzz[android-$abiName]",
        type = MesonTask::class,
    ) {
        this.dependsOn(cloneHarfbuzz)
        this.platform = Platform.Android(this@configure)
        this.dependency = Dependency.Harfbuzz
        this.mesonArgs = listOf(
            "-Dtests=disabled",
            "-Ddocs=disabled",
            "-Dglib=disabled",
            "-Dcairo=disabled",
        )
    }

    val autogenAss by tasks.register(
        name = "autogenAss[android-$abiName]",
        type = AutoGenTask::class,
    ) {
        this.dependsOn(cloneAss, mesonFreetype, mesonFribidi, mesonHarfbuzz)
        this.dependency = Dependency.Ass
    }

    val configureAss by tasks.register(
        name = "configureAss[android-$abiName]",
        type = ConfigureTask::class,
    ) {
        this.dependsOn(autogenAss)
        this.platform = Platform.Android(this@configure)
        this.dependency = Dependency.Ass
        this.configureArgs = listOf(
            "CFLAGS=-fPIC",
            "CXXFLAGS=-fPIC",
            "--host=${this@configure.ndkTriple}",
            "--with-pic",
            "--disable-asm",
            "--enable-static",
            "--disable-shared",
            "--disable-require-system-font-provider",
        )
    }

    val makeAss by tasks.register(
        name = "makeAss[android-$abiName]",
        type = MakeTask::class,
    ) {
        this.dependsOn(configureAss)
        this.platform = Platform.Android(this@configure)
        this.dependency = Dependency.Ass
    }

}

val buildAndroidBinaries by tasks.registering {
    val tasks = androidTargets.map { "makeAss[android-${it.abiName}]" }
    dependsOn(*tasks.toTypedArray())
}
