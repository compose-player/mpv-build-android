package fr.composeplayer.builds.android.tasks

import fr.composeplayer.builds.android.build.AndroidArchitecture
import fr.composeplayer.builds.android.build.BuildContext
import fr.composeplayer.builds.android.ProjectUtils
import fr.composeplayer.builds.android.build.BuildContext.Companion.buildContext
import fr.composeplayer.builds.android.build.Component
import fr.composeplayer.builds.android.utils.exists
import fr.composeplayer.builds.android.utils.CrossfileGenerator
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.gradle.kotlin.dsl.registering

fun Project.registerGenericBuild(
  group: String = "generic build",
  clone: Clone.() -> Unit = {},
  buildTargets: List<AndroidArchitecture> = ProjectUtils.BUILD_TARGETS,
  component: Component,
  prebuild: Task.(target: AndroidArchitecture) -> Unit = { enabled = false },
  build: AutoBuild.() -> Unit,
  postBuild: Task.(target: AndroidArchitecture) -> Unit = { enabled = false },
) {

  val buildAllTask by tasks.register("build[all]") {
    this.group = group
  }

  val clean by tasks.getting {
    doLast {
      for (target in buildTargets) {
        val context = BuildContext(this@registerGenericBuild, component, target)
        CrossfileGenerator(context).delete()
        context.sourceDirectory.deleteRecursively()
        context.buildDirectory.parentFile.deleteRecursively()
        context.prefixDirectory.parentFile.deleteRecursively()
      }
    }
  }

  val cloneTask by tasks.register(
    /* name = */  "clone",
    /* type = */  Clone::class.java,
    /* configurationAction = */  {
      clone()
      this.group = group
      if (enabled) applyFrom(component)
    },
  )

  for (target in buildTargets) {

    val prebuildTask by tasks.register(
      /* name = */  "preBuild[$target]",
      /* type = */  Task::class.java,
      /* configurationAction = */  {
        this.group = group
        prebuild.invoke(this, target)
      },
    )
    val buildTask by tasks.register(
      /* name = */  "build[$target]",
      /* type = */  AutoBuild::class.java,
      /* configurationAction = */  {
        this.group = group
        this.component.set(component)
        this.target.set(target)
        this.enabled = !buildContext(component, target).prefixDirectory.exists
        build.invoke(this)
      },
    )

    val postBuildTask by tasks.register(
      /* name = */  "postBuild[$target]",
      /* type = */  Task::class.java,
      /* configurationAction = */  {
        this.group = group
        postBuild.invoke(this, target)
      },
    )

    prebuildTask.dependsOn(cloneTask)
    buildTask.dependsOn(prebuildTask)
    buildTask.finalizedBy(postBuildTask)
    buildAllTask.dependsOn(buildTask)

  }

  rootProject.tasks.register(
    /* name = */ "assemble[$name]",
    /* type = */ Task::class.java,
    /* configurationAction = */ {
      this.group = "mpv-build"
      buildAllTask.mustRunAfter(cloneTask)
      dependsOn(cloneTask, buildAllTask)
    }
  )


}