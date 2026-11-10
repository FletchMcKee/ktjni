// Copyright 2026, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.Bundling
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register

@Suppress("Unused", "UnstableApiUsage")
class ShadowConventionPlugin : Plugin<Project> {
  override fun apply(target: Project): Unit = with(target) {
    pluginManager.withPlugin("org.gradle.java") {
      val sourceSets = extensions.getByType<JavaPluginExtension>().sourceSets
      val main = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
      val test = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME)

      val embedded = configurations.dependencyScope("embedded")
      val embeddedClasspath = configurations.resolvable("embeddedClasspath") { extendsFrom(embedded) }

      configurations.named(main.compileOnlyConfigurationName) { extendsFrom(embedded) }
      configurations.named(test.implementationConfigurationName) { extendsFrom(embedded) }

      val shadowJar = tasks.register<ShadowJar>("shadowJar") {
        description = "Relocates embedded dependencies."
        from(main.output)
        exclude("module-info.class")

        configurations.add(embeddedClasspath)
        destinationDirectory.set(layout.buildDirectory.dir("shadow"))
      }

      listOf(main.apiElementsConfigurationName, main.runtimeElementsConfigurationName).forEach { name ->
        configurations.named(name) {
          attributes { attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.SHADOWED)) }
          outgoing {
            artifacts.clear()
            artifact(shadowJar)
          }
        }
      }
    }
  }
}
