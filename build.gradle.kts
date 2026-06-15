import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	alias(libs.plugins.androidApplication)
	alias(libs.plugins.composeMultiplatform)
	alias(libs.plugins.composeCompiler)
	alias(libs.plugins.composeHotReload)
	alias(libs.plugins.kotlinSerialization)
	alias(libs.plugins.ksp)
	alias(libs.plugins.koin.compiler)
}

repositories {
	mavenCentral()
	google()
	// Mapbox Maven repository
	maven("https://api.mapbox.com/downloads/v2/releases/maven") {
		authentication {
			create<BasicAuthentication>("basic")
		}
		credentials {
			// Do not change the username below.
			// This should always be `mapbox` (not your username).
			username = "mapbox"
			password = providers.gradleProperty("mapbox_secret_token").get()
		}
	}
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

	implementation(libs.androidx.room.runtime)
	implementation(libs.androidx.room.ktx)
	ksp(libs.androidx.room.compiler)

	implementation(libs.kotlinx.datetime)

	implementation(platform(libs.koin.bom))
	implementation(libs.koin.core)
	implementation(libs.koin.compose)
	implementation(libs.koin.compose.viewmodel)
	implementation(libs.koin.android)
	implementation(libs.koin.androidx.compose)

	implementation(libs.voyager.navigator)
	implementation(libs.voyager.screenModel)
	implementation(libs.voyager.koin)

	implementation(libs.mapbox.map.compose)
	implementation(libs.mapbox.map.android)

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
		jvmTarget.set(JvmTarget.JVM_21)
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
		sourceCompatibility = JavaVersion.VERSION_21
		targetCompatibility = JavaVersion.VERSION_21
	}
	buildFeatures {
		compose = true
	}
}

