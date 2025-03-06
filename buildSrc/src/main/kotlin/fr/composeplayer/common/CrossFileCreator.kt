package fr.composeplayer.common

import org.gradle.api.Project
import java.io.File

interface CrossFileCreator<T : Platform> {

  fun create(project: Project, platform: T): File

}

val MesonTask.crossFile: File
  get() = when (val platform = this.platform) {
    is Platform.Android -> AndroidCrossFileCreator.create(project, platform)
  }