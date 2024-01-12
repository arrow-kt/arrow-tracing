package example

import arrow.fx.coroutines.continuations.resource
import com.zaxxer.hikari.HikariDataSource
import example.env.Dependencies
import example.env.Env
import example.env.dependencies
import example.env.hikari
import io.kotest.assertions.arrow.fx.coroutines.ProjectResource
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.extensions.testcontainers.StartablePerProjectListener
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait

private class PostgresSQL : PostgreSQLContainer<PostgresSQL>("postgres:14.10") {
  init { // Needed for Apple M1 tests
    waitingFor(Wait.forListeningPort())
  }
}

/**
 * Configuration of our Kotest Test Project. It contains our Test Container configuration which is
 * used in almost all tests.
 */
object KotestProject : AbstractProjectConfig() {
  private val postgres = PostgresSQL()

  private val dataSource: Env.DataSource by lazy {
    print("URL _____ ${postgres.jdbcUrl}")
    Env.DataSource(postgres.jdbcUrl, postgres.username, postgres.password, postgres.driverClassName)
  }

  private val env: Env by lazy { Env().copy(dataSource = dataSource) }

  // global dependencies for tests
  val dependencies: ProjectResource<Dependencies> = ProjectResource(resource { dependencies(env) })
  private val hikari: ProjectResource<HikariDataSource> =
    ProjectResource(resource { hikari(env.dataSource) })

  override val globalAssertSoftly: Boolean = true

  private val resetDatabaseListener =
    object : TestListener {
      override suspend fun afterTest(testCase: TestCase, result: TestResult) {
        super.afterTest(testCase, result)
        // cleanup of users
        hikari.get().connection.use { conn ->
          conn.prepareStatement("TRUNCATE users CASCADE").executeLargeUpdate()
        }
      }
    }

  override fun extensions(): List<Extension> =
    listOf(StartablePerProjectListener(postgres), hikari, dependencies, resetDatabaseListener)
}
