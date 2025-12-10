plugins {
  alias(libs.plugins.kotlin.jvm)
}

group = "dcs-studio"
version = "1.1.0"

repositories { mavenCentral() }

kotlin { jvmToolchain(21) }

dependencies {
  implementation(libs.kotlinx.coroutines.core)
}