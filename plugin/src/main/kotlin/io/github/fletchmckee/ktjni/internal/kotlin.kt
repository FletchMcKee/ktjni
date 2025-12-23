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
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompile

internal fun Project.configureKotlinMultiplatform(
  headerOutputDir: DirectoryProperty,
  aggregate: ConfigurableFileCollection,
) {
  val kotlinExtension = extensions.getByType(KotlinMultiplatformExtension::class.java)
  kotlinExtension.targets.configureEach {
    compilations.configureEach {
      // The only KMP platforms we need registrations for are `jvm` or `androidJvm`.
      if (platformType.name == "jvm" || platformType.name == "androidJvm") {
        val compileSourceDir = compileTaskProvider.flatMap { compileTask ->
          (compileTask as AbstractKotlinCompile<*>).destinationDirectory
        }

        registerKtjniTask(
          language = "kotlin",
          sourceSetName = name,
          compileSourceDir = compileSourceDir,
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
    val compileSourceDir = compileTaskProvider.flatMap { compileTask ->
      (compileTask as AbstractKotlinCompile<*>).destinationDirectory
    }

    registerKtjniTask(
      language = "kotlin",
      sourceSetName = name,
      compileSourceDir = compileSourceDir,
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
    val compileSourceDir = compileTaskProvider.flatMap { compileTask ->
      (compileTask as AbstractKotlinCompile<*>).destinationDirectory
    }

    registerKtjniTask(
      language = "kotlin",
      sourceSetName = name,
      compileSourceDir = compileSourceDir,
      headerOutputDir = headerOutputDir,
      aggregate = aggregate,
    )
  }
}
