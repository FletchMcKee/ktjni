// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.ktjni

import io.github.fletchmckee.ktjni.internal.configureAndroid
import io.github.fletchmckee.ktjni.internal.configureKotlinMultiplatform
import io.github.fletchmckee.ktjni.tasks.KtjniTask
import io.github.fletchmckee.ktjni.util.GROUP
import io.github.fletchmckee.ktjni.util.titleCase
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.TaskProvider

@Suppress("Unused") // Invoked reflectively
public class KtjniPlugin : Plugin<Project> {
  override fun apply(target: Project): Unit = with(target) {
    val extension = extensions.create("ktjni", KtjniExtension::class.java)
      .apply { outputDir.convention(layout.buildDirectory.dir("generated/ktjni")) }

    tasks.register("generateJniHeaders") {
      group = GROUP
      description = "Generates JNI headers for all JVM compilations"
    }

    context(extension) {
      configureAndroid()
      configureKotlinMultiplatform()
      configureJvm()
    }
  }

  context(ktjniExtension: KtjniExtension)
  private fun Project.configureJvm() = pluginManager.withPlugin("org.gradle.java-base") {
    val javaExtension = extensions.getByType(JavaPluginExtension::class.java)
    javaExtension.sourceSets.configureEach {
      registerKtjniTask(name, output.classesDirs)
    }
  }
}

context(ktjniExtension: KtjniExtension)
internal fun Project.registerKtjniTask(
  sourceSetName: String,
  classesDirs: FileCollection,
): TaskProvider<KtjniTask> {
  val taskName = "generate${sourceSetName.titleCase}JniHeaders"
  return tasks.register(taskName, KtjniTask::class.java) {
    sourceDir.from(classesDirs)
    outputDir.set(ktjniExtension.outputDir.map { it.dir(sourceSetName) })

    group = GROUP
    description = "Generates JNI headers from class files for $sourceSetName compilation."

    doFirst {
      logger.info("Ktjni - running $taskName")
    }
  }.also { ktjniTask -> tasks.named("generateJniHeaders") { inputs.files(ktjniTask) } }
}
