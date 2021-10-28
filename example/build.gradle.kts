plugins {
  kotlin("multiplatform")
  id("org.jlleitschuh.gradle.ktlint")
}

kotlin {
  targets {
    jvm {
      compilations.all {
        kotlinOptions {
          jvmTarget = "1.8"
        }
      }
    }
  }

  sourceSets {
    val jvmMain by getting {
      dependencies {
        implementation(project(jaeger))

      }
    }
  }
}
