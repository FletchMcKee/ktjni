// Copyright 2026, Colin McKee
// SPDX-License-Identifier: Apache-2.0
plugins {
  `kotlin-dsl`
}

dependencies {
  compileOnly(libs.android.api.gradlePlugin)
  compileOnly(libs.kotlin.gradlePlugin)
  compileOnly(libs.spotless.gradlePlugin)
  compileOnly(libs.shadow.gradlePlugin)
}

tasks.validatePlugins {
  enableStricterValidation = true
  failOnWarning = true
}

gradlePlugin {
  plugins {
    register("root") {
      id = libs.plugins.ktjni.root.get().pluginId
      implementationClass = "RootConventionPlugin"
    }

    register("spotless") {
      id = libs.plugins.ktjni.spotless.get().pluginId
      implementationClass = "SpotlessConventionPlugin"
    }

    register("shadow") {
      id = libs.plugins.ktjni.shadow.get().pluginId
      implementationClass = "ShadowConventionPlugin"
    }
  }
}
