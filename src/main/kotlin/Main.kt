import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.random.Random

suspend fun main() = coroutineScope {
    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    val store = Store<MyEffect, MyAction, MyState>(
        initialState = MyState.empty,
        executor = Executor { effect ->
            when (effect) {
                MyEffect.Load -> init()
                is MyEffect.RandomIncrement -> incrementRandomly(effect.min, effect.max)
            }
        },
        reducer = Reducer { state, action ->
            when (action) {
                is MyAction.Set -> MyState(0, value = action.initialValue, 0)
                is MyAction.Increment -> state.incrementedBy(action.count)
                MyAction.Tick -> state.ticked()
            }
        },
        onError = { state, throwable ->
            throwable.printStackTrace()
            state.toError(errorCode = 1_000)
        }
    )

    scope.launch {
        debug("Collecting...")
        store.state.collect { state ->
            debug("new state: $state")
        }
    }

    store.send(MyEffect.Load)

    delay(100)
    store.send(MyEffect.RandomIncrement(10, 11))
    store.send(MyEffect.RandomIncrement(100, 101))

    delay(1500)
    store.send(MyEffect.RandomIncrement(1000, 1001))

    delay(1000)
}

fun init(): Flow<MyAction> = flow {
    val initialValue = Random.nextInt(10_000, 50_000)
    emit(MyAction.Set(initialValue))

    while (true) {
        delay(500)
        emit(MyAction.Tick)
    }
}

fun incrementRandomly(min: Int, max: Int): Flow<MyAction> = flow {
    emit(MyAction.Increment(Random.nextInt(min, max)))
}

fun debug(message: String) {
    println("${java.util.Date()} | $message")
//    println("${java.util.Date()} [${Thread.currentThread().name}] $message")
}