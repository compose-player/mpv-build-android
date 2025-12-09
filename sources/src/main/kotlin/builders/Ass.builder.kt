package builders

import base.AndroidABI
import base.Library
import tasks.AutoBuildTask
import tasks.CloneTask
import tasks.Task

class AssBuilder(override val target: AndroidABI) : BaseBuilder() {

  override val lib: Library = Library.ass

  override val cloneTask: Task = CloneTask(lib)

  override val buildTask: Task = AutoBuildTask(
    lib = lib,
    target = target,
    environmentVariables = mapOf("ASFLAGS" to "-DPIC=1"),
    arguments = arrayOf(
      "-Ddefault_library=shared",
      "-Dbuildtype=release",
      "-Dtest=disabled",
      "-Dasm=disabled",
      "-Dlibunibreak=disabled",
      "-Dfontconfig=disabled",
      "-Drequire-system-font-provider=false",
      "-Dlarge-tiles=true",
      "-Db_pie=false"
    ),
  )

}
