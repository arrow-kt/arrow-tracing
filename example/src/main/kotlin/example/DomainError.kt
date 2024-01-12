package example

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException

public sealed interface DomainError

public sealed interface ValidationError : DomainError

@OptIn(ExperimentalSerializationApi::class)
public data class IncorrectJson(val exception: MissingFieldException) : ValidationError

public data class EmptyUpdate(val description: String) : ValidationError

/*public data class IncorrectInput(val errors: NonEmptyList<InvalidField>) : ValidationError {
  public constructor(head: InvalidField) : this(nonEmptyListOf(head))
}*/

public data class MissingParameter(val name: String) : ValidationError

public sealed interface UserError : DomainError

public data class UserNotFound(val property: String) : UserError

public data class EmailAlreadyExists(val email: String) : UserError

public data class UsernameAlreadyExists(val username: String) : UserError

public object PasswordNotMatched : UserError

public sealed interface JwtError : DomainError

public data class JwtGeneration(val description: String) : JwtError

public data class JwtInvalid(val description: String) : JwtError

public sealed interface ArticleError : DomainError

public data class CannotGenerateSlug(val description: String) : ArticleError

public data class ArticleBySlugNotFound(val slug: String) : ArticleError
