// Copyright 2026, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.ktjni

import assertk.all
import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.isNull
import assertk.assertions.single
import io.github.fletchmckee.ktjni.dsl.buildProject
import io.github.fletchmckee.ktjni.util.CompatibleMatrix.Latest
import io.github.fletchmckee.ktjni.util.CompatibleMatrix.Min
import io.github.fletchmckee.ktjni.util.assertRuns
import io.github.fletchmckee.ktjni.util.assertVariantHeaders
import io.github.fletchmckee.ktjni.util.assertVariantOutcome
import io.github.fletchmckee.ktjni.util.taskOutcomes
import io.github.fletchmckee.ktjni.util.writeAndroidManifestFile
import io.github.fletchmckee.ktjni.util.writeCommonSettingsFile
import io.github.fletchmckee.ktjni.util.writeJavaExampleFile
import io.github.fletchmckee.ktjni.util.writeKotlinExampleFile
import java.io.File
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class AndroidPluginTest {
  @TempDir lateinit var projectRoot: File
  private lateinit var buildFile: File

  @BeforeEach fun setup() {
    val localCacheDir = File(projectRoot, "local-cache")
    buildFile = File(projectRoot, "build.gradle.kts")
    val settingsFile = File(projectRoot, "settings.gradle.kts")

    settingsFile.writeCommonSettingsFile(localCacheDir)
  }

  @Test fun `latest - generates headers for builtInKotlin Android library`() {
    projectRoot.writeKotlinExampleFile()

    buildProject(buildFile) {
      plugins {
        androidLibrary(Latest.agp)
      }

      android()
      kotlin()
    }

    assertRuns(
      projectDir = projectRoot,
      compatibleMatrix = Latest,
      assert = { firstRun, secondRun ->
        assertThat(firstRun)
        firstRun
          .assertVariantOutcome(TaskOutcome.SUCCESS, "debug", "release")
          .assertVariantOutcome(TaskOutcome.NO_SOURCE, "debugAndroidTest", "debugUnitTest")
          // Both test release variants are null after AGP 9.
          .assertVariantOutcome(null, "releaseAndroidTest", "releaseUnitTest")
        secondRun
          .assertVariantOutcome(TaskOutcome.UP_TO_DATE, "debug", "release")
        assertVariantHeaders(projectRoot, "debug", "release")
      },
    )
  }

  @Test fun `latest - generates headers for Kotlin Android library`() {
    projectRoot.writeKotlinExampleFile()

    buildProject(buildFile) {
      plugins {
        kotlinAndroid(Latest.kgp)
        androidLibrary(Latest.agp)
      }

      android()
      kotlin()
    }

    assertRuns(
      projectDir = projectRoot,
      builtInKotlin = false,
      compatibleMatrix = Latest,
      assert = { firstRun, secondRun ->
        firstRun
          .assertVariantOutcome(TaskOutcome.SUCCESS, "debug", "release")
          .assertVariantOutcome(TaskOutcome.NO_SOURCE, "debugAndroidTest", "debugUnitTest")
          // Both test release variants are null after AGP 9.
          .assertVariantOutcome(null, "releaseAndroidTest", "releaseUnitTest")
        secondRun
          .assertVariantOutcome(TaskOutcome.UP_TO_DATE, "debug", "release")
        assertVariantHeaders(projectRoot, "debug", "release")
      },
    )
  }

  @Test fun `min - generates headers for Kotlin Android library`() {
    projectRoot.writeKotlinExampleFile()

    buildProject(buildFile) {
      plugins {
        kotlinAndroid(Min.kgp)
        androidLibrary(Min.agp)
      }

      android {
        compileSdk = Min.compileSdk
      }

      kotlin()
    }

    assertRuns(
      projectDir = projectRoot,
      compatibleMatrix = Min,
      assert = { firstRun, secondRun ->
        firstRun
          .assertVariantOutcome(TaskOutcome.SUCCESS, "debug", "release")
          .assertVariantOutcome(TaskOutcome.NO_SOURCE, "debugAndroidTest", "debugUnitTest", "releaseUnitTest")
          .assertVariantOutcome(null, "releaseAndroidTest")
        secondRun
          .assertVariantOutcome(TaskOutcome.UP_TO_DATE, "debug", "release")
        assertVariantHeaders(projectRoot, "debug", "release")
      },
    )
  }

  @Test fun `latest - generates headers for builtInKotlin Android application`() {
    projectRoot.writeKotlinExampleFile()
    projectRoot.writeAndroidManifestFile()

    buildProject(buildFile) {
      plugins {
        androidApplication(Latest.agp)
      }

      android()
      kotlin()
    }

    assertRuns(
      projectDir = projectRoot,
      compatibleMatrix = Latest,
      assert = { firstRun, secondRun ->
        firstRun
          .assertVariantOutcome(TaskOutcome.SUCCESS, "debug", "release")
          .assertVariantOutcome(TaskOutcome.NO_SOURCE, "debugAndroidTest", "debugUnitTest")
          // Both test release variants are null after AGP 9.
          .assertVariantOutcome(null, "releaseAndroidTest", "releaseUnitTest")
        secondRun
          .assertVariantOutcome(TaskOutcome.UP_TO_DATE, "debug", "release")
        assertVariantHeaders(projectRoot, "debug", "release")
      },
    )
  }

  @Test fun `min - generates headers for Kotlin Android application`() {
    projectRoot.writeKotlinExampleFile()
    projectRoot.writeAndroidManifestFile()

    buildProject(buildFile) {
      plugins {
        kotlinAndroid(Min.kgp)
        androidApplication(Min.agp)
      }

      android {
        compileSdk = Min.compileSdk
      }

      kotlin()
    }

    assertRuns(
      projectDir = projectRoot,
      compatibleMatrix = Min,
      assert = { firstRun, secondRun ->
        firstRun
          .assertVariantOutcome(TaskOutcome.SUCCESS, "debug", "release")
          .assertVariantOutcome(TaskOutcome.NO_SOURCE, "debugAndroidTest", "debugUnitTest", "releaseUnitTest")
          .assertVariantOutcome(null, "releaseAndroidTest")
        secondRun
          .assertVariantOutcome(TaskOutcome.UP_TO_DATE, "debug", "release")
        assertVariantHeaders(projectRoot, "debug", "release")
      },
    )
  }

  @Test fun `latest - generates headers for Kotlin Android Application`() {
    projectRoot.writeKotlinExampleFile()
    projectRoot.writeAndroidManifestFile()

    buildProject(buildFile) {
      plugins {
        kotlinAndroid(Latest.kgp)
        androidApplication(Latest.agp)
      }

      android()
      kotlin()
    }

    assertRuns(
      projectDir = projectRoot,
      builtInKotlin = false,
      compatibleMatrix = Latest,
      assert = { firstRun, secondRun ->
        firstRun
          .assertVariantOutcome(TaskOutcome.SUCCESS, "debug", "release")
          .assertVariantOutcome(TaskOutcome.NO_SOURCE, "debugAndroidTest", "debugUnitTest")
          // Both test release variants are null after AGP 9.
          .assertVariantOutcome(null, "releaseAndroidTest", "releaseUnitTest")
        secondRun
          .assertVariantOutcome(TaskOutcome.UP_TO_DATE, "debug", "release")
        assertVariantHeaders(projectRoot, "debug", "release")
      },
    )
  }

  @Test fun `latest - generates headers for Java Android application`() {
    projectRoot.writeJavaExampleFile()
    projectRoot.writeAndroidManifestFile()

    buildProject(buildFile) {
      plugins {
        androidApplication(Latest.agp)
      }

      android()
      java()
    }

    assertRuns(
      projectDir = projectRoot,
      compatibleMatrix = Latest,
      assert = { firstRun, secondRun ->
        firstRun
          .assertVariantOutcome(TaskOutcome.SUCCESS, "debug", "release")
          .assertVariantOutcome(TaskOutcome.NO_SOURCE, "debugAndroidTest", "debugUnitTest")
          .assertVariantOutcome(null, "releaseUnitTest", "releaseAndroidTest")
        secondRun
          .assertVariantOutcome(TaskOutcome.UP_TO_DATE, "debug", "release")
        assertVariantHeaders(projectRoot, "debug", "release")
      },
    )
  }

  @Test fun `min - generates headers for Java Android application`() {
    projectRoot.writeJavaExampleFile()
    projectRoot.writeAndroidManifestFile()

    buildProject(buildFile) {
      plugins {
        androidApplication(Min.agp)
      }

      android {
        compileSdk = Min.compileSdk
      }

      java()
    }

    assertRuns(
      projectDir = projectRoot,
      compatibleMatrix = Min,
      assert = { firstRun, secondRun ->
        assertThat(firstRun).all {
          taskOutcomes("generateDebugJniHeaders", "generateReleaseJniHeaders")
            .containsOnly(TaskOutcome.SUCCESS)

          taskOutcomes(
            "generateDebugAndroidTestJniHeaders",
            "generateDebugUnitTestJniHeaders",
            "generateReleaseUnitTestJniHeaders",
          ).containsOnly(TaskOutcome.NO_SOURCE)

          taskOutcomes("generateReleaseAndroidTestJniHeaders")
            .single()
            .isNull()
        }

        assertThat(secondRun)
          .taskOutcomes("generateDebugJniHeaders", "generateReleaseJniHeaders")
          .containsOnly(TaskOutcome.UP_TO_DATE)
        assertVariantHeaders(projectRoot, "debug", "release")
      },
    )
  }
}
