@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
}

kotlin {
  // explicitApi()
}

dependencies {
  implementation(libs.kotlin.stdlib)
  api(libs.coroutines.core)
  testImplementation(libs.bundles.kotest.jvm)
}

tasks {
  withType<JavaCompile>().configureEach {
    targetCompatibility = "${JavaVersion.toVersion(8)}"
    sourceCompatibility = "${JavaVersion.toVersion(8)}"
  }

  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
      jvmTarget = "1.8"
    }
  }
}
