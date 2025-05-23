plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	id("kotlin-parcelize")
}

buildscript {
	val kotlinVersion by extra("1.9.0")
}

android {
	namespace = "org.lemonadestand.btb"
	compileSdk = 34

	defaultConfig {
		applicationId = "org.lemonadestand.btb"
		minSdk = 28
		targetSdk = 34
		versionCode = 10
		versionName = "1.10"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}

	buildFeatures {
		viewBinding = true
		buildConfig = true
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	kotlinOptions {
		jvmTarget = "17"
	}
}

dependencies {
	implementation("com.google.android.material:material:1.12.0")
	implementation("com.google.android.material:material:1.12.0")
	implementation("androidx.constraintlayout:constraintlayout:2.2.0")
	implementation("androidx.appcompat:appcompat:1.7.0")
	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.2.1")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
	implementation("com.google.android.material:material:1.12.0")

	implementation("com.squareup.retrofit2:retrofit:2.7.1")
	implementation("com.squareup.retrofit2:converter-gson:2.6.2")
	implementation("com.squareup.retrofit2:converter-scalars:2.3.0")
	implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
	implementation("com.squareup.okhttp3:okhttp:4.9.3")
	implementation("com.google.code.gson:gson:2.10.1")

	/* implementation("androidx.navigation:navigation-fragment-ktx:2.5.1")
	 implementation("androidx.navigation:navigation-ui-ktx:2.5.1")*/

	// Navigation
	implementation("androidx.navigation:navigation-fragment-ktx:2.8.4")
	implementation("androidx.navigation:navigation-ui-ktx:2.8.4")
	implementation("androidx.navigation:navigation-dynamic-features-fragment:2.8.4")
	androidTestImplementation("androidx.navigation:navigation-testing:2.8.4")
	implementation("androidx.navigation:navigation-compose:2.8.4")

	implementation("com.github.bumptech.glide:glide:4.16.0")

	implementation("com.intuit.sdp:sdp-android:1.1.0")
	//implementation(project(":swipe-to-action"))
	implementation("com.github.ChanTsune:swipe-to-action:0.0.0-alpha4")


	// Coroutines
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

	// Coroutine Lifecycle Scopes
	implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
	implementation("com.facebook.shimmer:shimmer:0.5.0")
	implementation("com.github.appsfeature:html-editor:1.1")
	implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
	implementation("com.github.razaghimahdi:Android-Loading-Dots:1.3.1")

	implementation("com.jaredrummler:colorpicker:1.1.0")
	implementation("com.github.yukuku:ambilwarna:2.0.1")


	implementation("com.google.firebase:firebase-messaging:24.0.3")

	implementation("com.amazonaws:aws-android-sdk-s3:2.22.1")

	implementation("androidx.webkit:webkit:1.12.1")
}

apply(plugin = "androidx.navigation.safeargs.kotlin")