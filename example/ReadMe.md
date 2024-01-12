# Example Project Ktor + Arrow Fx/ SuspendApp + Arrow Tracing

highly inspired by https://github.com/nomisRev/ktor-arrow-example/tree/main

### How it works

This example uses Kotlin with Ktor and Arrow as the main building blocks. Other technologies used:

- SqlDelight for the persistence layer
- Kotest for testing
- Jaeger for Distributed Tracing

### Running the project

To run the project, you first need to start the environment. This can be done with docker-compose up, and then you can start the Ktor server with ./gradlew run.

```ssh
docker-compose up
./gradlew run
curl -i 0.0.0.0:8080/readiness
```
Beware that ./gradlew run doesn't properly run JVM Shutdown hooks, and the port remains bound.

```ssh
gradle -stop
```