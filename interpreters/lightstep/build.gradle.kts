kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(project(opentracing))
      }
    }
    jvmMain {
      dependencies {
        implementation("com.lightstep.tracer:lightstep-tracer-jre:0.30.5")
      }
    }
  }
}
