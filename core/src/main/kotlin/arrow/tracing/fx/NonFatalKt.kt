package arrow.tracing.fx

import kotlin.coroutines.cancellation.CancellationException

public fun NonFatal(t: Throwable): Boolean =
  when (t) {
    is VirtualMachineError,
    is ThreadDeath,
    is InterruptedException,
    is LinkageError,
    is CancellationException -> false
    else -> true
  }
