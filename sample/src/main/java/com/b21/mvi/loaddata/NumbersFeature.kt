package com.b21.mvi.loaddata

import com.b21.mvicore21.BaseFeature
import io.reactivex.Scheduler

open class NumbersFeature(
    useCase: NumbersUseCase,
    main: Scheduler
) : BaseFeature<NumbersState, NumbersWish, Nothing, (NumbersState) -> NumbersState, NumbersAction>(
    initialState = NumbersState.Loading,
    actor = NumbersRefreshActor(useCase, main),
    bootstrapperAction = NumbersAction.Load,
    wishToAction = { wish, _ ->
        when (wish) {
            NumbersWish.Refresh -> NumbersAction.Load
        }
    }
)

sealed class NumbersEffect : (NumbersState) -> NumbersState {
    data class Data(val data: List<Long>) : NumbersEffect() {
        override fun invoke(state: NumbersState): NumbersState {
            return NumbersState.Data(data)
        }
    }

    object Loading : NumbersEffect() {
        override fun invoke(state: NumbersState): NumbersState {
            return NumbersState.Loading
        }
    }

    object Error : NumbersEffect() {
        override fun invoke(state: NumbersState): NumbersState {
            return NumbersState.Error
        }
    }
}

sealed class NumbersWish {
    object Refresh : NumbersWish()
}

sealed class NumbersAction {
    object Load : NumbersAction()
}

sealed class NumbersState {
    data class Data(val data: List<Long>) : NumbersState()
    object Loading : NumbersState()
    object Error : NumbersState()
}
