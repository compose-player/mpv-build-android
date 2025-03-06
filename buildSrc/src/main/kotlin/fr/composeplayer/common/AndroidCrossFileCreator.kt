package fr.composeplayer.common

import org.gradle.api.Project
import java.io.File

object AndroidCrossFileCreator : CrossFileCreator<Platform.Android> {

  override fun create(
    project: Project,
    platform: Platform.Android
  ): File {
    val crossFilesDir = File(project.rootDir, "cross-files").apply(File::mkdirs)
    val file = File(crossFilesDir, "android-${platform.abi.abiName}.txt")
    if (file.exists()) return file
    val cpu = platform.abi.ndkTriple.split("-").first()

    val content = """
      [built-in options]
      buildtype = 'release'
      default_library = 'static'
      wrap_mode = 'nodownload'
      
      [binaries]
      c = '${platform.abi.clang}'
      cpp = '${platform.abi.cpp}'
      ar = 'llvm-ar'
      nm = 'llvm-nm'
      strip = 'llvm-strip'
      pkg-config = 'pkg-config'
      
      [host_machine]
      system = 'android'
      cpu_family = '${platform.abi.cpuFamily}'
      cpu = '${cpu}'
      endian = 'little'
    """
    return file.apply { writeText(content.trimIndent()) }
  }

}