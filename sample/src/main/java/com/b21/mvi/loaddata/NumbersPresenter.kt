package com.b21.mvi.loaddata

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.disposables.CompositeDisposable

class NumbersPresenter(
    private val view: NumbersView,
    private val feature: NumbersFeature
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

        disposable.add(
            feature.news
                .subscribe(
                    { news ->
                        when (news) {
                            is NumbersNews.SelectedItem -> view.showSelectedItem(news.item)
                        }
                    },
                    { throw RuntimeException(it) },
                    { throw RuntimeException() })
        )

        disposable.add(view.userIntents
            .subscribe(
                {
                    when (it) {
                        UserIntent.Refresh -> feature.accept(NumbersWish.Refresh)
                        is UserIntent.SelectedItem -> feature.accept(NumbersWish.SelectedItem(it.item))
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
