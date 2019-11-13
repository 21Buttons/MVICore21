package com.b21.mvicore21

import io.reactivex.Flowable
import io.reactivex.functions.Consumer

abstract class Feature<State : Any, Wish : Any, News : Any> : Flowable<State>(), Consumer<Wish> {
    abstract var state: State
    abstract val news: Flowable<News>
}
