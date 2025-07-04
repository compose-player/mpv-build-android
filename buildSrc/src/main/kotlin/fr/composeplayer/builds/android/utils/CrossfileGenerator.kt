package fr.composeplayer.builds.android.utils

import fr.composeplayer.builds.android.build.BuildContext
import java.io.File

class CrossfileGenerator(val context: BuildContext) {

  private val cFlags: String
    get() = context.cFlags
      .distinct()
      .joinToString(
        separator = ", ",
        transform = { "'$it'" },
      )

  private val ldFlags: String
    get() = context.ldFlags
      .distinct()
      .joinToString(
        separator = ", ",
        transform = { "'$it'" },
      )

  fun delete() {
    val file = context.project.rootDir.resolve("crossfiles/${context.component}-${context.target}.pc")
    file.delete()
  }

  fun generate(): File {
    val file = context.project.rootDir.resolve("crossfiles/${context.component}-${context.target}.pc")
    val (cpu) = context.target.ndkTriple.split("-")
    val content = """
      [built-in options]
      buildtype = 'release'
      default_library = 'static'
      wrap_mode = 'nodownload'
      prefix = '${context.prefixDirectory.absolutePath}'
      c_args = [$cFlags]
      cpp_args = [$cFlags]
      c_link_args = [$ldFlags]
      cpp_link_args = [$ldFlags]
      
      [binaries]
      c = '${context.toolsChainDirectory.resolve(context.target.clang).absolutePath}'
      cpp = '${context.toolsChainDirectory.resolve(context.target.cpp).absolutePath}'
      ar = '${context.toolsChainDirectory.resolve("llvm-ar").absolutePath}'
      nm = '${context.toolsChainDirectory.resolve("llvm-nm").absolutePath}'
      strip = '${context.toolsChainDirectory.resolve("llvm-strip").absolutePath}'
      pkg-config = 'pkg-config'
      
      [host_machine]
      system = 'android'
      cpu_family = '${context.target.cpuFamily}'
      cpu = '${cpu}'
      endian = 'little'
    """
    return file
      .apply { parentFile.mkdirs() }
      .apply { writeText(content.trimIndent()) }
  }

}

