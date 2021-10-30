package arrow.tracing.core

/**
 * An opaque hunk of data that can we can hand off to another system (in the form of HTTP headers),
 * which can then create new spans as children of this one. By this mechanism we allow our trace
 * to span remote calls.
 */
public data class Kernel(val headers: Map<String, String>)
