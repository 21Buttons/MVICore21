package com.b21.mvi.loaddata

import com.b21.mvicore21.Actor
import io.reactivex.Flowable
import io.reactivex.Scheduler

class NumbersActor(
    private val useCase: NumbersUseCase,
    private val main: Scheduler
) : Actor<NumbersAction, NumbersEffect> {
    override fun invoke(action: NumbersAction): Flowable<NumbersEffect> {
        return when (action) {
            NumbersAction.Load -> {
                useCase.getNumbers()
                    .map {
                        if (it.data != null) {
                            NumbersEffect.Data(it.data)
                        } else {
                            NumbersEffect.Error
                        }
                    }
                    .startWith(NumbersEffect.Loading)
            }
            is NumbersAction.SelectedItem -> Flowable.just<NumbersEffect>(NumbersEffect.Empty)
        }
            .observeOn(main)
    }
}
