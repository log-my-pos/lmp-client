import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	alias(libs.plugins.androidApplication)
	alias(libs.plugins.composeMultiplatform)
	alias(libs.plugins.composeCompiler)
	alias(libs.plugins.composeHotReload)
	alias(libs.plugins.kotlinSerialization)
}

repositories {
	mavenCentral()
	google()
	maven("https://api.mapbox.com/downloads/v2/releases/maven")
}

dependencies {
	implementation(libs.compose.runtime)
	implementation(libs.compose.foundation)
	implementation(libs.compose.material3)
	implementation(libs.compose.ui)
	implementation(libs.compose.components.resources)
	implementation(libs.compose.uiToolingPreview)

	implementation(libs.androidx.activity.compose)
	implementation(libs.androidx.lifecycle.viewmodelCompose)
	implementation(libs.androidx.lifecycle.runtimeCompose)

	implementation(libs.jetbrains.navigation)

	implementation(libs.mapbox.map)
	implementation(libs.mapbox.android)

	implementation(libs.mapbox.search.android)
	implementation(libs.mapbox.search.android.ui)
	implementation(libs.mapbox.search.discover)
	implementation(libs.mapbox.search.place.autocomplete)
	implementation(libs.mapbox.search.autofill)
	implementation(libs.mapbox.search.offline)

	implementation(libs.tablericons.outline)
	implementation(libs.tablericons.filled)

	implementation(libs.coil)

	implementation(libs.haze)
	implementation(libs.haze.blur)
	implementation(libs.haze.materials)

	debugImplementation(libs.compose.uiTooling)
}


kotlin {
	compilerOptions {
		jvmTarget.set(JvmTarget.JVM_17)
	}
}

android {
	namespace = "dev.pandasystems.logmypos_client"
	compileSdk = libs.versions.android.compileSdk.get().toInt()

	defaultConfig {
		applicationId = "dev.pandasystems.logmypos_client"
		minSdk = libs.versions.android.minSdk.get().toInt()
		targetSdk = libs.versions.android.targetSdk.get().toInt()
		versionCode = 1
		versionName = "1.0"
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
	buildTypes {
		getByName("release") {
			isMinifyEnabled = false
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	buildFeatures {
		compose = true
	}
}

