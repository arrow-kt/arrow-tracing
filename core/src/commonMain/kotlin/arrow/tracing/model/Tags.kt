package arrow.tracing.model

public object Tags {
  /** The software package, framework, library, or module that generated the associated Span. E.g., "grpc", "django", "JDBI". */
  public fun component(packageName: String): Pair<String, StringValue> =
    "component.packageName" to packageName.toTraceValue()

  /** true if and only if the application considers the operation represented by the Span to have failed */
  public fun error(boolean: Boolean): Pair<String, BooleanValue> = "operation.error" to boolean.toTraceValue()
}

public object SamplingTags {
  /**
   * If greater than 0, a hint to the Tracer to do its best to capture the trace.
   * If 0, a hint to the trace to not-capture the trace.
   * If absent, the Tracer should use its default sampling mechanism.
   */
  public fun priorityOf(priority: Int): Pair<String, IntValue> = "sampling.priority" to priority.toTraceValue()
}

public object MessageBusTags {
  /**
   * An address at which messages can be exchanged.
   * E.g. A Kafka record has an associated "topic name" that
   * can be extracted by the instrumented producer or consumer and stored using this tag.
   */
  public fun destination(address: String): Pair<String, StringValue> =
    "message_bus.destination" to address.toTraceValue()
}

public object SpanTags {

  public const val clientKind: String = "client"

  public const val serverKind: String = "server"

  public const val producerKind: String = "producer"

  public const val consumerKind: String = "consumer"

  /**
   * Either "client" or "server" for the appropriate roles in an RPC,
   * and "producer" or "consumer" for the appropriate roles in a messaging scenario.
   */
  public fun kindOf(kind: String): Pair<String, StringValue> = "span.kind" to kind.toTraceValue()
}

public object HttpTags {
  private const val prefix = "http"

  /** HTTP method of the request for the associated Span. E.g., "GET", "POST" */
  public fun methodOf(method: String): Pair<String, StringValue> = "$prefix.method" to method.toTraceValue()

  /** HTTP response status code for the associated Span. E.g., 200, 503, 404 */
  public fun statusCodeOf(statusCode: Int): Pair<String, IntValue> = "$prefix.statusCode" to statusCode.toTraceValue()

  /**
   * URL of the request being handled in this segment of the trace, in standard URI format.
   * E.g., "https://domain.net/path/to?resource=here"
   */
  public fun url(uri: String): Pair<String, StringValue> = "$prefix.url" to uri.toTraceValue()
}

public object PeerTags {
  private const val prefix = "peer"

  /**
   * Remote "address", suitable for use in a networking client library.
   * This may be a "ip:port", a bare "hostname", a FQDN, or even a JDBC substring like "mysql://prod-db:3306"
   */
  public fun remoteAddress(address: String): Pair<String, TraceValue> =
    "$prefix.remoteAddress" to address.toTraceValue()

  /** Remote hostname. E.g., "opentracing.io", "internal.dns.name" */
  public fun hostname(name: String): Pair<String, StringValue> = "$prefix.hostname" to name.toTraceValue()

  /** Remote IPv4 address as a .-separated tuple. E.g., "127.0.0.1" */
  public fun ipv4(address: String): Pair<String, StringValue> = "$prefix.ipv4Address" to address.toTraceValue()

  /**
   * Remote IPv6 address as a string of colon-separated 4-char hex tuples.
   * E.g., "2001:0db8:85a3:0000:0000:8a2e:0370:7334"s
   */
  public fun ipv6(address: String): Pair<String, StringValue> = "$prefix.ipv6Address" to address.toTraceValue()

  /** Remote port. E.g., 80 */
  public fun remotePort(port: Int): Pair<String, IntValue> = "$prefix.port" to port.toTraceValue()

  /**
   * Remote service name (for some unspecified definition of "service").
   * E.g., "elasticsearch", "custom_microservice", "memcache"
   */
  public fun serviceName(name: String): Pair<String, StringValue> = "$prefix.service" to name.toTraceValue()
}

public object DbTags {
  private const val prefix = "db"

  /**
   * Database instance name.
   * E.g., In java, if the jdbc.url="jdbc:mysql://127.0.0.1:3306/customers", the instance name is "customers".
   */
  public fun instanceNameOf(name: String): Pair<String, StringValue> = "$prefix.instanceName" to name.toTraceValue()

  /**
   * A database statement for the given database type.
   * E.g., for db.type="sql", "SELECT * FROM wuser_table"; for db.type="redis", "SET mykey 'WuValue'".
   */
  public fun statementTypeOf(type: String): Pair<String, StringValue> = "$prefix.statementType" to type.toTraceValue()

  /**
   * Database type.
   * For any SQL database, "sql". For others, the lower-case database category, e.g. "cassandra", "hbase", or "redis".
   */
  public fun type(type: String): Pair<String, StringValue> = "$prefix.type" to type.toTraceValue()

  /**
   * Username for accessing database.
   * E.g., "readonly_user" or "reporting_user"
   */
  public fun userName(userName: String): Pair<String, StringValue> = "$prefix.username" to userName.toTraceValue()
}
