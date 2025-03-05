plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.dsl) apply false
}

val checkEnv by tasks.registering {
    doLast {
        val toto = System.getProperty("ANDROID_NDK_HOME")
        println("toto = ${toto}")
        val ndk = System.getenv("ANDROID_NDK_HOME")
        println("ndk = ${ndk}")
    }
}