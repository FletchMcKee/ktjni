// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.ktjni.internal

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ComponentIdentity
import com.android.build.api.variant.HasAndroidTest
import com.android.build.api.variant.HasUnitTest
import io.github.fletchmckee.ktjni.registerKtjniTask
import io.github.fletchmckee.ktjni.util.titleCase
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.compile.JavaCompile

internal fun Project.configureAndroidVariants(
  headerOutputDir: DirectoryProperty,
  aggregate: ConfigurableFileCollection,
) {
  val androidExtension = extensions.getByType(AndroidComponentsExtension::class.java)
  androidExtension.onVariants { variant ->
    findJavaCompilationTask(variant, headerOutputDir, aggregate)
    (variant as? HasUnitTest)?.unitTest?.let {
      findJavaCompilationTask(it, headerOutputDir, aggregate)
    }
    (variant as? HasAndroidTest)?.androidTest?.let {
      findJavaCompilationTask(it, headerOutputDir, aggregate)
    }
  }
}

private fun Project.findJavaCompilationTask(
  variant: ComponentIdentity,
  headerOutputDir: DirectoryProperty,
  aggregate: ConfigurableFileCollection,
) {
  val compileTaskName = "compile${variant.name.titleCase}JavaWithJavac"
  // Defers task lookup until execution since the task won't exist yet.
  val compileSourceDir = providers.provider {
    tasks.named(compileTaskName, JavaCompile::class.java)
  }.flatMap { taskProvider ->
    taskProvider.flatMap { task -> task.destinationDirectory }
  }

  registerKtjniTask(
    language = "java",
    sourceSetName = variant.name,
    compileSourceDir = compileSourceDir,
    headerOutputDir = headerOutputDir,
    aggregate = aggregate,
  )
}
