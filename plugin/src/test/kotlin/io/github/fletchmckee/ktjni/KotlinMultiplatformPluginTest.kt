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
        firstRun
          .assertVariantOutcome<Kotlin>(TaskOutcome.SUCCESS, "jvmMain", "androidMain")
        secondRun
          .assertVariantOutcome<Kotlin>(TaskOutcome.UP_TO_DATE, "jvmMain", "androidMain")
        assertVariantHeaders<Kotlin>(projectRoot, "jvmMain", "androidMain")
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
        firstRun
          .assertVariantOutcome<Kotlin>(TaskOutcome.SUCCESS, "jvmMain", "androidRelease", "androidDebug")
        secondRun
          .assertVariantOutcome<Kotlin>(TaskOutcome.UP_TO_DATE, "jvmMain", "androidRelease", "androidDebug")
        assertVariantHeaders<Kotlin>(projectRoot, "jvmMain", "androidRelease", "androidDebug")
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
          .assertVariantOutcome<Kotlin>(TaskOutcome.SUCCESS, "jvmMain")
          .assertVariantOutcome<Kotlin>(TaskOutcome.NO_SOURCE, "androidMain")
        secondRun
          .assertVariantOutcome<Kotlin>(TaskOutcome.UP_TO_DATE, "jvmMain")
        assertVariantHeaders<Kotlin>(projectRoot, "jvmMain")
      },
    )
  }

  @Test fun `min - jvmMain generates headers for jvm only`() {
    projectRoot.writeKotlinExampleFile(
      sourceSet = "jvmMain",
      extension = "jvm.kt",
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
        firstRun
          .assertVariantOutcome<Kotlin>(TaskOutcome.SUCCESS, "jvmMain")
          .assertVariantOutcome<Kotlin>(TaskOutcome.NO_SOURCE, "androidRelease", "androidDebug")
        secondRun
          .assertVariantOutcome<Kotlin>(TaskOutcome.UP_TO_DATE, "jvmMain")
        assertVariantHeaders<Kotlin>(projectRoot, "jvmMain")
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
          .assertVariantOutcome<Kotlin>(TaskOutcome.SUCCESS, "androidMain")
          .assertVariantOutcome<Kotlin>(TaskOutcome.NO_SOURCE, "jvmMain")
        secondRun
          .assertVariantOutcome<Kotlin>(TaskOutcome.UP_TO_DATE, "androidMain")
        assertVariantHeaders<Kotlin>(projectRoot, "androidMain")
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
        firstRun
          .assertVariantOutcome<Kotlin>(TaskOutcome.SUCCESS, "androidRelease")
          .assertVariantOutcome<Kotlin>(TaskOutcome.NO_SOURCE, "jvmMain")
        secondRun
          .assertVariantOutcome<Kotlin>(TaskOutcome.UP_TO_DATE, "androidRelease")
        assertVariantHeaders<Kotlin>(projectRoot, "androidRelease")
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
          .assertVariantOutcome<Java>(TaskOutcome.SUCCESS, "jvmMain")
          .assertVariantOutcome<Kotlin>(TaskOutcome.NO_SOURCE, "jvmMain")
        secondRun
          .assertVariantOutcome<Java>(TaskOutcome.UP_TO_DATE, "jvmMain")
        assertVariantHeaders<Java>(projectRoot, "jvmMain")
      },
    )
  }

  @Test fun `min - generates headers for Kotlin Multiplatform Java classes`() {
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
        firstRun
          .assertVariantOutcome<Java>(TaskOutcome.SUCCESS, "main")
          .assertVariantOutcome<Kotlin>(TaskOutcome.NO_SOURCE, "jvmMain")
        secondRun
          .assertVariantOutcome<Java>(TaskOutcome.UP_TO_DATE, "main")
        assertVariantHeaders<Java>(projectRoot, "main")
      },
    )
  }
}
