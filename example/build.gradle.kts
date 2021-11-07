kotlin {
  sourceSets {
    jvmMain {
      dependencies {
        implementation(project(jaeger))
      }
    }
  }
}
