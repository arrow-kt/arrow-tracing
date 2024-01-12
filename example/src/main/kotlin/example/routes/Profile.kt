package example.routes

import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable public data class ProfileWrapper<T : Any>(val profile: T)

@Serializable
public data class Profile(
  val username: String,
  val bio: String,
  val image: String,
  val following: Boolean
)
