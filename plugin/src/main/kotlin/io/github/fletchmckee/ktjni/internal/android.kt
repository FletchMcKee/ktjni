// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.ktjni.internal

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Component
import io.github.fletchmckee.ktjni.registerKtjniTask
import io.github.fletchmckee.ktjni.util.titleCase
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

internal fun Project.configureAndroidVariants(
  headerOutputDir: DirectoryProperty,
  aggregate: ConfigurableFileCollection,
) {
  val androidExtension = extensions.getByType(AndroidComponentsExtension::class.java)
  androidExtension.onVariants { variant ->
    configureKotlinAndroid(variant, headerOutputDir, aggregate)
    configureJavaAndroid(variant, headerOutputDir, aggregate)

    @Suppress("UnstableApiUsage")
    variant.nestedComponents.forEach { nested ->
      configureKotlinAndroid(nested, headerOutputDir, aggregate)
      configureJavaAndroid(nested, headerOutputDir, aggregate)
    }
  }
}

private fun Project.configureJavaAndroid(
  variant: Component,
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
    compileSourceDir = objects.fileCollection().from(compileSourceDir),
    headerOutputDir = headerOutputDir,
    aggregate = aggregate,
  )
}

private fun Project.configureKotlinAndroid(
  variant: Component,
  headerOutputDir: DirectoryProperty,
  aggregate: ConfigurableFileCollection,
) {
  val kotlinAndroid = project.extensions.findByType(KotlinAndroidExtension::class.java) ?: return
  kotlinAndroid.target.compilations.matching { it.name == variant.name }
    .configureEach {
      val compileSourceDir = compileTaskProvider.map { it as KotlinJvmCompile }
        .map { it.destinationDirectory }
      registerKtjniTask(
        language = "kotlin",
        sourceSetName = variant.name,
        compileSourceDir = objects.fileCollection().from(compileSourceDir),
        headerOutputDir = headerOutputDir,
        aggregate = aggregate,
      )
    }
}
