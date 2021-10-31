package arrow.tracing.core

/**
 * [headers] that can be handed off to another system (in the form of HTTP headers),
 * which can then create new spans as children of this one.
 * This allows our trace to span remote calls.
 */
public data class Kernel(val headers: Map<String, String>)
