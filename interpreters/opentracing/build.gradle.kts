kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(project(coreEntrypoint))
      }
    }
    jvmMain {
      dependencies {
        implementation(libs.opentracing.api)
        implementation(libs.opentracing.util)
      }
    }
  }
}
