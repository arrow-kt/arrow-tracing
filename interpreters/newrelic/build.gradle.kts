kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(project(coreEntrypoint))
      }
    }
    jvmMain {
      dependencies {
        implementation(libs.newrelic.telemetry)
        implementation(libs.newrelic.telemetry.core)
        implementation(libs.newrelic.telemetry.okhttp)
      }
    }
  }
}
