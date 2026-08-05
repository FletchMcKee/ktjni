// Copyright 2026, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.ktjni.dsl

import io.github.fletchmckee.ktjni.util.KtjniVersion
import io.github.fletchmckee.ktjni.util.ScalaVersion
import java.io.File

@DslMarker
internal annotation class KtjniTestDsl

@KtjniTestDsl
internal class ProjectBuilder {
  private val plugins = PluginsBlock()
  private var android: AndroidBlock? = null
  private var kotlin: KotlinBlock? = null
  private var java: JavaBlock? = null
  private var scala: ScalaBlock? = null

  fun plugins(block: PluginsBlock.() -> Unit) {
    plugins.apply(block)
  }

  fun android(block: AndroidBlock.() -> Unit = {}) {
    android = AndroidBlock().apply(block)
  }

  fun kotlin(block: KotlinBlock.() -> Unit = {}) {
    kotlin = KotlinBlock().apply(block)
  }

  fun java(block: JavaBlock.() -> Unit = {}) {
    java = JavaBlock().apply(block)
  }

  fun scala(block: ScalaBlock.() -> Unit = {}) {
    scala = ScalaBlock().apply(block)
  }

  internal fun render(): String = buildString {
    appendLine(plugins.render())
    android?.let { appendLine(it.render()) }
    kotlin?.let { appendLine(it.render()) }
    java?.let { appendLine(it.render()) }
    scala?.let { appendLine(it.render()) }
  }
}

@KtjniTestDsl
internal class PluginsBlock {
  private val plugins = mutableListOf<String>()

  fun androidLibrary(version: String) = add("com.android.library", version)
  fun androidApplication(version: String) = add("com.android.application", version)
  fun androidMultiplatform(version: String) = add("com.android.kotlin.multiplatform.library", version)
  fun kotlinAndroid(version: String) = add("org.jetbrains.kotlin.android", version)
  fun kotlinJvm(version: String) = add("org.jetbrains.kotlin.jvm", version)
  fun kotlinMultiplatform(version: String) = add("org.jetbrains.kotlin.multiplatform", version)
  fun java() = add("java", null)
  fun scala() = add("scala", null)

  private fun add(id: String, version: String?) {
    plugins += """id("$id") ${version?.let { """version "$version"""" }.orEmpty()}"""
  }

  internal fun render(): String = buildString {
    appendLine("plugins {")
    plugins.forEach { appendLine("  $it") }
    appendLine("  id(\"io.github.fletchmckee.ktjni\") version \"$KtjniVersion\"")
    append("}")
  }
}

@KtjniTestDsl
internal class AndroidBlock {
  var namespace: String = "com.example"
  var compileSdk: Int = 37
  var minSdk: Int = 23
  var jdk = 11

  internal fun render(): String = """
    android {
      namespace = "$namespace"
      compileSdk = $compileSdk

      defaultConfig {
        minSdk = $minSdk
      }

      compileOptions {
        sourceCompatibility = JavaVersion.VERSION_$jdk
        targetCompatibility = JavaVersion.VERSION_$jdk
      }
    }
  """.trimIndent()
}

@KtjniTestDsl
internal class AndroidMultiplatformBlock {
  var namespace: String = "com.example"
  var compileSdk: Int = 37
  var minSdk: Int = 23
  var jvmTarget = 11

  internal fun render(): String = """
    android {
      namespace = "$namespace"
      compileSdk = $compileSdk
      minSdk = $minSdk

      compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_$jvmTarget)
      }
    }
  """.trimIndent()
}

@KtjniTestDsl
internal class KotlinBlock {
  var jvmTarget = 11

  private val targets = mutableListOf<String>()
  private var android: AndroidMultiplatformBlock? = null

  fun jvm(withJava: Boolean = false) {
    targets += if (withJava) "jvm { withJava() }" else "jvm()"
  }

  fun androidTarget() {
    targets += "androidTarget { publishLibraryVariants(\"release\") }"
  }

  fun android(block: AndroidMultiplatformBlock.() -> Unit = {}) {
    android = AndroidMultiplatformBlock().apply(block)
  }

  private val isMultiplatform: Boolean
    get() = targets.isNotEmpty() || android != null

  internal fun render(): String = buildString {
    append(
      """
      tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
          jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_$jvmTarget)
        }
      }

      """.trimIndent(),
    )
    appendLine("kotlin {")
    if (isMultiplatform) {
      targets.forEach { appendLine("  $it") }
      android?.let { appendLine(it.render()) }
    } else {
      appendLine(
        """
        compilerOptions {
          jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_$jvmTarget)
        }

        """.trimIndent(),
      )
    }
    append("}")
  }
}

@KtjniTestDsl
internal class JavaBlock {
  var sourceCompatibility = 11
  var targetCompatibility = 11
  var toolchain: Int? = null

  internal fun render(): String = buildString {
    appendLine("java {")
    toolchain?.let {
      appendLine("  toolchain {")
      appendLine("    languageVersion = JavaLanguageVersion.of($it)")
      appendLine("  }")
    }
    appendLine("  sourceCompatibility = JavaVersion.VERSION_$sourceCompatibility")
    appendLine("  targetCompatibility = JavaVersion.VERSION_$targetCompatibility")
    appendLine("}")
  }
}

@KtjniTestDsl
internal class ScalaBlock {
  var scalaVersion = ScalaVersion
  var jdk = 11
  var toolchain: Int? = null
  var additionalParameters = false

  internal fun render(): String = buildString {
    appendLine(
      """
        scala {
          scalaVersion = "$scalaVersion"
        }
      """.trimIndent(),
    )
    if (additionalParameters) {
      appendLine(
        """
          tasks.withType<ScalaCompile>().configureEach {
            scalaCompileOptions.additionalParameters.add("-Xunchecked-java-output-version:$jdk")
          }
        """.trimIndent(),
      )
    }
    appendLine("java {")
    toolchain?.let {
      appendLine("  toolchain {")
      appendLine("    languageVersion = JavaLanguageVersion.of($it)")
      appendLine("  }")
    }
    appendLine("  sourceCompatibility = JavaVersion.VERSION_$jdk")
    appendLine("  targetCompatibility = JavaVersion.VERSION_$jdk")
    appendLine("}")
  }
}

internal fun buildProject(
  buildFile: File,
  block: ProjectBuilder.() -> Unit,
) = buildFile.writeText(ProjectBuilder().apply(block).render())
