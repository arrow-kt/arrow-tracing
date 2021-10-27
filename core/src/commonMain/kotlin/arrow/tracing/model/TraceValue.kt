package arrow.tracing.model

public sealed class TraceValue {
  public abstract val value: Any
}

public data class StringValue(override val value: String) : TraceValue()

public data class BooleanValue(override val value: Boolean) : TraceValue()

public data class DoubleValue(override val value: Double) : TraceValue()

public data class FloatValue(override val value: Float) : TraceValue()

public data class LongValue(override val value: Long) : TraceValue()

public data class IntValue(override val value: Int) : TraceValue()

public data class CharValue(override val value: Char) : TraceValue()

public data class ShortValue(override val value: Short) : TraceValue()

//public data class ByteArrayValue(override val value: ByteArray, val charset: Charset) : TraceValue()

public fun String.toTraceValue(): StringValue =
  StringValue(this)

public fun Boolean.toTraceValue(): BooleanValue =
  BooleanValue(this)

public fun Double.toTraceValue(): DoubleValue =
  DoubleValue(this)

public fun Float.toTraceValue(): FloatValue =
  FloatValue(this)

public fun Long.toTraceValue(): LongValue =
  LongValue(this)

public fun Int.toTraceValue(): IntValue =
  IntValue(this)

public fun Char.toTraceValue(): CharValue =
  CharValue(this)

public fun Short.toTraceValue(): ShortValue =
  ShortValue(this)

//public fun ByteArray.toTraceValue(charset: Charset): ByteArrayValue =
//  ByteArrayValue(this, charset)
