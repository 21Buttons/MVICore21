package com.b21.mvi.sum

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.rxjava3.disposables.CompositeDisposable

class CounterPresenter(
    private val view: CounterView,
    private val feature: CounterFeature
) : DefaultLifecycleObserver {

    private val disposable = CompositeDisposable()

    override fun onCreate(owner: LifecycleOwner) {
        disposable.add(
            feature
                .subscribe(
                    view::render,
                    { throw RuntimeException(it) },
                    { throw RuntimeException() })
        )

        disposable.add(view.userIntents
            .subscribe(
                {
                    when (it) {
                        UserIntent.Sum -> feature.accept(CounterWish.SumClick)
                        UserIntent.Subtract -> feature.accept(CounterWish.SubtractClick)
                    }
                },
                { throw RuntimeException(it) }
            )
        )
    }

    override fun onDestroy(owner: LifecycleOwner) {
        disposable.clear()
    }
}
