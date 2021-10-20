import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flatMapMerge
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
    private val reducer: Reducer<State, Action>,
    onError: suspend (State, Throwable) -> State
) {

    private val effects = Channel<Effect>()

    val state: Flow<State> = effects
        .consumeAsFlow()
        .flatMapMerge { effect -> executor(effect) }
        .scan(initialState) { state, action ->
            try {
                reducer(state, action)
            } catch (e: Exception) {
                onError(state, e)
            }
        }

    suspend fun send(effect: Effect) {
        effects.send(effect)
    }
}
