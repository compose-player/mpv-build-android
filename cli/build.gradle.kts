
plugins {
  alias(libs.plugins.kotlin.jvm)
  id("kotlin-kapt")
  application
}

group = "dcs-studio"
version = "1.1.0"

repositories { mavenCentral() }

kotlin {
  jvmToolchain(21)
}

kapt {
  arguments {
    arg("project", "${project.group}/${project.name}")
  }
}

dependencies {
  implementation(projects.sources)
  implementation(libs.kotlinx.coroutines.core)
  implementation("info.picocli:picocli:4.7.7")
  implementation("net.lingala.zip4j:zip4j:2.11.5")
  kapt("info.picocli:picocli-codegen:4.7.7")
}

application {
  mainClass = "cli.MpvCliBuilder"
}

tasks.jar {
  archiveFileName = "mpv-builder-cli.jar"
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
  manifest {
    attributes["Main-Class"] = application.mainClass
  }
  val cp = configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
  from(cp)
}