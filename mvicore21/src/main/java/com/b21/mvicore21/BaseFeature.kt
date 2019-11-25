package com.b21.mvicore21

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.reactivestreams.Subscriber

typealias WishToAction<Wish, State, Action> = (Wish, State) -> Action?
typealias Actor<Action, Effect> = (Action) -> Flowable<out Effect>
typealias PostProcessor<Action, Effect, State> = (Action?, Effect?, State) -> Action?
typealias NewsPublisher<Action, Effect, State, News> = (Action?, Effect?, State) -> News?

open class BaseFeature<State : Any, Wish : Any, News : Any, Effect : (State) -> State, Action : Any>(
    initialState: State,
    bootstrapperAction: Action? = null,
    private val wishToAction: WishToAction<Wish, State, Action>,
    private val actor: Actor<Action, Effect>,
    private val postProcessor: PostProcessor<Action, Effect, State> = { _, _, _ -> null },
    private val newsPublisher: NewsPublisher<Action, Effect, State, News> = { _, _, _ -> null }
) : Feature<State, Wish, News>() {

    private val actions: Subject<Action> = PublishSubject.create()
    private val newsRelay: Subject<News> = PublishSubject.create()
    private val injectStateRelay: Subject<State> = PublishSubject.create()

    private val states: Flowable<State> = actions.toFlowable(BackpressureStrategy.BUFFER)
        .executeBootstrapper(bootstrapperAction)
        .executeActor()
        .reduce(initialState)
        .handleSideEffects()
        .mapToState()
        .distinctUntilChanged()
        .doOnNext { _state = it }
        .replay(1)
        .refCount()

    private var _state: State = initialState

    override var state: State
        get() = _state
        set(value) {
            injectStateRelay.onNext(value)
        }

    override val news: Flowable<News> = newsRelay
        .toFlowable(BackpressureStrategy.BUFFER)
        .replay(1)
        .refCount()

    public override fun subscribeActual(s: Subscriber<in State>) {
        states.subscribe(s)
        news.subscribe()
    }

    override fun accept(wish: Wish) {
        wishToAction(wish, state)?.let { actions.onNext(it) }
    }

    private fun Flowable<Action>.executeBootstrapper(bootstrapperAction: Action?): Flowable<Action> {
        return if (bootstrapperAction != null) {
            this.startWith(just(bootstrapperAction))
        } else {
            this
        }
    }

    private fun Flowable<Action>.executeActor(): Flowable<PreScanData<State, Action, Effect>> {
        return this.flatMap { action ->
            actor(action)
                .map { effect ->
                    PreScanData(action, effect)
                }
        }
    }

    private fun Flowable<PreScanData<State, Action, Effect>>.reduce(initialState: State): Flowable<FeatureData<State, Action, Effect>> {
        return this
            .mergeWith(injectStateRelay
                .toFlowable(BackpressureStrategy.LATEST)
                .map<PreScanData<State, Action, Effect>> { PreScanData(it) })
            .scan(FeatureData.InitialState(initialState)) { previousData: FeatureData<State, Action, Effect>, actionEffect: PreScanData<State, Action, Effect> ->
                FeatureData.WithInfo(
                    actionEffect.reduce(previousData.state),
                    actionEffect.action,
                    actionEffect.effect
                )
            }
    }

    private fun Flowable<FeatureData<State, Action, Effect>>.handleSideEffects(): Flowable<FeatureData<State, Action, Effect>> {
        return this.publish { stream ->
            stream
                .mergeWith(
                    stream
                        .ofTypeInfo<FeatureData.WithInfo<State, Action, Effect>>()
                        .executePostProcessor()
                        .executeNews()
                        .ignoreElements()
                )
        }
    }

    private inline fun <reified T> Flowable<FeatureData<State, Action, Effect>>.ofTypeInfo() =
        this.ofType(T::class.java)

    private fun Flowable<FeatureData.WithInfo<State, Action, Effect>>.executePostProcessor(): Flowable<FeatureData.WithInfo<State, Action, Effect>> {
        return this.doOnNext { (state, action, effect) ->
            val newAction = postProcessor(action, effect, state)
            if (newAction != null) {
                actions.onNext(newAction)
            }
        }
    }

    private fun Flowable<FeatureData.WithInfo<State, Action, Effect>>.executeNews(): Flowable<FeatureData.WithInfo<State, Action, Effect>> {
        return this.doOnNext { (state, action, effect) ->
            val news = newsPublisher(action, effect, state)
            if (news != null) {
                newsRelay.onNext(news)
            }
        }
    }

    private fun Flowable<out FeatureData<State, Action, Effect>>.mapToState(): Flowable<State> {
        return this.map { it.state }
    }
}

private class PreScanData<State : Any, out Action : Any, out Effect : (State) -> State> private constructor(
    val reduce: (State) -> State,
    val action: Action?,
    val effect: Effect?
) {

    constructor(state: State) : this({ state }, null, null)

    constructor(action: Action, effect: Effect) : this(effect, action, effect)
}

private sealed class FeatureData<State : Any, out Action : Any, out Effect : (State) -> State> {
    abstract val state: State

    data class WithInfo<State : Any, Action : Any, Effect : (State) -> State>(
        override val state: State,
        val action: Action?,
        val effect: Effect?
    ) : FeatureData<State, Action, Effect>()

    data class InitialState<State : Any>(
        override val state: State
    ) : FeatureData<State, Nothing, Nothing>()
}
