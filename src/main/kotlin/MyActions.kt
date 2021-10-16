sealed interface MyAction {
    class Set(val state: MyState) : MyAction
    class Increment(val count: Int) : MyAction
    object Tick : MyAction
}