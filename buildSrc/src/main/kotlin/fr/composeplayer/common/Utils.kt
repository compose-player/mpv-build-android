package fr.composeplayer.common

import org.gradle.process.ExecOperations
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import java.io.File

fun ExecOperations.execExpectingSuccess(
  block: ExecSpec.() -> Unit
): ExecResult {
  val result = exec {
    standardOutput = System.out
    errorOutput = System.err
    block.invoke(this)
  }
  return result.rethrowFailure().assertNormalExitValue()
}

fun ExecSpec.addExports(
  platform: Platform,
  binariesDir: File,
) {
  this.environment["PKG_CONFIG_PATH"] = null
  this.environment["ANDROID_NDK_ROOT"] = null
  this.environment["ANDROID_SDK_ROOT"] = null
  this.environment["PKG_CONFIG_SYSROOT_DIR"] = binariesDir.absolutePath
  this.environment["PKG_CONFIG_LIBDIR"] = "${binariesDir.absolutePath}/lib/pkgconfig"
  this.environment["ANDROID_HOME"] = null
  this.environment["PATH"] = when (platform) {
    is Platform.Android -> "${platform.toolsChainDir.absolutePath}:${environment["PATH"]}"
  }
  this.environment["CXX"] = platform.CXX
  this.environment["CC"] = platform.CC
}