import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  alias(libs.plugins.kotlin.dsl)
  alias(libs.plugins.kotlin.jvm)
}

repositories { mavenCentral() }


java {
  sourceCompatibility = JavaVersion.VERSION_23
  targetCompatibility = JavaVersion.VERSION_23
}

tasks.withType<KotlinCompile> {
  compilerOptions.jvmTarget = JvmTarget.JVM_23
}

sourceSets {
  val main by getting {
    val location = layout.projectDirectory.asFile.resolve("src/main/kotlin")
    kotlin.srcDirs.add(location)
    java.srcDirs.add(location)
  }
}