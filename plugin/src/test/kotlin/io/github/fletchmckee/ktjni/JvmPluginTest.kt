// Copyright 2026, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.ktjni

import io.github.fletchmckee.ktjni.dsl.buildProject
import io.github.fletchmckee.ktjni.util.CompatibleMatrix.Latest
import io.github.fletchmckee.ktjni.util.CompatibleMatrix.Min
import io.github.fletchmckee.ktjni.util.Language.Java
import io.github.fletchmckee.ktjni.util.Language.Kotlin
import io.github.fletchmckee.ktjni.util.Language.Scala
import io.github.fletchmckee.ktjni.util.assertRuns
import io.github.fletchmckee.ktjni.util.assertVariantHeaders
import io.github.fletchmckee.ktjni.util.assertVariantOutcome
import io.github.fletchmckee.ktjni.util.writeCommonSettingsFile
import io.github.fletchmckee.ktjni.util.writeJavaExampleFile
import io.github.fletchmckee.ktjni.util.writeKotlinExampleFile
import io.github.fletchmckee.ktjni.util.writeScalaExampleFile
import java.io.File
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class JvmPluginTest {
  @TempDir lateinit var projectRoot: File
  private lateinit var buildFile: File

  @BeforeEach fun setup() {
    val localCacheDir = File(projectRoot, "local-cache")
    buildFile = File(projectRoot, "build.gradle.kts")
    val settingsFile = File(projectRoot, "settings.gradle.kts")

    settingsFile.writeCommonSettingsFile(localCacheDir)
  }

  @Test fun `latest - generates headers for Kotlin JVM`() {
    projectRoot.writeKotlinExampleFile()

    buildProject(buildFile) {
      plugins {
        kotlinJvm(Latest.kgp)
      }

      kotlin()
      java()
    }

    assertRuns(
      projectDir = projectRoot,
      compatibleMatrix = Latest,
      android = false,
    ) { firstRun, secondRun ->
      firstRun
        .assertVariantOutcome<Kotlin>(TaskOutcome.SUCCESS, "main")
        .assertVariantOutcome<Java>(TaskOutcome.NO_SOURCE, "main")
      secondRun
        .assertVariantOutcome<Kotlin>(TaskOutcome.UP_TO_DATE, "main")
      assertVariantHeaders<Kotlin>(projectRoot, "main")
    }
  }

  @Test fun `min - generates headers for Kotlin JVM`() {
    projectRoot.writeKotlinExampleFile()

    buildProject(buildFile) {
      plugins {
        kotlinJvm(Min.kgp)
      }

      kotlin()
      java()
    }

    assertRuns(
      projectDir = projectRoot,
      compatibleMatrix = Min,
      android = false,
    ) { firstRun, secondRun ->
      firstRun
        .assertVariantOutcome<Kotlin>(TaskOutcome.SUCCESS, "main")
        .assertVariantOutcome<Java>(TaskOutcome.NO_SOURCE, "main")
      secondRun
        .assertVariantOutcome<Kotlin>(TaskOutcome.UP_TO_DATE, "main")
      assertVariantHeaders<Kotlin>(projectRoot, "main")
    }
  }

  @Test fun `latest - generates headers for Java`() {
    projectRoot.writeJavaExampleFile()

    buildProject(buildFile) {
      plugins {
        java()
      }

      java()
    }

    assertRuns(
      projectDir = projectRoot,
      compatibleMatrix = Latest,
      android = false,
    ) { firstRun, secondRun ->
      firstRun
        .assertVariantOutcome<Java>(TaskOutcome.SUCCESS, "main")
        .assertVariantOutcome<Kotlin>(null, "main")
      secondRun
        .assertVariantOutcome<Java>(TaskOutcome.UP_TO_DATE, "main")
      assertVariantHeaders<Java>(projectRoot, "main")
    }
  }

  @Test fun `min - generates headers for Java`() {
    projectRoot.writeJavaExampleFile()

    buildProject(buildFile) {
      plugins {
        java()
      }

      java()
    }

    assertRuns(
      projectDir = projectRoot,
      compatibleMatrix = Min,
      android = false,
    ) { firstRun, secondRun ->
      firstRun
        .assertVariantOutcome<Java>(TaskOutcome.SUCCESS, "main")
        .assertVariantOutcome<Kotlin>(null, "main")
      secondRun
        .assertVariantOutcome<Java>(TaskOutcome.UP_TO_DATE, "main")
      assertVariantHeaders<Java>(projectRoot, "main")
    }
  }

  @Test fun `latest - generates headers for Scala`() {
    projectRoot.writeScalaExampleFile()

    buildProject(buildFile) {
      plugins {
        scala()
      }

      scala {
        // Looks like some scala plugin bug is forcing this: https://github.com/gradle/gradle/issues/36466
        additionalParameters = true
        jdk = 17
      }
    }

    assertRuns(
      projectDir = projectRoot,
      compatibleMatrix = Latest,
      android = false,
    ) { firstRun, secondRun ->
      firstRun
        .assertVariantOutcome<Scala>(TaskOutcome.SUCCESS, "main")
        .assertVariantOutcome<Java>(TaskOutcome.NO_SOURCE, "main")
      secondRun
        .assertVariantOutcome<Scala>(TaskOutcome.UP_TO_DATE, "main")
      assertVariantHeaders<Scala>(projectRoot, "main")
    }
  }

  @Test fun `min - generates headers for Scala`() {
    projectRoot.writeScalaExampleFile()

    buildProject(buildFile) {
      plugins {
        scala()
      }

      scala {
        scalaVersion = Min.scala
      }
    }

    assertRuns(
      projectDir = projectRoot,
      compatibleMatrix = Min,
      android = false,
    ) { firstRun, secondRun ->
      firstRun
        .assertVariantOutcome<Scala>(TaskOutcome.SUCCESS, "main")
        .assertVariantOutcome<Java>(TaskOutcome.NO_SOURCE, "main")
      secondRun
        .assertVariantOutcome<Scala>(TaskOutcome.UP_TO_DATE, "main")
      assertVariantHeaders<Scala>(projectRoot, "main")
    }
  }
}
