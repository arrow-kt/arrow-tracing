kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(project(coreEntrypoint))
      }
    }
    jvmMain {
      dependencies {

      }
    }
  }
}
