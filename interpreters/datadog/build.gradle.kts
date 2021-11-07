kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(project(opentracing))
      }
    }
    jvmMain {
      dependencies {
        implementation(libs.datadog.api)
        implementation(libs.datadog.ot)
      }
    }
  }
}
