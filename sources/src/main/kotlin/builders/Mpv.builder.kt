package builders

import base.AndroidABI
import base.Library
import tasks.AutoBuildTask
import tasks.CloneTask
import tasks.Task

class MpvBuilder(override val target: AndroidABI) : BaseBuilder() {

  override val lib: Library = Library.mpv

  override val cloneTask: Task = CloneTask(lib)

  override val buildTask: Task = AutoBuildTask(
    lib = lib,
    target = target,
    environmentVariables = emptyMap(),
    arguments = arrayOf(
      "--default-library", "shared",
      "-Dbuildtype=release",
      "-Dgpl=false",
      "-Dcplayer=false",
      "-Dlibmpv=true",
      "-Dtests=false",

      "-Dcdda=disabled",
      "-Dcplugins=disabled",
      "-Ddvbin=disabled",
      "-Ddvdnav=disabled",
      "-Diconv=disabled",
      "-Djavascript=disabled",
      "-Djpeg=disabled",
      "-Dlcms2=disabled",
      "-Dlibarchive=disabled",
      "-Dlibavdevice=disabled",
      "-Dlibbluray=disabled",
      "-Dlua=disabled",

      "-Dhtml-build=disabled",
      "-Dmanpage-build=disabled",
      "-Dpdf-build=disabled",
    ),
  )


}