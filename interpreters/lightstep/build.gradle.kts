kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(project(opentracing))
      }
    }
    jvmMain {
      dependencies {
        implementation(libs.lightstep.jre)
      }
    }
  }
}
