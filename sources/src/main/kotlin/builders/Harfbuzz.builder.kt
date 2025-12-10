package builders

import base.AndroidABI
import base.Library
import tasks.AutoBuildTask
import tasks.CloneTask
import tasks.Task

class HarfbuzzBuilder(override val target: AndroidABI) : BaseBuilder() {

  override val lib: Library = Library.harfbuzz

  override val cloneTask: Task = CloneTask(lib)

  override val buildTask: Task = AutoBuildTask(
    lib = lib,
    target = target,
    environmentVariables = emptyMap(),
    arguments = arrayOf(
      "-Dbuildtype=release",
      "-Dtests=disabled",
      "-Ddocs=disabled",
      "-Dfreetype=enabled",
    ),
  )


}