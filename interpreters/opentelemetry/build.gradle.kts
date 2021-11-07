kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(project(coreEntrypoint))
      }
    }
    jvmMain {
      dependencies {

      }
    }
  }
}
