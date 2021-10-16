data class MyState(
    val duration: Int,
    val value: Int
) {
    fun incrementedBy(count: Int): MyState {
        return copy(value = value + count)
    }

    fun ticked(): MyState {
        return copy(duration = duration + 1)
    }

    companion object {
        val empty: MyState get() = MyState(-1, -1)
    }
}
