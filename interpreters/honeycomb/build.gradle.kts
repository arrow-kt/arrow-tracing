kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(project(core))
      }
    }
    jvmMain {
      dependencies {
        implementation("io.honeycomb.libhoney:libhoney-java:1.3.1")
      }
    }
  }
}
