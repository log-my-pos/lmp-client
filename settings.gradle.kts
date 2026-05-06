rootProject.name = "LogMyPos-Client"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
	repositories {
		google {
			mavenContent {
				includeGroupAndSubgroups("androidx")
				includeGroupAndSubgroups("com.android")
				includeGroupAndSubgroups("com.google")
			}
		}
		mavenCentral()
		gradlePluginPortal()
	}
}

dependencyResolutionManagement {
	repositories {
		google {
			mavenContent {
				includeGroupAndSubgroups("androidx")
				includeGroupAndSubgroups("com.android")
				includeGroupAndSubgroups("com.google")
				includeGroupAndSubgroups("org.chromium")
			}
		}
		mavenCentral()
		maven("https://api.mapbox.com/downloads/v2/releases/maven")
	}
}

include(":composeApp")