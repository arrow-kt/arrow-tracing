kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(project(coreEntrypoint))
      }
    }
    jvmMain {
      dependencies {
        implementation("com.newrelic.telemetry:telemetry:0.10.0")
        implementation("com.newrelic.telemetry:telemetry-core:0.12.0")
        implementation("com.newrelic.telemetry:telemetry-http-okhttp:0.12.0")
      }
    }
  }
}
