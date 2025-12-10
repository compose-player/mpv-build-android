package builders

import base.AndroidABI
import base.Library
import tasks.AutoBuildTask
import tasks.CloneTask
import tasks.Task

class Dav1dBuilder(override val target: AndroidABI) : BaseBuilder() {

  override val lib: Library = Library.dav1d

  override val cloneTask: Task = CloneTask(lib)

  override val buildTask: Task = AutoBuildTask(
    lib = lib,
    target = target,
    environmentVariables = emptyMap(),
    arguments = arrayOf(
      "-Denable_tests=false",
      "-Db_lto=true",
      "-Dstack_alignment=16",
    ),
  )


}