import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan

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
        .flatMapMerge { effect -> executor(effect) }
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
