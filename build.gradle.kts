import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
  kotlin("multiplatform") version "1.5.31" apply false
  id("org.jlleitschuh.gradle.ktlint") version "10.1.0" apply true
  id("io.kotest.multiplatform") version "5.0.0.5"
  id("io.arrow-kt.arrow-gradle-config-nexus") version "0.3.2"
  id("io.arrow-kt.arrow-gradle-config-publish-multiplatform") version "0.3.2"
}

allprojects {
  apply(plugin = "io.kotest.multiplatform")
  apply(plugin = "org.jlleitschuh.gradle.ktlint")
  apply(plugin = "org.gradle.idea")

  group = "io.arrow-kt"
  version = "0.0.1-SNAPSHOT"

  repositories {
    google()
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
  }

  tasks.withType<KotlinCompile> {
    kotlinOptions {
      jvmTarget = "1.8"
      freeCompilerArgs = listOf("-Xjsr305=strict")
    }
  }

  ktlint {
    filter {
      exclude("build.gradle.kts")
    }
  }
}

subprojects {
  if (!name.startsWith("interpreters")) {
    apply(plugin = "org.jetbrains.kotlin.multiplatform")

    kotlin {
      explicitApi()

      targets {
        jvm {
          compilations.all {
            kotlinOptions {
              jvmTarget = "1.8"
            }
          }
        }

        js(IR) {
          browser()
          nodejs()
          useCommonJs()
          binaries.executable()
        }

        linuxX64()

        mingwX64()

        iosArm32()
        iosArm64()
        iosSimulatorArm64()
        iosX64()
        macosArm64()
        macosX64()
        tvosArm64()
        tvosSimulatorArm64()
        tvosX64()
        watchosArm32()
        watchosArm64()
        watchosSimulatorArm64()
        watchosX64()
        watchosX86()
      }

      sourceSets {
        val commonMain by getting {
          dependencies {
            implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")
            compileOnly("io.arrow-kt:arrow-fx-coroutines:1.0.1-SNAPSHOT")
          }
        }

        val jsMain by getting {
          dependsOn(commonMain)
        }

        val jvmMain by getting {
          dependsOn(commonMain)
        }

        val commonTest by getting {
          dependsOn(commonMain)
          dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
            implementation("io.kotest:kotest-assertions-core:5.0.0.M3")
            implementation("io.kotest:kotest-property:5.0.0.M3")
            implementation("io.arrow-kt:arrow-fx-coroutines:1.0.1-SNAPSHOT")
          }
        }

        val jvmTest by getting {
          dependsOn(commonTest)
          dependsOn(jvmMain)
          dependencies {
            implementation("io.kotest:kotest-runner-junit5:5.0.0.M3")
          }
        }

        val jsTest by getting {
          dependsOn(commonTest)
          dependsOn(jsMain)
        }

        val mingwX64Main by getting
        val linuxX64Main by getting
        val iosArm32Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosX64Main by getting
        val macosArm64Main by getting
        val macosX64Main by getting
        val tvosArm64Main by getting
        val tvosSimulatorArm64Main by getting
        val tvosX64Main by getting
        val watchosArm32Main by getting
        val watchosArm64Main by getting
        val watchosSimulatorArm64Main by getting
        val watchosX64Main by getting
        val watchosX86Main by getting

        create("nativeMain") {
          dependsOn(commonMain)
          mingwX64Main.dependsOn(this)
          linuxX64Main.dependsOn(this)
          iosArm32Main.dependsOn(this)
          iosArm64Main.dependsOn(this)
          iosSimulatorArm64Main.dependsOn(this)
          iosX64Main.dependsOn(this)
          macosArm64Main.dependsOn(this)
          macosX64Main.dependsOn(this)
          tvosArm64Main.dependsOn(this)
          tvosSimulatorArm64Main.dependsOn(this)
          tvosX64Main.dependsOn(this)
          watchosArm32Main.dependsOn(this)
          watchosArm64Main.dependsOn(this)
          watchosSimulatorArm64Main.dependsOn(this)
          watchosX64Main.dependsOn(this)
          watchosX86Main.dependsOn(this)
        }
      }
    }

    tasks.named<Test>("jvmTest") {
      useJUnitPlatform()
      testLogging {
        showExceptions = true
        showStandardStreams = true
        events = setOf(
          TestLogEvent.FAILED,
          TestLogEvent.PASSED
        )
        exceptionFormat = TestExceptionFormat.FULL
      }
    }
  }
}

fun Project.kotlin(configure: Action<KotlinMultiplatformExtension>): Unit =
  (this as ExtensionAware).extensions.configure("kotlin", configure)

fun KotlinMultiplatformExtension.targets(configure: Action<Any>): Unit =
  (this as ExtensionAware).extensions.configure("targets", configure)

fun KotlinMultiplatformExtension.sourceSets(configure: Action<NamedDomainObjectContainer<KotlinSourceSet>>): Unit =
  (this as ExtensionAware).extensions.configure("sourceSets", configure)
