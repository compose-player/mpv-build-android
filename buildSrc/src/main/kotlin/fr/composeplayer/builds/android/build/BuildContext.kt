package fr.composeplayer.builds.android.build

import org.gradle.api.Project
import org.gradle.api.Task
import java.io.File
import fr.composeplayer.builds.android.ProjectUtils

class BuildContext(
  val project: Project,
  val component: Component,
  val target: AndroidArchitecture,
) {

  val sourceDirectory = project.rootDir.resolve("vendor/${component}")
  val buildDirectory = project.rootDir.resolve("builds/$component/${target}")
  val prefixDirectory = project.rootDir.resolve("binaries/$component/$target")

  val ndkDirectory: File
    get() {
      val path = System.getenv("ANDROID_NDK_HOME")
      return when {
        path.isNullOrBlank() -> {
          val user = System.getProperty("user.name")
          File("/Users/$user/Library/Android/sdk/ndk/${ProjectUtils.NDK_VERSION}")
        }
        else -> File(path)
      }
    }
  val toolsChainDirectory = ndkDirectory.resolve("toolchains/llvm/prebuilt/${WorkingMachine.arch}/bin")


  val cFlags: List<String>
    get() = buildList {  }

  val ldFlags: List<String>
    get() = buildList {  }

  companion object {

    fun Task.buildContext(
      component: Component,
      target: AndroidArchitecture,
    ): BuildContext = BuildContext(
      project = project,
      component = component,
      target = target,
    )

  }


}

