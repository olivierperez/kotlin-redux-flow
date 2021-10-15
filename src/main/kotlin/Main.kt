import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random

suspend fun main() = coroutineScope {
    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    val store = Store<MyEffect, MyAction, MyState>(
        initialState = MyState(-1),
        executor = Executor { effect ->
            when (effect) {
                MyEffect.Load -> flowOf(MyAction.Init)
                is MyEffect.RandomIncrement -> flow {
                    emit(MyAction.Increment(Random.nextInt(effect.min, effect.max)))
                }
            }
        },
        reducer = Reducer { state, action ->
            when (action) {
                MyAction.Init -> MyState(0)
                is MyAction.Increment -> state.copy(value = state.value + action.count)
            }
        }
    )

    scope.launch {
        debug("Collecting...")
        store.state.collect { state ->
            debug("new state: $state")
        }
    }

    store.send(MyEffect.Load)
    store.send(MyEffect.RandomIncrement(0, 9))
    store.send(MyEffect.RandomIncrement(10,99))
    store.send(MyEffect.RandomIncrement(100, 999))

    delay(2000)
}

fun debug(message: String) {
    println(message)
    //println("[${Thread.currentThread().name}] $message")
}