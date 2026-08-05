// Copyright 2026, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.ktjni

import io.github.fletchmckee.ktjni.dsl.buildProject
import io.github.fletchmckee.ktjni.util.CompatibleMatrix.Latest
import io.github.fletchmckee.ktjni.util.CompatibleMatrix.Min
import io.github.fletchmckee.ktjni.util.Language.Java
import io.github.fletchmckee.ktjni.util.Language.Kotlin
import io.github.fletchmckee.ktjni.util.assertRuns
import io.github.fletchmckee.ktjni.util.assertVariantHeaders
import io.github.fletchmckee.ktjni.util.assertVariantOutcome
import io.github.fletchmckee.ktjni.util.writeAndroidManifestFile
import io.github.fletchmckee.ktjni.util.writeCommonSettingsFile
import io.github.fletchmckee.ktjni.util.writeJavaExampleFile
import io.github.fletchmckee.ktjni.util.writeKotlinExampleFile
import java.io.File
import kotlin.test.Ignore
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
        firstRun
          .assertVariantOutcome<Kotlin>(TaskOutcome.SUCCESS, "debug", "release")
          .assertVariantOutcome<Java>(TaskOutcome.NO_SOURCE, "debug", "release")
          .assertVariantOutcome<Kotlin>(TaskOutcome.NO_SOURCE, "debugAndroidTest", "debugUnitTest")
          // Both test release variants are null after AGP 9.
          .assertVariantOutcome<Kotlin>(null, "releaseAndroidTest", "releaseUnitTest")
        secondRun
          .assertVariantOutcome<Kotlin>(TaskOutcome.UP_TO_DATE, "debug", "release")
        assertVariantHeaders<Kotlin>(projectRoot, "debug", "release")
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
          .assertVariantOutcome<Kotlin>(TaskOutcome.SUCCESS, "debug", "release")
          .assertVariantOutcome<Java>(TaskOutcome.NO_SOURCE, "debug", "release")
          .assertVariantOutcome<Kotlin>(TaskOutcome.NO_SOURCE, "debugAndroidTest", "debugUnitTest")
          // Both test release variants are null after AGP 9.
          .assertVariantOutcome<Kotlin>(null, "releaseAndroidTest", "releaseUnitTest")
        secondRun
          .assertVariantOutcome<Kotlin>(TaskOutcome.UP_TO_DATE, "debug", "release")
        assertVariantHeaders<Kotlin>(projectRoot, "debug", "release")
      },
    )
  }

  // TODO: Verify if this is an AGP or ktjni issue.
  @Ignore("Ktjni tasks are up-to-date but assertConfigurationCacheReused fails")
  @Test
  fun `min - generates headers for Kotlin Android library`() {
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
          .assertVariantOutcome<Kotlin>(TaskOutcome.SUCCESS, "debug", "release")
          .assertVariantOutcome<Java>(TaskOutcome.NO_SOURCE, "debug", "release")
          .assertVariantOutcome<Kotlin>(TaskOutcome.NO_SOURCE, "debugAndroidTest", "debugUnitTest", "releaseUnitTest")
          .assertVariantOutcome<Kotlin>(null, "releaseAndroidTest")
        secondRun
          .assertVariantOutcome<Kotlin>(TaskOutcome.UP_TO_DATE, "debug", "release")
        assertVariantHeaders<Kotlin>(projectRoot, "debug", "release")
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
          .assertVariantOutcome<Kotlin>(TaskOutcome.SUCCESS, "debug", "release")
          .assertVariantOutcome<Java>(TaskOutcome.NO_SOURCE, "debug", "release")
          .assertVariantOutcome<Kotlin>(TaskOutcome.NO_SOURCE, "debugAndroidTest", "debugUnitTest")
          // Both test release variants are null after AGP 9.
          .assertVariantOutcome<Kotlin>(null, "releaseAndroidTest", "releaseUnitTest")
        secondRun
          .assertVariantOutcome<Kotlin>(TaskOutcome.UP_TO_DATE, "debug", "release")
        assertVariantHeaders<Kotlin>(projectRoot, "debug", "release")
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
          .assertVariantOutcome<Kotlin>(TaskOutcome.SUCCESS, "debug", "release")
          .assertVariantOutcome<Java>(TaskOutcome.NO_SOURCE, "debug", "release")
          .assertVariantOutcome<Kotlin>(TaskOutcome.NO_SOURCE, "debugAndroidTest", "debugUnitTest")
          // Both test release variants are null after AGP 9.
          .assertVariantOutcome<Kotlin>(null, "releaseAndroidTest", "releaseUnitTest")
        secondRun
          .assertVariantOutcome<Kotlin>(TaskOutcome.UP_TO_DATE, "debug", "release")
        assertVariantHeaders<Kotlin>(projectRoot, "debug", "release")
      },
    )
  }

  // TODO: Verify if this is an AGP or ktjni issue.
  @Ignore("Ktjni tasks are up-to-date but assertConfigurationCacheReused fails")
  @Test
  fun `min - generates headers for Kotlin Android application`() {
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
          .assertVariantOutcome<Kotlin>(TaskOutcome.SUCCESS, "debug", "release")
          .assertVariantOutcome<Java>(TaskOutcome.NO_SOURCE, "debug", "release")
          .assertVariantOutcome<Kotlin>(TaskOutcome.NO_SOURCE, "debugAndroidTest", "debugUnitTest", "releaseUnitTest")
          .assertVariantOutcome<Kotlin>(null, "releaseAndroidTest")
        secondRun
          .assertVariantOutcome<Kotlin>(TaskOutcome.UP_TO_DATE, "debug", "release")
        assertVariantHeaders<Kotlin>(projectRoot, "debug", "release")
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
          .assertVariantOutcome<Java>(TaskOutcome.SUCCESS, "debug", "release")
          .assertVariantOutcome<Kotlin>(TaskOutcome.NO_SOURCE, "debug", "release")
          .assertVariantOutcome<Java>(TaskOutcome.NO_SOURCE, "debugAndroidTest", "debugUnitTest")
          .assertVariantOutcome<Java>(null, "releaseUnitTest", "releaseAndroidTest")
        secondRun
          .assertVariantOutcome<Java>(TaskOutcome.UP_TO_DATE, "debug", "release")
        assertVariantHeaders<Java>(projectRoot, "debug", "release")
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
        firstRun
          .assertVariantOutcome<Java>(TaskOutcome.SUCCESS, "debug", "release")
          .assertVariantOutcome<Kotlin>(null, "debug", "release")
          .assertVariantOutcome<Java>(TaskOutcome.NO_SOURCE, "debugAndroidTest", "debugUnitTest", "releaseUnitTest")
          .assertVariantOutcome<Java>(null, "releaseAndroidTest")
        secondRun
          .assertVariantOutcome<Java>(TaskOutcome.UP_TO_DATE, "debug", "release")
        assertVariantHeaders<Java>(projectRoot, "debug", "release")
      },
    )
  }
}
