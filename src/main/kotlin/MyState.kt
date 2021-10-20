data class MyState(
    val duration: Int,
    val value: Int,
    val error: Int
) {
    fun incrementedBy(count: Int): MyState {
        return copy(value = value + count)
    }

    fun ticked(): MyState {
        return copy(duration = duration + 1)
    }

    fun toError(errorCode: Int): MyState {
        return copy(error = errorCode)
    }

    companion object {
        val empty: MyState get() = MyState(-1, -1, 0)
    }
}
