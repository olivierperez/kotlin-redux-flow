sealed interface MyAction {
    class Set(val initialValue: Int) : MyAction
    class Increment(val count: Int) : MyAction
    object Tick : MyAction
}