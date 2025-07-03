package fr.composeplayer.builds.android.utils

import java.io.File

val File.exists: Boolean
  get() = this.exists()