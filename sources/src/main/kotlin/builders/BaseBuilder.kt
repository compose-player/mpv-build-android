package builders

import base.AndroidABI
import base.Library
import tasks.Task

sealed class BaseBuilder {

  abstract val lib: Library
  abstract val target: AndroidABI
  abstract val cloneTask: Task
  abstract val buildTask: Task

  open suspend fun cleanSources() = cloneTask.clean()

  open suspend fun cleanBuilds() = buildTask.clean()

  open suspend fun build() {
    cloneTask.execute()
    buildTask.execute()
  }

  companion object {

    fun forLib(lib: Library, target: AndroidABI): BaseBuilder {
      return when (lib) {
        Library.ffmpeg -> FFmpegBuilder(target)
        Library.dav1d -> Dav1dBuilder(target)
        Library.placebo -> PlaceboBuilder(target)
        Library.mbedtls -> MbedtlsBuilder(target)
        Library.mpv -> MpvBuilder(target)
        Library.freetype -> FreetypeBuilder(target)
        Library.harfbuzz -> HarfbuzzBuilder(target)
        Library.fribidi -> FribidiBuilder(target)
        Library.ass -> AssBuilder(target)
      }
    }
  }

}

