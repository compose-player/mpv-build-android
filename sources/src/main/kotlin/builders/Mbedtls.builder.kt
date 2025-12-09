package builders

import base.AndroidABI
import base.Library
import tasks.*
import utils.commandLine
import utils.sourceDirOf

class MbedtlsBuilder(override val target: AndroidABI) : BaseBuilder() {

  override val lib: Library = Library.mbedtls

  override val cloneTask: Task = CloneTask(lib)

  override val buildTask: Task = AutoBuildTask(
    lib = lib,
    target = target,
    environmentVariables = emptyMap(),
    arguments = arrayOf(
      "-DUSE_SHARED_MBEDTLS_LIBRARY=OFF",
      "-DUSE_STATIC_MBEDTLS_LIBRARY=ON",
    ),
    beforeBuild = {
      commandLine {
        val config = sourceDirOf(lib).resolve("scripts/config.py").absolutePath
        command = when (target) {
          AndroidABI.x86 -> arrayOf(config, "unset", "MBEDTLS_AESNI_C")
          AndroidABI.arm32, AndroidABI.arm64, AndroidABI.x86_64 -> arrayOf(config, "set", "MBEDTLS_AESNI_C")
        }
      }
    },
  )

}