// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.ktjni.util

import com.google.common.truth.Truth.assertThat
import java.io.File
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome

internal val KtjniVersion: String get() = System.getProperty("ktjniVersion")
internal val AgpVersion: String get() = System.getProperty("agpVersion")
internal val KgpVersion: String get() = System.getProperty("kgpVersion")
internal val ScalaVersion: String get() = System.getProperty("scalaVersion")

internal fun assertRuns(
  projectDir: File,
  compatibleMatrix: CompatibleMatrix,
  android: Boolean = true,
  builtInKotlin: Boolean = true,
  assert: (firstRun: BuildResult, secondRun: BuildResult) -> Unit,
) {
  val firstRun = createTestRunner(
    projectDir = projectDir,
    android = android,
    builtInKotlin = builtInKotlin,
    gradleVersion = compatibleMatrix.gradle,
  ).assertAggregateOutcome(TaskOutcome.SUCCESS)

  val secondRun = createTestRunner(
    projectDir = projectDir,
    android = android,
    builtInKotlin = builtInKotlin,
    gradleVersion = compatibleMatrix.gradle,
  ).assertAggregateOutcome(TaskOutcome.UP_TO_DATE)
    .assertConfigurationCacheReused()

  assert(firstRun, secondRun)
}

internal fun BuildResult.assertAggregateOutcome(outcome: TaskOutcome = TaskOutcome.SUCCESS): BuildResult {
  assertThat(task(":generateJniHeaders")?.outcome).isEqualTo(outcome)
  return this
}

internal inline fun <reified T : Language> BuildResult.assertVariantOutcome(
  outcome: TaskOutcome? = TaskOutcome.SUCCESS,
  vararg variants: String,
): BuildResult {
  variants.forEach { variant ->
    assertThat(task(":generate${T::class.simpleName}${variant.titleCase}JniHeaders")?.outcome)
      .isEqualTo(outcome)
  }
  return this
}

internal inline fun <reified T : Language> assertVariantHeaders(
  projectDir: File,
  vararg variants: String,
) {
  variants.forEach { variant ->
    assertHeaders(projectDir, "build/generated/ktjni/${T::class.simpleName?.lowercase()}/$variant")
  }
}

internal fun GradleRunner.withCommonConfiguration(projectRoot: File): GradleRunner {
  File(projectRoot, "gradle.properties").writeText(
    """
      org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=1g
    """.trimIndent(),
  )
  return withProjectDir(projectRoot)
    .withTestKitDir(File("build/gradle-test-kit").absoluteFile)
}

internal fun File.writeCommonSettingsFile(localCacheDir: File) = writeText(
  """
  rootProject.name = "test-project"

  pluginManagement {
    repositories {
      maven { url = uri("${System.getProperty("localRepoPath")}") }
      mavenCentral()
      google()
      gradlePluginPortal()
    }
  }

  plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
  }

  dependencyResolutionManagement {
    repositories {
      mavenLocal()
      mavenCentral()
      google()
    }
  }

  buildCache {
    local {
      directory = file("${localCacheDir.toURI()}")
    }
  }

  """.trimIndent(),
)

internal fun File.writeKotlinExampleFile(
  sourceSet: String = "main",
  extension: String = "kt",
) {
  val srcDir = File(this, "src/$sourceSet/kotlin/com/example").apply { mkdirs() }
  File(srcDir, "Example.$extension").writeText(
    """
  package com.example

  class Example {
    external fun exampleNative(): Int
  }

    """.trimIndent(),
  )
}

internal fun File.writeJavaExampleFile(sourceSet: String = "main") {
  val srcDir = File(this, "src/$sourceSet/java/com/example").apply { mkdirs() }
  File(srcDir, "Example.java").writeText(
    """
    package com.example;

    public class Example {
      public native int exampleNative();
    }

    """.trimIndent(),
  )
}

internal fun File.writeScalaExampleFile(sourceSet: String = "main") {
  val srcDir = File(this, "src/$sourceSet/scala/com/example").apply { mkdirs() }
  File(srcDir, "Example.scala").writeText(
    """
    package com.example

    class Example {
      @native
      def exampleNative(): Int
    }

    """.trimIndent(),
  )
}

internal fun BuildResult.assertConfigurationCacheReused(): BuildResult {
  assertThat(output).contains("Reusing configuration cache")
  return this
}

private fun createTestRunner(
  projectDir: File,
  android: Boolean = false,
  builtInKotlin: Boolean = true,
  gradleVersion: String? = null,
  vararg tasks: String = arrayOf("--configuration-cache"),
): BuildResult = GradleRunner.create()
  .apply {
    forwardOutput()
    when {
      android -> withAndroidConfiguration(projectDir, builtInKotlin)
      else -> withCommonConfiguration(projectDir)
    }
    withArguments(*arrayOf("generateJniHeaders") + tasks)
    gradleVersion?.let { withGradleVersion(it) }
    withDebug(true)
  }.build()

private fun assertHeaders(parent: File, path: String) {
  val headerDir = File(parent, path)
  assertThat(headerDir.exists()).isTrue()

  val headerFile = File(headerDir, "com_example_Example.h")
  assertThat(headerFile.exists()).isTrue()

  val headerContent = headerFile.readText()
  assertThat(headerContent).isEqualTo(expectedOutcome)
}

private val expectedOutcome = """
  /* DO NOT EDIT THIS FILE - it is machine generated */
  #include <jni.h>
  /* Header for class com_example_Example */

  #ifndef _Included_com_example_Example
  #define _Included_com_example_Example
  #ifdef __cplusplus
  extern "C" {
  #endif

  /*
   * Class:     com_example_Example
   * Method:    exampleNative
   * Signature: ()I
   */
  JNIEXPORT jint JNICALL Java_com_example_Example_exampleNative
    (JNIEnv *, jobject);

  #ifdef __cplusplus
  }
  #endif
  #endif

""".trimIndent()

internal fun String.withInvariantPathSeparators() = replace("\\", "/")
