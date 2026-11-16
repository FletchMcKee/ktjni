// Copyright 2026, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.ktjni

import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.exists
import assertk.assertions.hasText
import io.github.fletchmckee.ktjni.util.ExpectedOutcome
import io.github.fletchmckee.ktjni.util.taskOutcomes
import java.io.File
import kotlin.test.Test
import org.gradle.testkit.runner.TaskOutcome

class JvmPluginTest {
  @Test fun latestKotlinJvm() {
    val projectDir = File(fixturesDir, "latest/kotlin-jvm")
    assertThat(createCleanTestRunner(projectDir))
      .taskOutcomes("generateJniHeaders", "generateMainJniHeaders")
      .containsOnly(TaskOutcome.SUCCESS)

    assertThat(createTestRunner(projectDir))
      .taskOutcomes("generateJniHeaders", "generateMainJniHeaders")
      .containsOnly(TaskOutcome.UP_TO_DATE)

    assertThat(File(projectDir, "build/generated/ktjni/main"))
      .transform { it.resolve("com_example_Example.h") }
      .hasText(ExpectedOutcome)
  }

  @Test fun minKotlinJvm() {
    val projectDir = File(fixturesDir, "min/kotlin-jvm")
    assertThat(createCleanTestRunner(projectDir))
      .taskOutcomes("generateJniHeaders", "generateMainJniHeaders")
      .containsOnly(TaskOutcome.SUCCESS)

    assertThat(createTestRunner(projectDir))
      .taskOutcomes("generateJniHeaders", "generateMainJniHeaders")
      .containsOnly(TaskOutcome.UP_TO_DATE)

    assertThat(File(projectDir, "build/generated/ktjni/main"))
      .transform { it.resolve("com_example_Example.h") }
      .hasText(ExpectedOutcome)
  }

  @Test fun latestJavaLibrary() {
    val projectDir = File(fixturesDir, "latest/java-library")
    assertThat(createCleanTestRunner(projectDir))
      .taskOutcomes("generateJniHeaders", "generateMainJniHeaders")
      .containsOnly(TaskOutcome.SUCCESS)

    assertThat(createTestRunner(projectDir))
      .taskOutcomes("generateJniHeaders", "generateMainJniHeaders")
      .containsOnly(TaskOutcome.UP_TO_DATE)

    assertThat(File(projectDir, "build/generated/ktjni/main"))
      .transform { it.resolve("com_example_Example.h") }
      .hasText(ExpectedOutcome)
  }

  @Test fun minJavaLibrary() {
    val projectDir = File(fixturesDir, "min/java-library")
    assertThat(createCleanTestRunner(projectDir))
      .taskOutcomes("generateJniHeaders", "generateMainJniHeaders")
      .containsOnly(TaskOutcome.SUCCESS)

    assertThat(createTestRunner(projectDir))
      .taskOutcomes("generateJniHeaders", "generateMainJniHeaders")
      .containsOnly(TaskOutcome.UP_TO_DATE)

    assertThat(File(projectDir, "build/generated/ktjni/main"))
      .transform { it.resolve("com_example_Example.h") }
      .hasText(ExpectedOutcome)
  }

  @Test fun latestKotlinJvmWithJava() {
    val projectDir = File(fixturesDir, "latest/kotlin-jvm-with-java")
    assertThat(createCleanTestRunner(projectDir))
      .taskOutcomes("generateJniHeaders", "generateMainJniHeaders")
      .containsOnly(TaskOutcome.SUCCESS)

    assertThat(createTestRunner(projectDir))
      .taskOutcomes("generateJniHeaders", "generateMainJniHeaders")
      .containsOnly(TaskOutcome.UP_TO_DATE)

    assertThat(File(projectDir, "build/generated/ktjni/main"))
      .transform { it.resolve("com_example_Example.h") }
      .hasText(ExpectedOutcome)

    // Verify the java one exists.
    assertThat(File(projectDir, "build/generated/ktjni/main"))
      .transform { it.resolve("com_example_OtherExample.h") }
      .exists()
  }

  @Test fun minKotlinJvmWithJava() {
    val projectDir = File(fixturesDir, "min/kotlin-jvm-with-java")
    assertThat(createCleanTestRunner(projectDir))
      .taskOutcomes("generateJniHeaders", "generateMainJniHeaders")
      .containsOnly(TaskOutcome.SUCCESS)

    assertThat(createTestRunner(projectDir))
      .taskOutcomes("generateJniHeaders", "generateMainJniHeaders")
      .containsOnly(TaskOutcome.UP_TO_DATE)

    assertThat(File(projectDir, "build/generated/ktjni/main"))
      .transform { it.resolve("com_example_Example.h") }
      .hasText(ExpectedOutcome)

    // Verify the java one exists.
    assertThat(File(projectDir, "build/generated/ktjni/main"))
      .transform { it.resolve("com_example_OtherExample.h") }
      .exists()
  }

  @Test fun latestScala() {
    val projectDir = File(fixturesDir, "latest/scala")
    assertThat(createCleanTestRunner(projectDir))
      .taskOutcomes("generateJniHeaders", "generateMainJniHeaders")
      .containsOnly(TaskOutcome.SUCCESS)

    assertThat(createTestRunner(projectDir))
      .taskOutcomes("generateJniHeaders", "generateMainJniHeaders")
      .containsOnly(TaskOutcome.UP_TO_DATE)

    assertThat(File(projectDir, "build/generated/ktjni/main"))
      .transform { it.resolve("com_example_Example.h") }
      .hasText(ExpectedOutcome)
  }

  @Test fun minScala() {
    val projectDir = File(fixturesDir, "min/scala")
    assertThat(createCleanTestRunner(projectDir))
      .taskOutcomes("generateJniHeaders", "generateMainJniHeaders")
      .containsOnly(TaskOutcome.SUCCESS)

    assertThat(createTestRunner(projectDir))
      .taskOutcomes("generateJniHeaders", "generateMainJniHeaders")
      .containsOnly(TaskOutcome.UP_TO_DATE)

    assertThat(File(projectDir, "build/generated/ktjni/main"))
      .transform { it.resolve("com_example_Example.h") }
      .hasText(ExpectedOutcome)
  }
}
