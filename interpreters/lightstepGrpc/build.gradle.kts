kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(project(lightstep))
      }
    }
    jvmMain {
      dependencies {
        implementation("com.lightstep.tracer:tracer-grpc:0.30.3")
        // grpc dependency with ktor
      }
    }
  }
}
