// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  alias(libs.plugins.ktjni.spotless)
  alias(libs.plugins.binary.compatibility.validator)
  alias(libs.plugins.maven.publish)
}

kotlin {
  explicitApi()

  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_11)
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<ValidatePlugins>().configureEach {
  enableStricterValidation = true
}

val localRepo: Provider<Directory> = layout.buildDirectory.dir("local-repo")

tasks.withType<Test>().configureEach {
  maxHeapSize = "1g"
  useJUnitPlatform()

  testLogging {
    events(TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.PASSED)
    exceptionFormat = TestExceptionFormat.FULL
  }

  dependsOn("publishAllPublicationsToLocalRepoRepository")
  inputs
    .files(localRepo.map { it.asFileTree.matching { exclude("**/maven-metadata.xml*") } })
    .withPathSensitivity(PathSensitivity.RELATIVE)
    .withPropertyName("localRepo")

  systemProperties["ktjniVersion"] = providers.gradleProperty("VERSION_NAME").get()
  systemProperties["agpVersion"] = libs.versions.agp.get()
  systemProperties["kgpVersion"] = libs.versions.kotlin.get()
  systemProperties["scalaVersion"] = libs.versions.scala.get()
  val localRepoPath: Provider<String> = localRepo.map { it.asFile.absolutePath }

  doFirst {
    systemProperties["localRepoPath"] = localRepoPath.get()
  }
}

configurations {
  compileClasspath {
    attributes {
      attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
    }
  }

  testCompileClasspath {
    attributes {
      attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
    }
  }

  testRuntimeClasspath {
    attributes {
      attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
    }
  }
}

dependencies {
  // TODO: Shadow these
  implementation(libs.asm)
  implementation(libs.asm.tree)

  compileOnly(libs.kotlin.gradlePlugin)
  compileOnly(libs.android.api.gradlePlugin)
  compileOnly(gradleApi())
  compileOnly(localGroovy())

  testImplementation(libs.junit.jupiter)
  testImplementation(libs.junit.jupiter.params)
  testImplementation(libs.kotlin.test.junit5)
  testImplementation(libs.google.truth)
  testImplementation(gradleTestKit())

  testRuntimeOnly(libs.junit.platform.launcher)
}

gradlePlugin {
  plugins {
    register("ktjni") {
      id = "io.github.fletchmckee.ktjni"
      implementationClass = "io.github.fletchmckee.ktjni.KtjniPlugin"
    }
  }
}

publishing {
  repositories {
    maven {
      name = "localRepo"
      url = uri(localRepo)
    }
  }
}

mavenPublishing {
  configure(
    GradlePlugin(javadocJar = JavadocJar.Javadoc()),
  )
}
