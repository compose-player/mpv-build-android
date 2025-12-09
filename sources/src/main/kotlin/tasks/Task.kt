package tasks

import java.io.File

abstract class Task {

  companion object {
    val entryPointDir: File get() = System.getProperty("user.dir").let(::File)
    val productsDir: File get() = entryPointDir.resolve(".products")
  }

  abstract val skip: Boolean

  suspend fun execute() {
    if (skip) return
    try {
      executeInternal()
      onSuccess()
    } catch (error: Throwable) {
      onFailure(error)
      throw error
    }
  }

  protected abstract suspend fun executeInternal()

  open suspend fun onFailure(reason: Throwable) = Unit

  open suspend fun onSuccess() = Unit

  abstract suspend fun clean()

}