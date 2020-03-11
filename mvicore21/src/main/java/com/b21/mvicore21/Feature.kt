package com.b21.mvicore21

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.functions.Consumer

abstract class Feature<State : Any, Wish : Any, News : Any> : Flowable<State>(), Consumer<Wish> {
    abstract var state: State
    abstract val news: Flowable<News>
}
