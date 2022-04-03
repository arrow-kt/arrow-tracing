enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")

plugins {
  id("com.gradle.enterprise") version "3.8.1"
}

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      from(files("gradle/projects.libs.versions.toml"))
      val kotlinVersion: String? by settings
      kotlinVersion?.let { version("kotlin", it) }
    }
  }

  repositories {
    mavenCentral()
  }
}

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
  }
}

rootProject.name = "arrow-tracing"

include("core-entrypoint")

// interpreters
include(
  "interpreters",
  "interpreters:opensensus",
  "interpreters:opentracing",
  "interpreters:opentelemetry",
  "interpreters:jaeger",
  "interpreters:datadog",
  "interpreters:lightstep",
  "interpreters:lightstepGrpc",
  "interpreters:lightstepHttp",
  "interpreters:newrelic",
  "interpreters:honeycomb"
)

include("example")
