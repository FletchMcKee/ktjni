// Copyright 2026, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import io.github.fletchmckee.ktjni.buildlogic.configureSpotless
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("Unused") // Invoked reflectively
class SpotlessConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    configureSpotless()
  }
}
