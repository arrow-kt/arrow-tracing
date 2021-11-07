kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(project(lightstep))
      }
    }
    jvmMain {
      dependencies {
        implementation(libs.lightstep.grpc)
        // grpc dependency with ktor
      }
    }
  }
}
