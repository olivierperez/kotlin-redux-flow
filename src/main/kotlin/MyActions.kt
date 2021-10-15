sealed interface MyAction {
    object Init : MyAction
    class Increment(val count: Int) : MyAction
}