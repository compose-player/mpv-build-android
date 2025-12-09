package utils

import base.*
import tasks.Task
import java.io.File

class CrossFileGenerator(val target: AndroidABI, val lib: Library) {

  val file: File
    get() = Task.productsDir.resolve("cross-files/${lib.name}-${target.ABI_NAME}.crossfile")

  fun delete() = file.delete()

  private val prefixDir = prefixDirectoryOf(lib, target)

  private val pkgConfigLibdir: String
    get() = buildString {
      val entries = Library.entries
      for (i in entries.indices) {
        val lib = entries[i]
        val pkgConfig = prefixDirectoryOf(lib, target).resolve("lib/pkgconfig")
        append(pkgConfig)
        if (i < indices.last) append(":")
      }
    }

  fun generate(): File {
    val (cpu) = target.NDK_TRIPLE.split("-")
    val content = """
      [built-in options]
      buildtype = 'release'
      default_library = 'static'
      wrap_mode = 'nodownload'
      prefix = '${prefixDir.absolutePath}'
      c_args = []
      cpp_args = []
      c_link_args = ['-Wl,-z,max-page-size=16384']
      cpp_link_args = ['-Wl,-z,max-page-size=16384']
      
      [binaries]
      c = '${TOOLCHAINS_DIR.resolve(target.CLANG).absolutePath}'
      cpp = '${TOOLCHAINS_DIR.resolve(target.CPP).absolutePath}'
      ar = '${TOOLCHAINS_DIR.resolve("llvm-ar").absolutePath}'
      nm = '${TOOLCHAINS_DIR.resolve("llvm-nm").absolutePath}'
      strip = '${TOOLCHAINS_DIR.resolve("llvm-strip").absolutePath}'
      pkg-config = 'pkg-config'
      
      [host_machine]
      system = 'android'
      cpu_family = '${target.CPU_FAMILY}'
      cpu = '${cpu}'
      endian = 'little'
    """
    return file
      .apply { parentFile.mkdirs() }
      .apply { writeText(content.trimIndent()) }
  }

}
