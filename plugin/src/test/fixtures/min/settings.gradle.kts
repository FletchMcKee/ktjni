rootProject.name = "fixtures-min"

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

include(":kotlin-android-application")
include(":kotlin-android-library")
include(":java-android-application")
include(":java-android-library")
