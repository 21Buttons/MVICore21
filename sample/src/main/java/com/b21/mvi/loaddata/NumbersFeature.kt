package com.b21.mvi.loaddata

import com.b21.mvicore21.BaseFeature
import io.reactivex.Scheduler

open class NumbersFeature(
    useCase: NumbersUseCase,
    main: Scheduler
) : BaseFeature<NumbersState, NumbersWish, NumbersNews, NumbersEffect, NumbersAction>(
    initialState = NumbersState.Loading,
    actor = NumbersRefreshActor(useCase, main),
    bootstrapperAction = NumbersAction.Load,
    wishToAction = { wish, _ ->
        when (wish) {
            NumbersWish.Refresh -> NumbersAction.Load
            is NumbersWish.SelectedItem -> NumbersAction.SelectedItem(wish.item)
        }
    },
    newsPublisher = NumbersNewsPublisher()
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

    object Empty : NumbersEffect() {
        override fun invoke(state: NumbersState) = state
    }
}

sealed class NumbersWish {
    data class SelectedItem(val item: String) : NumbersWish()
    object Refresh : NumbersWish()
}

sealed class NumbersAction {
    data class SelectedItem(val item: String) : NumbersAction()
    object Load : NumbersAction()
}

sealed class NumbersState {
    data class Data(val data: List<Long>) : NumbersState()
    object Loading : NumbersState()
    object Error : NumbersState()
}

sealed class NumbersNews {
    data class SelectedItem(val item: String) : NumbersNews()
}
