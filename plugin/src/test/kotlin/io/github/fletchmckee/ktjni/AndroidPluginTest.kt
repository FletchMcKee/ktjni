// Copyright 2026, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.ktjni

import assertk.all
import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.hasText
import io.github.fletchmckee.ktjni.util.ExpectedOutcome
import io.github.fletchmckee.ktjni.util.taskOutcomes
import java.io.File
import kotlin.test.Test
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome

class AndroidPluginTest {
  @Test fun latestKotlinAndroidLibrary() { // builtInKotlin
    val projectDir = File(fixturesDir, "latest/kotlin-android-library")
    assertThat(createCleanTestRunner(projectDir)).all {
      taskOutcomes("generateJniHeaders", "generateDebugJniHeaders", "generateReleaseJniHeaders")
        .containsOnly(TaskOutcome.SUCCESS)
      taskOutcomes("generateDebugAndroidTestJniHeaders", "generateDebugUnitTestJniHeaders")
        .containsOnly(TaskOutcome.NO_SOURCE)
      taskOutcomes("generateReleaseAndroidTestJniHeaders", "generateReleaseUnitTestJniHeaders")
        .containsOnly(null)
    }

    assertThat(createTestRunner(projectDir))
      .taskOutcomes("generateJniHeaders", "generateDebugJniHeaders", "generateReleaseJniHeaders")
      .containsOnly(TaskOutcome.UP_TO_DATE)

    listOf("debug", "release").forEach { variant ->
      assertThat(File(projectDir, "build/generated/ktjni/$variant"))
        .transform { it.resolve("com_example_Example.h") }
        .hasText(ExpectedOutcome)
    }
  }

  @Test fun minKotlinAndroidLibrary() {
    val projectDir = File(fixturesDir, "min/kotlin-android-library")
    assertThat(createCleanTestRunner(projectDir)).all {
      taskOutcomes("generateJniHeaders", "generateDebugJniHeaders", "generateReleaseJniHeaders")
        .containsOnly(TaskOutcome.SUCCESS)
      // `releaseUnitTest` variant executes with AGP min.
      taskOutcomes("generateDebugAndroidTestJniHeaders", "generateDebugUnitTestJniHeaders", "generateReleaseUnitTestJniHeaders")
        .containsOnly(TaskOutcome.NO_SOURCE)
      taskOutcomes("generateReleaseAndroidTestJniHeaders")
        .containsOnly(null)
    }

    assertThat(createTestRunner(projectDir))
      .taskOutcomes("generateJniHeaders", "generateDebugJniHeaders", "generateReleaseJniHeaders")
      .containsOnly(TaskOutcome.UP_TO_DATE)

    listOf("debug", "release").forEach { variant ->
      assertThat(File(projectDir, "build/generated/ktjni/$variant"))
        .transform { it.resolve("com_example_Example.h") }
        .hasText(ExpectedOutcome)
    }
  }

  @Test fun latestKotlinAndroidApplication() { // builtInKotlin
    val projectDir = File(fixturesDir, "latest/kotlin-android-application")
    assertThat(createCleanTestRunner(projectDir)).all {
      taskOutcomes("generateJniHeaders", "generateDebugJniHeaders", "generateReleaseJniHeaders")
        .containsOnly(TaskOutcome.SUCCESS)
      taskOutcomes("generateDebugAndroidTestJniHeaders", "generateDebugUnitTestJniHeaders")
        .containsOnly(TaskOutcome.NO_SOURCE)
      taskOutcomes("generateReleaseUnitTestJniHeaders", "generateReleaseAndroidTestJniHeaders")
        .containsOnly(null)
    }

    assertThat(createTestRunner(projectDir))
      .taskOutcomes("generateJniHeaders", "generateDebugJniHeaders", "generateReleaseJniHeaders")
      .containsOnly(TaskOutcome.UP_TO_DATE)

    listOf("debug", "release").forEach { variant ->
      assertThat(File(projectDir, "build/generated/ktjni/$variant"))
        .transform { it.resolve("com_example_Example.h") }
        .hasText(ExpectedOutcome)
    }
  }

  @Test fun minKotlinAndroidApplication() {
    val projectDir = File(fixturesDir, "min/kotlin-android-application")
    assertThat(createCleanTestRunner(projectDir)).all {
      taskOutcomes("generateJniHeaders", "generateDebugJniHeaders", "generateReleaseJniHeaders")
        .containsOnly(TaskOutcome.SUCCESS)
      // `releaseUnitTest` variant executes with AGP min.
      taskOutcomes("generateDebugAndroidTestJniHeaders", "generateDebugUnitTestJniHeaders", "generateReleaseUnitTestJniHeaders")
        .containsOnly(TaskOutcome.NO_SOURCE)
      taskOutcomes("generateReleaseAndroidTestJniHeaders")
        .containsOnly(null)
    }

    assertThat(createTestRunner(projectDir))
      .taskOutcomes("generateJniHeaders", "generateDebugJniHeaders", "generateReleaseJniHeaders")
      .containsOnly(TaskOutcome.UP_TO_DATE)

    listOf("debug", "release").forEach { variant ->
      assertThat(File(projectDir, "build/generated/ktjni/$variant"))
        .transform { it.resolve("com_example_Example.h") }
        .hasText(ExpectedOutcome)
    }
  }

  @Test fun latestJavaAndroidLibrary() {
    val projectDir = File(fixturesDir, "latest/java-android-library")
    assertThat(createCleanTestRunner(projectDir)).all {
      taskOutcomes("generateJniHeaders", "generateDebugJniHeaders", "generateReleaseJniHeaders")
        .containsOnly(TaskOutcome.SUCCESS)
      taskOutcomes("generateDebugAndroidTestJniHeaders", "generateDebugUnitTestJniHeaders")
        .containsOnly(TaskOutcome.NO_SOURCE)
      taskOutcomes("generateReleaseUnitTestJniHeaders", "generateReleaseAndroidTestJniHeaders")
        .containsOnly(null)
    }

    assertThat(createTestRunner(projectDir))
      .taskOutcomes("generateJniHeaders", "generateDebugJniHeaders", "generateReleaseJniHeaders")
      .containsOnly(TaskOutcome.UP_TO_DATE)

    listOf("debug", "release").forEach { variant ->
      assertThat(File(projectDir, "build/generated/ktjni/$variant"))
        .transform { it.resolve("com_example_Example.h") }
        .hasText(ExpectedOutcome)
    }
  }

  @Test fun minJavaAndroidLibrary() {
    val projectDir = File(fixturesDir, "min/java-android-library")
    assertThat(createCleanTestRunner(projectDir)).all {
      taskOutcomes("generateJniHeaders", "generateDebugJniHeaders", "generateReleaseJniHeaders")
        .containsOnly(TaskOutcome.SUCCESS)
      taskOutcomes("generateDebugAndroidTestJniHeaders", "generateDebugUnitTestJniHeaders", "generateReleaseUnitTestJniHeaders")
        .containsOnly(TaskOutcome.NO_SOURCE)
      taskOutcomes("generateReleaseAndroidTestJniHeaders")
        .containsOnly(null)
    }

    assertThat(createTestRunner(projectDir))
      .taskOutcomes("generateJniHeaders", "generateDebugJniHeaders", "generateReleaseJniHeaders")
      .containsOnly(TaskOutcome.UP_TO_DATE)

    listOf("debug", "release").forEach { variant ->
      assertThat(File(projectDir, "build/generated/ktjni/$variant"))
        .transform { it.resolve("com_example_Example.h") }
        .hasText(ExpectedOutcome)
    }
  }

  @Test fun latestJavaAndroidApplication() {
    val projectDir = File(fixturesDir, "latest/java-android-application")
    assertThat(createCleanTestRunner(projectDir)).all {
      taskOutcomes("generateJniHeaders", "generateDebugJniHeaders", "generateReleaseJniHeaders")
        .containsOnly(TaskOutcome.SUCCESS)
      taskOutcomes("generateDebugAndroidTestJniHeaders", "generateDebugUnitTestJniHeaders")
        .containsOnly(TaskOutcome.NO_SOURCE)
      taskOutcomes("generateReleaseUnitTestJniHeaders", "generateReleaseAndroidTestJniHeaders")
        .containsOnly(null)
    }

    assertThat(createTestRunner(projectDir))
      .taskOutcomes("generateJniHeaders", "generateDebugJniHeaders", "generateReleaseJniHeaders")
      .containsOnly(TaskOutcome.UP_TO_DATE)

    listOf("debug", "release").forEach { variant ->
      assertThat(File(projectDir, "build/generated/ktjni/$variant"))
        .transform { it.resolve("com_example_Example.h") }
        .hasText(ExpectedOutcome)
    }
  }

  @Test fun minJavaAndroidApplication() {
    val projectDir = File(fixturesDir, "min/java-android-application")
    assertThat(createCleanTestRunner(projectDir)).all {
      taskOutcomes("generateJniHeaders", "generateDebugJniHeaders", "generateReleaseJniHeaders")
        .containsOnly(TaskOutcome.SUCCESS)
      taskOutcomes("generateDebugAndroidTestJniHeaders", "generateDebugUnitTestJniHeaders", "generateReleaseUnitTestJniHeaders")
        .containsOnly(TaskOutcome.NO_SOURCE)
      taskOutcomes("generateReleaseAndroidTestJniHeaders")
        .containsOnly(null)
    }

    assertThat(createTestRunner(projectDir))
      .taskOutcomes("generateJniHeaders", "generateDebugJniHeaders", "generateReleaseJniHeaders")
      .containsOnly(TaskOutcome.UP_TO_DATE)

    listOf("debug", "release").forEach { variant ->
      assertThat(File(projectDir, "build/generated/ktjni/$variant"))
        .transform { it.resolve("com_example_Example.h") }
        .hasText(ExpectedOutcome)
    }
  }
}

private fun createCleanTestRunner(projectDir: File): BuildResult {
  File(projectDir, "build").deleteRecursively()
  return createTestRunner(projectDir)
}

private fun createTestRunner(
  projectDir: File,
  vararg tasks: String = arrayOf("generateJniHeaders"),
): BuildResult {
  val isMin = projectDir.parentFile.name == "min"
  return GradleRunner.create()
    .apply {
      forwardOutput()
      withProjectDir(projectDir)
      withArguments(
        buildList {
          addAll(tasks)
          add("--configuration-cache")
          // kotlin-android at our min is not IP-compatible.
          if (!isMin) add("--isolated-projects")
        },
      )
      if (isMin) withGradleVersion(MIN_GRADLE_VERSION)
      withDebug(false)
    }.build()
}

private val fixturesDir = File("src/test/fixtures")
private const val MIN_GRADLE_VERSION = "8.14.7"
