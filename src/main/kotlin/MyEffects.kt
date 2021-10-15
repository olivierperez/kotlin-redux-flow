sealed interface MyEffect {
    object Load : MyEffect
    data class RandomIncrement(val min: Int, val max: Int) : MyEffect
}