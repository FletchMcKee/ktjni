// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.ktjni.tasks

import javax.inject.Inject
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction

@CacheableTask
internal abstract class KtjniTask
@Inject
constructor() : KtjniWorkerTask() {
  @get:InputFiles
  @get:SkipWhenEmpty
  @get:PathSensitive(PathSensitivity.RELATIVE)
  abstract val sourceDir: ConfigurableFileCollection

  @get:OutputDirectory
  abstract val outputDir: DirectoryProperty

  @TaskAction
  fun generateJniHeaders() {
    workQueue().submit(GenerateJniHeaders::class.java) {
      sourceDir.from(this@KtjniTask.sourceDir)
      outputDir.set(this@KtjniTask.outputDir)
    }
  }
}
