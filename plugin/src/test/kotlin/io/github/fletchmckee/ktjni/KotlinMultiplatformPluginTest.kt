// Copyright 2026, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.ktjni

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import assertk.assertions.single
import io.github.fletchmckee.ktjni.dsl.buildProject
import io.github.fletchmckee.ktjni.util.CompatibleMatrix.Latest
import io.github.fletchmckee.ktjni.util.CompatibleMatrix.Min
import io.github.fletchmckee.ktjni.util.assertRuns
import io.github.fletchmckee.ktjni.util.assertVariantHeaders
import io.github.fletchmckee.ktjni.util.assertVariantOutcome
import io.github.fletchmckee.ktjni.util.taskOutcomes
import io.github.fletchmckee.ktjni.util.writeCommonSettingsFile
import io.github.fletchmckee.ktjni.util.writeJavaExampleFile
import io.github.fletchmckee.ktjni.util.writeKotlinExampleFile
import java.io.File
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class KotlinMultiplatformPluginTest {
  @TempDir lateinit var projectRoot: File
  private lateinit var buildFile: File

  @BeforeEach fun setup() {
    val localCacheDir = File(projectRoot, "local-cache")
    buildFile = File(projectRoot, "build.gradle.kts")
    val settingsFile = File(projectRoot, "settings.gradle.kts")

    settingsFile.writeCommonSettingsFile(localCacheDir)
  }

  @Test fun `latest - commonMain generates headers for android and jvm`() {
    projectRoot.writeKotlinExampleFile(sourceSet = "commonMain")

    buildProject(buildFile) {
      plugins {
        kotlinMultiplatform(Latest.kgp)
        androidMultiplatform(Latest.agp)
      }

      kotlin {
        android()
        jvm()
      }
    }

    assertRuns(
      projectDir = projectRoot,
      compatibleMatrix = Latest,
      assert = { firstRun, secondRun ->
        firstRun.assertVariantOutcome(TaskOutcome.SUCCESS, "jvmMain", "androidMain")
        secondRun.assertVariantOutcome(TaskOutcome.UP_TO_DATE, "jvmMain", "androidMain")
        assertVariantHeaders(projectRoot, "jvmMain", "androidMain")
      },
    )
  }

  @Test fun `min - commonMain generates headers for android and jvm`() {
    projectRoot.writeKotlinExampleFile(sourceSet = "commonMain")

    buildProject(buildFile) {
      plugins {
        kotlinMultiplatform(Min.kgp)
        androidLibrary(Min.agp)
      }

      android {
        compileSdk = Min.compileSdk
      }

      kotlin {
        androidTarget()
        jvm()
      }
    }

    assertRuns(
      projectDir = projectRoot,
      compatibleMatrix = Min,
      assert = { firstRun, secondRun ->
        firstRun.assertVariantOutcome(TaskOutcome.SUCCESS, "jvmMain", "release", "debug")
        firstRun.assertVariantOutcome(null, "androidDebug", "androidRelease")
        secondRun.assertVariantOutcome(TaskOutcome.UP_TO_DATE, "jvmMain", "release", "debug")
        assertVariantHeaders(projectRoot, "jvmMain", "release", "debug")
      },
    )
  }

  @Test fun `latest - jvmMain generates headers for jvm only`() {
    projectRoot.writeKotlinExampleFile(
      sourceSet = "jvmMain",
      extension = "jvm.kt",
    )

    buildProject(buildFile) {
      plugins {
        kotlinMultiplatform(Latest.kgp)
        androidMultiplatform(Latest.agp)
      }

      kotlin {
        android()
        jvm()
      }
    }

    assertRuns(
      projectDir = projectRoot,
      compatibleMatrix = Latest,
      assert =
      { firstRun, secondRun ->
        firstRun
          .assertVariantOutcome(TaskOutcome.SUCCESS, "jvmMain")
          .assertVariantOutcome(TaskOutcome.NO_SOURCE, "androidMain")
        secondRun
          .assertVariantOutcome(TaskOutcome.UP_TO_DATE, "jvmMain")
        assertVariantHeaders(projectRoot, "jvmMain")
      },
    )
  }

  @Test fun `min - jvmMain generates headers for jvm only`() {
    projectRoot.writeKotlinExampleFile(sourceSet = "jvmMain", extension = "jvm.kt")

    buildProject(buildFile) {
      plugins {
        kotlinMultiplatform(Min.kgp)
        androidLibrary(Min.agp)
      }

      android {
        compileSdk = Min.compileSdk
      }

      kotlin {
        androidTarget()
        jvm()
      }
    }

    assertRuns(
      projectDir = projectRoot,
      compatibleMatrix = Min,
      assert = { firstRun, secondRun ->
        firstRun.assertVariantOutcome(TaskOutcome.SUCCESS, "jvmMain")
        secondRun.assertVariantOutcome(TaskOutcome.UP_TO_DATE, "jvmMain")
        assertVariantHeaders(projectRoot, "jvmMain")
      },
    )
  }

  @Test fun `latest - androidMain generates headers for android only`() {
    projectRoot.writeKotlinExampleFile(
      sourceSet = "androidMain",
      extension = "android.kt",
    )

    buildProject(buildFile) {
      plugins {
        kotlinMultiplatform(Latest.kgp)
        androidMultiplatform(Latest.agp)
      }

      kotlin {
        android()
        jvm()
      }
    }

    assertRuns(
      projectDir = projectRoot,
      compatibleMatrix = Latest,
      assert = { firstRun, secondRun ->
        firstRun
          .assertVariantOutcome(TaskOutcome.SUCCESS, "androidMain")
          .assertVariantOutcome(TaskOutcome.NO_SOURCE, "jvmMain")
        secondRun
          .assertVariantOutcome(TaskOutcome.UP_TO_DATE, "androidMain")
        assertVariantHeaders(projectRoot, "androidMain")
      },
    )
  }

  @Test fun `min - androidMain generates headers for android only`() {
    projectRoot.writeKotlinExampleFile(
      sourceSet = "androidMain",
      extension = "android.kt",
    )

    buildProject(buildFile) {
      plugins {
        kotlinMultiplatform(Min.kgp)
        androidLibrary(Min.agp)
      }

      android {
        compileSdk = Min.compileSdk
      }

      kotlin {
        androidTarget()
        jvm()
      }
    }

    assertRuns(
      projectDir = projectRoot,
      compatibleMatrix = Min,
      assert = { firstRun, secondRun ->
        firstRun.assertVariantOutcome(TaskOutcome.SUCCESS, "release")
        secondRun.assertVariantOutcome(TaskOutcome.UP_TO_DATE, "release")
        assertVariantHeaders(projectRoot, "release")
      },
    )
  }

  @Test fun `latest - generates headers for Kotlin Multiplatform Java classes`() {
    projectRoot.writeJavaExampleFile(sourceSet = "jvmMain")

    buildProject(buildFile) {
      plugins {
        kotlinMultiplatform(Latest.kgp)
      }

      java()
      kotlin {
        jvm()
      }
    }

    assertRuns(
      projectDir = projectRoot,
      compatibleMatrix = Latest,
      android = false,
      assert = { firstRun, secondRun ->
        firstRun
          .assertVariantOutcome(TaskOutcome.SUCCESS, "jvmMain")
          .assertVariantOutcome(null, "main")
        secondRun.assertVariantOutcome(TaskOutcome.UP_TO_DATE, "jvmMain")
        assertVariantHeaders(projectRoot, "jvmMain")
      },
    )
  }

  @Test fun `min - generates headers for Kotlin Multiplatform Java classes withJava`() {
    projectRoot.writeJavaExampleFile(sourceSet = "jvmMain")

    buildProject(buildFile) {
      plugins {
        kotlinMultiplatform(Min.kgp)
      }

      java()
      kotlin {
        jvm(withJava = true)
      }
    }

    assertRuns(
      projectDir = projectRoot,
      compatibleMatrix = Min,
      android = false,
      assert = { firstRun, secondRun ->
        assertThat(firstRun).all {
          taskOutcomes("generateMainJniHeaders")
            .single()
            .isEqualTo(TaskOutcome.SUCCESS)

          taskOutcomes("generateJvmMainJniHeaders")
            .single()
            // Verify we prevent any KMP overlap by skipping.
            .isEqualTo(TaskOutcome.SKIPPED)
        }

        assertThat(secondRun)
          .taskOutcomes("generateMainJniHeaders")
          .single()
          .isEqualTo(TaskOutcome.UP_TO_DATE)
        assertVariantHeaders(projectRoot, "main")
      },
    )
  }

  @Test fun `latest - generates headers for Kotlin Multiplatform Kotlin classes`() {
    projectRoot.writeKotlinExampleFile(sourceSet = "jvmMain")

    buildProject(buildFile) {
      plugins {
        kotlinMultiplatform(Latest.kgp)
      }

      java()
      kotlin {
        jvm()
      }
    }

    assertRuns(
      projectDir = projectRoot,
      compatibleMatrix = Latest,
      android = false,
      assert = { firstRun, secondRun ->
        assertThat(firstRun).all {
          taskOutcomes("generateJvmMainJniHeaders")
            .single()
            .isEqualTo(TaskOutcome.SUCCESS)

          taskOutcomes("generateMainJniHeaders")
            .single()
            .isNull()
        }

        assertThat(secondRun)
          .taskOutcomes("generateJvmMainJniHeaders")
          .single()
          .isEqualTo(TaskOutcome.UP_TO_DATE)
        assertVariantHeaders(projectRoot, "jvmMain")
      },
    )
  }

  @Test fun `min - generates headers for Kotlin Multiplatform Kotlin classes`() {
    projectRoot.writeKotlinExampleFile(sourceSet = "jvmMain")

    buildProject(buildFile) {
      plugins {
        kotlinMultiplatform(Min.kgp)
      }

      java()
      kotlin {
        jvm()
      }
    }

    assertRuns(
      projectDir = projectRoot,
      compatibleMatrix = Min,
      android = false,
      assert = { firstRun, secondRun ->
        assertThat(firstRun).all {
          taskOutcomes("generateJvmMainJniHeaders")
            .single()
            .isEqualTo(TaskOutcome.SUCCESS)

          taskOutcomes("generateMainJniHeaders")
            .single()
            .isNull()
        }

        assertThat(secondRun)
          .taskOutcomes("generateJvmMainJniHeaders")
          .single()
          .isEqualTo(TaskOutcome.UP_TO_DATE)
        assertVariantHeaders(projectRoot, "jvmMain")
      },
    )
  }
}
