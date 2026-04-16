// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.ktjni.internal

import io.github.fletchmckee.ktjni.registerKtjniTask
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

internal fun Project.configureKotlinMultiplatform(
  headerOutputDir: DirectoryProperty,
  aggregate: ConfigurableFileCollection,
) {
  val kotlinExtension = extensions.getByType(KotlinMultiplatformExtension::class.java)
  kotlinExtension.targets.configureEach {
    compilations.configureEach {
      // The only KMP platforms we need registrations for are `jvm` or `androidJvm`.
      if (platformType == KotlinPlatformType.jvm || platformType == KotlinPlatformType.androidJvm) {
        registerKtjniTask(
          language = "kotlin",
          sourceSetName = name,
          compileSourceDir = output.classesDirs,
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
    registerKtjniTask(
      language = "kotlin",
      sourceSetName = name,
      compileSourceDir = output.classesDirs,
      headerOutputDir = headerOutputDir,
      aggregate = aggregate,
    )
  }
}

internal fun Project.configureKotlinAndroid(
  headerOutputDir: DirectoryProperty,
  aggregate: ConfigurableFileCollection,
) {
  val kotlinExtension = extensions.getByType(KotlinAndroidProjectExtension::class.java)
  kotlinExtension.target.compilations.configureEach {
    registerKtjniTask(
      language = "kotlin",
      sourceSetName = name,
      compileSourceDir = output.classesDirs,
      headerOutputDir = headerOutputDir,
      aggregate = aggregate,
    )
  }
}
