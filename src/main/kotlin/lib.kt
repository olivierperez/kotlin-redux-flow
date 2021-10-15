import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class Reducer<State, Action>(
    private val reduce: (State, Action) -> State
) {
    operator fun invoke(state: State, action: Action): State {
        return reduce(state, action)
    }
}

class Executor<Effect, Action>(
    private val execute: (Effect) -> Flow<Action>
) {
    operator fun invoke(effect: Effect): Flow<Action> {
        return execute(effect)
    }
}

class Store<Effect, Action, State>(
    initialState: State,
    private val executor: Executor<Effect, Action>,
    private val reducer: Reducer<State, Action>
) {

    private val effects = Channel<Effect>()

    val state: Flow<State> = effects
        .consumeAsFlow()
        .transform { effect -> emitAll(executor(effect)) }
        .onEach {
            //debug("DEBUG || on action")
        }
        .scan(initialState) { state, action -> reducer(state, action) }
        .onEach {
            //debug("DEBUG || on state")
        }
        .catch { throwable ->
            debug("on error")
            throwable.printStackTrace()
            // TODO Handle errors
            emit(initialState)
        }

    suspend fun send(effect: Effect) {
        //debug("Sending effect: $effect")
        effects.send(effect)
    }
}
