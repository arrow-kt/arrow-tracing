@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
}

kotlin {
  explicitApi()

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(libs.kotlin.stdlibCommon)
        api(libs.arrow.fx)
      }
    }

    val jvmMain by getting {
      dependsOn(commonMain)
    }

    val commonTest by getting {
      dependsOn(commonMain)
      dependencies {
        implementation(libs.bundles.kotest.mpp)
      }
    }

    val jvmTest by getting {
      dependsOn(commonTest)
      dependsOn(jvmMain)
      dependencies {
        implementation(libs.kotest.runnerJUnit5)
      }
    }
  }
}
