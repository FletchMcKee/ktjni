// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.ktjni.internal

import io.github.fletchmckee.ktjni.KtjniExtension
import io.github.fletchmckee.ktjni.registerKtjniTask
import io.github.fletchmckee.ktjni.util.titleCase
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.kotlinToolingVersion
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.tooling.core.KotlinToolingVersion

// Can remove when KGP minimum is 2.1.20
context(ktjniExtension: KtjniExtension)
internal fun Project.configureKotlinMultiplatform() = pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
  if (kotlinToolingVersion >= KotlinToolingVersion("2.1.20")) return@withPlugin

  val kotlinExtension = extensions.getByType(KotlinMultiplatformExtension::class.java)
  kotlinExtension.targets.withType(KotlinJvmTarget::class.java).configureEach jvmTarget@{
    compilations.configureEach {
      registerKtjniTask("${target.name}${name.titleCase}", output.classesDirs).configure {
        @Suppress("Deprecation")
        enabled = !this@jvmTarget.withJavaEnabled
      }
    }
  }
}
