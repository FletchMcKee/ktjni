// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
rootProject.name = "ktjni"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

pluginManagement {
  includeBuild("build-logic")
  repositories {
    @Suppress("UnstableApiUsage")
    maven {
      name = "localRepo"
      url = uri(layout.settingsDirectory.dir("plugin/build/repo"))
    }

    google()
    mavenCentral()
    gradlePluginPortal()
  }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }
}

include(
  ":plugin",
  ":samples:demo",
  ":samples:simple",
)
