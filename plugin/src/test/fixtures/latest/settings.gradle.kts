rootProject.name = "fixtures-latest"

pluginManagement {
  repositories {
    exclusiveContent {
      forRepository {
        maven { url = uri(settings.rootDir.resolve("../../../../build/local-repo")) }
      }
      filter {
        includeGroupAndSubgroups("io.github.fletchmckee")
      }
    }
    google {
      content {
        includeGroupAndSubgroups("com.android")
        includeGroupAndSubgroups("com.google")
        includeGroupAndSubgroups("androidx")
      }
    }
    mavenCentral()
  }
}

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      from(files("../../../../../gradle/libs.versions.toml"))
    }
  }

  repositories {
    google {
      content {
        includeGroupAndSubgroups("com.android")
        includeGroupAndSubgroups("com.google")
        includeGroupAndSubgroups("androidx")
      }
    }
    mavenCentral()
  }
}

include(":java-android-application")
include(":java-android-library")
include(":java-library")
include(":kotlin-android-application")
include(":kotlin-android-library")
include(":kotlin-jvm")
include(":kotlin-jvm-with-java")
include(":scala")
