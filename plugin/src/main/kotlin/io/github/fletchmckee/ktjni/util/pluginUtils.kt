// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.ktjni.util

internal const val GROUP = "ktjni"

internal val String.titleCase: String get() = replaceFirstChar(Char::uppercase)
