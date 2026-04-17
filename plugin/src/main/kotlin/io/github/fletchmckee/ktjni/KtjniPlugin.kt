// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.ktjni

import io.github.fletchmckee.ktjni.internal.PluginId
import io.github.fletchmckee.ktjni.internal.configureAndroidVariants
import io.github.fletchmckee.ktjni.internal.configureKotlinJvm
import io.github.fletchmckee.ktjni.internal.configureKotlinMultiplatform
import io.github.fletchmckee.ktjni.tasks.KtjniTask
import io.github.fletchmckee.ktjni.util.GROUP
import io.github.fletchmckee.ktjni.util.taskSuffix
import io.github.fletchmckee.ktjni.util.titleCase
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.scala.ScalaCompile

@Suppress("Unused") // Invoked reflectively
public class KtjniPlugin : Plugin<Project> {
  override fun apply(target: Project): Unit = with(target) {
    val extension = extensions.create("ktjni", KtjniExtension::class.java)
    // Connects all header task outputs as inputs to this aggregator task.
    val aggregate = objects.fileCollection()

    tasks.register("generateJniHeaders") {
      group = GROUP
      description = "Generates JNI headers for all JVM compile tasks"

      // Using `inputs.files` instead of `dependsOn` allows for better up-to-date checking.
      inputs.files(aggregate)
    }

    val defaultBuildDir = project.layout.buildDirectory.dir("generated/ktjni")
    val headerOutputDir = extension.outputDir.convention(defaultBuildDir)

    configureAndroid(headerOutputDir, aggregate)
    configureKotlin(headerOutputDir, aggregate)
    configureJava(headerOutputDir, aggregate)
    configureScala(headerOutputDir, aggregate)
  }

  private fun Project.configureAndroid(
    headerOutputDir: DirectoryProperty,
    aggregate: ConfigurableFileCollection,
  ) = listOf(
    PluginId.AndroidLibrary,
    PluginId.AndroidApplication,
  ).forEach {
    pluginManager.withPlugin(it.id) {
      configureAndroidVariants(headerOutputDir, aggregate)
    }
  }

  private fun Project.configureKotlin(
    headerOutputDir: DirectoryProperty,
    aggregate: ConfigurableFileCollection,
  ) {
    plugins.withId(PluginId.KotlinMultiplatform.id) {
      configureKotlinMultiplatform(headerOutputDir, aggregate)
    }

    plugins.withId(PluginId.KotlinJvm.id) {
      configureKotlinJvm(headerOutputDir, aggregate)
    }
  }

  private fun Project.configureJava(
    headerOutputDir: DirectoryProperty,
    aggregate: ConfigurableFileCollection,
  ) = pluginManager.withPlugin(PluginId.Java.id) {
    val javaExtension = extensions.getByType(JavaPluginExtension::class.java)
    javaExtension.sourceSets.configureEach {
      val compileSourceDir = tasks.named(compileJavaTaskName, JavaCompile::class.java)
        .flatMap { it.destinationDirectory }

      registerKtjniTask(
        language = "java",
        sourceSetName = name,
        compileSourceDir = objects.fileCollection().from(compileSourceDir),
        headerOutputDir = headerOutputDir,
        aggregate = aggregate,
      )
    }
  }

  private fun Project.configureScala(
    headerOutputDir: DirectoryProperty,
    aggregate: ConfigurableFileCollection,
  ) = pluginManager.withPlugin(PluginId.Scala.id) {
    // The scala plugin also applies the java plugin.
    val javaExtension = extensions.getByType(JavaPluginExtension::class.java)
    javaExtension.sourceSets.configureEach {
      val compileSourceDir = tasks.named(getCompileTaskName("scala"), ScalaCompile::class.java)
        .flatMap { it.destinationDirectory }

      registerKtjniTask(
        language = "scala",
        sourceSetName = name,
        compileSourceDir = objects.fileCollection().from(compileSourceDir),
        headerOutputDir = headerOutputDir,
        aggregate = aggregate,
      )
    }
  }
}

internal fun Project.registerKtjniTask(
  language: String,
  sourceSetName: String,
  compileSourceDir: ConfigurableFileCollection,
  headerOutputDir: DirectoryProperty,
  aggregate: ConfigurableFileCollection,
  target: String = "", // For Kotlin Multiplatform
) {
  val taskSuffix = sourceSetName.taskSuffix(target)
  val taskName = "generate${language.titleCase}${taskSuffix.titleCase}JniHeaders"

  val generateJniHeadersTask = tasks.register(taskName, KtjniTask::class.java) {
    sourceDir.from(compileSourceDir)
    outputDir.set(headerOutputDir.map { it.dir("$language/$taskSuffix") })

    group = GROUP
    description = "Generates $language JNI headers from class files for $sourceSetName compilation."

    doFirst {
      logger.info("Ktjni - running $taskName")
    }
  }

  aggregate.from(generateJniHeadersTask.flatMap { it.outputDir })
}
