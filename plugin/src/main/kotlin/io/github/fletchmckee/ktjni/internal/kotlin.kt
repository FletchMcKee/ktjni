// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.ktjni.internal

import io.github.fletchmckee.ktjni.registerKtjniTask
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

internal fun Project.configureKotlinMultiplatform(
  headerOutputDir: DirectoryProperty,
  aggregate: ConfigurableFileCollection,
) {
  val kotlinExtension = extensions.getByType(KotlinMultiplatformExtension::class.java)
  kotlinExtension.targets.configureEach {
    compilations.configureEach {
      // The only KMP platforms we need registrations for are `jvm` or `androidJvm`.
      if (platformType == KotlinPlatformType.jvm || platformType == KotlinPlatformType.androidJvm) {
        val compileSourceDir = compileTaskProvider.map { it as KotlinJvmCompile }
          .map { it.destinationDirectory }
        registerKtjniTask(
          language = "kotlin",
          sourceSetName = name,
          compileSourceDir = objects.fileCollection().from(compileSourceDir),
          headerOutputDir = headerOutputDir,
          aggregate = aggregate,
          target = target.name,
        )
      }
    }
  }
}

internal fun Project.configureKotlinJvm(
  headerOutputDir: DirectoryProperty,
  aggregate: ConfigurableFileCollection,
) {
  val kotlinExtension = extensions.getByType(KotlinJvmProjectExtension::class.java)
  kotlinExtension.target.compilations.configureEach {
    val compileSourceDir = compileTaskProvider.map { it as KotlinJvmCompile }
      .map { it.destinationDirectory }
    registerKtjniTask(
      language = "kotlin",
      sourceSetName = name,
      compileSourceDir = objects.fileCollection().from(compileSourceDir),
      headerOutputDir = headerOutputDir,
      aggregate = aggregate,
    )
  }
}
