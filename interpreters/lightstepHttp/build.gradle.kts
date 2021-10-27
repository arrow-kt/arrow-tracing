kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(project(lightstep))
      }
    }
    jvmMain {
      dependencies {
        implementation("com.lightstep.tracer:tracer-okhttp:0.30.3")

      }
    }
  }
}
