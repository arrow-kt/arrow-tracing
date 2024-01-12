package example.env

import arrow.fx.coroutines.continuations.ResourceScope
import com.sksamuel.cohort.HealthCheckRegistry
import com.sksamuel.cohort.hikari.HikariConnectionsHealthCheck
import example.repo.UserPersistence
import example.repo.userPersistence
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Dispatchers

public class Dependencies(
  // val userService: UserService,
  // val jwtService: JwtService,
  // val articleService: ArticleService,
  public val healthCheck: HealthCheckRegistry,
  // val tagPersistence: TagPersistence,
  public val userPersistence: UserPersistence
)

public suspend fun ResourceScope.dependencies(env: Env): Dependencies {
  val hikari = hikari(env.dataSource)
  val sqlDelight = sqlDelight(hikari)
  val userRepo = userPersistence(sqlDelight.usersQueries, sqlDelight.followingQueries)
  // val articleRepo = articleRepo(sqlDelight.articlesQueries, sqlDelight.tagsQueries)
  // val tagPersistence = tagPersistence(sqlDelight.tagsQueries)
  // val favouritePersistence = favouritePersistence(sqlDelight.favoritesQueries)
  // val jwtService = jwtService(env.auth, userRepo)
  // val slugGenerator = slugifyGenerator()
  // val userService = userService(userRepo, jwtService)

  val checks =
    HealthCheckRegistry(Dispatchers.Default) {
      val check = HikariConnectionsHealthCheck(hikari, 1)
      register(check.name, check, 5.seconds, 5.seconds)
    }

  return Dependencies(
    // userService = userService,
    // jwtService = jwtService,
    // articleService =
    // articleService(slugGenerator, articleRepo, userRepo, tagPersistence, favouritePersistence),
    healthCheck = checks,
    // tagPersistence = tagPersistence,
    userPersistence = userRepo
  )
}
