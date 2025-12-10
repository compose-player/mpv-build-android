package builders

import base.AndroidABI
import base.Library
import tasks.AutoBuildTask
import tasks.CloneTask
import tasks.Task
import utils.prefixDirectoryOf

class PlaceboBuilder(override val target: AndroidABI) : BaseBuilder() {

  override val lib: Library = Library.placebo

  override val cloneTask: Task = CloneTask(lib)

  override val buildTask: Task = AutoBuildTask(
    lib = lib,
    target = target,
    environmentVariables = emptyMap(),
    arguments = arrayOf(
      "-Dbuildtype=release",
      "-Dvulkan=disabled",
      "-Ddemos=false",
      "-Dtests=false",
    ),
    afterBuild = {
      val prefixDir = prefixDirectoryOf(lib, target)
      val pc = prefixDir.resolve("lib/pkgconfig/libplacebo.pc")
      val newText = pc.readLines()
        .joinToString(
          separator = "\n",
          transform = {
            when {
              it.startsWith("Libs: ") -> "$it -lc++"
              else -> it
            }
          }
        )
      pc.writeText(newText)
    },
  )

}
