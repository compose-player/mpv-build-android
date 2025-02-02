import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.dsl)
    alias(libs.plugins.kotlin.jvm)
}

repositories {
    mavenCentral()
}

val LibrariesForLibs.VersionAccessors.javaVersion: JavaVersion
    get() = JavaVersion.toVersion( this.jdk.get() )

val LibrariesForLibs.VersionAccessors.jvmTarget: JvmTarget
    get() = JvmTarget.fromTarget( this.jdk.get() )

java {
    sourceCompatibility = libs.versions.javaVersion
    targetCompatibility = libs.versions.javaVersion
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions.jvmTarget = libs.versions.jvmTarget
}

dependencies {
}