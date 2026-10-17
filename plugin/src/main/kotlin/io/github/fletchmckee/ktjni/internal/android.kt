// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.ktjni.internal

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import io.github.fletchmckee.ktjni.KtjniExtension
import io.github.fletchmckee.ktjni.registerKtjniTask
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile

context(ktjniExtension: KtjniExtension)
internal fun Project.configureAndroid() = listOf(
  "com.android.library",
  "com.android.application",
  "com.android.kotlin.multiplatform.library",
).forEach { id ->
  pluginManager.withPlugin(id) {
    configureAndroidVariants()
  }
}

context(ktjniExtension: KtjniExtension)
private fun Project.configureAndroidVariants() {
  val androidExtension = extensions.getByType(AndroidComponentsExtension::class.java)
  androidExtension.onVariants { variant ->
    @Suppress("UnstableApiUsage")
    variant.components.forEach { nested ->
      val classDirs = objects.listProperty(Directory::class.java)
      val ktjniTask = registerKtjniTask(nested.name, files(classDirs))

      nested.artifacts.forScope(ScopedArtifacts.Scope.PROJECT)
        .use(ktjniTask)
        .toGet(
          type = ScopedArtifact.CLASSES,
          inputJars = { objects.listProperty(RegularFile::class.java) },
          inputDirectories = { classDirs },
        )
    }
  }
}
