kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(project(lightstep))
      }
    }
    jvmMain {
      dependencies {
        implementation(libs.lightstep.okhtpp)
      }
    }
  }
}
