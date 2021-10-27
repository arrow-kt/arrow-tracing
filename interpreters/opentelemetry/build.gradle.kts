kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(project(core))
      }
    }
    jvmMain {
      dependencies {

      }
    }
  }
}
