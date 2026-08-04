// Copyright 2026, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.ktjni.util

// Not really a matrix, but the plan is to test min and latest.
// TODO: Remove hardcoded values.
enum class CompatibleMatrix(
  val agp: String,
  val kgp: String,
  val gradle: String? = null,
  val compileSdk: Int = 37,
  val scala: String = "",
) {
  Min(
    agp = "8.3.0",
    kgp = "1.9.0",
    gradle = "8.14.4",
    compileSdk = 34,
    scala = "2.13.12",
  ),
  Latest(
    agp = AgpVersion,
    kgp = KgpVersion,
    scala = ScalaVersion,
  ),
}

sealed interface Language {
  data object Kotlin : Language
  data object Java : Language
  data object Scala : Language
}
