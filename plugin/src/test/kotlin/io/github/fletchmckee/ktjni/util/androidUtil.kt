// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.ktjni.util

import java.io.File
import java.util.Properties
import org.gradle.testkit.runner.GradleRunner

internal fun androidHome(): String {
  System.getenv("ANDROID_SDK_ROOT")?.let { return it.withInvariantPathSeparators() }
  System.getenv("ANDROID_HOME")?.let { return it.withInvariantPathSeparators() }

  val localProp = File(File(System.getProperty("user.dir")).parentFile, "local.properties")
  check(localProp.exists()) { "Missing 'ANDROID_HOME' environment variable or local.properties with 'sdk.dir'" }
  val prop = Properties()
  localProp.inputStream().use {
    prop.load(it)
  }
  return prop.getProperty("sdk.dir").withInvariantPathSeparators()
}

// https://cs.android.com/android-studio/platform/tools/base/+/mirror-goog-studio-main:common/src/main/java/com/android/prefs/AbstractAndroidLocations.kt?q=xdg_config_home
private val androidPrefsRoot: File = File("build/android-prefs").absoluteFile
private val androidPrefsDir: File = File(androidPrefsRoot, ".android")

private fun createAndroidPrefsDir() {
  File(androidPrefsDir, "cache").mkdirs()
  System.setProperty("ANDROID_USER_HOME", androidPrefsDir.absolutePath.withInvariantPathSeparators())
  System.setProperty("ANDROID_PREFS_ROOT", androidPrefsRoot.absolutePath.withInvariantPathSeparators())
}

internal fun GradleRunner.withAndroidConfiguration(
  projectRoot: File,
  builtInKotlin: Boolean = true,
): GradleRunner {
  createAndroidPrefsDir()
  File(projectRoot, "gradle.properties").writeText(
    """
      org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=1g
      android.useAndroidX=true
      android.builtInKotlin=$builtInKotlin
      android.newDsl=$builtInKotlin
    """.trimIndent(),
  )
  File(projectRoot, "local.properties").apply {
    if (!exists()) writeText("sdk.dir=${androidHome()}\n")
  }
  return withProjectDir(projectRoot)
    .withTestKitDir(File("build/gradle-test-kit").absoluteFile)
}

internal fun File.writeAndroidManifestFile(sourceSet: String = "main") {
  File(this, "src/$sourceSet/AndroidManifest.xml").writeText(
    """
      <!--suppress XmlUnusedNamespaceDeclaration -->
      <manifest xmlns:android="http://schemas.android.com/apk/res/android"/>
    """.trimIndent(),
  )
}
