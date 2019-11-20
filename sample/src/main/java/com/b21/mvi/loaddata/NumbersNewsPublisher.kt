package com.b21.mvi.loaddata

import com.b21.mvicore21.NewsPublisher

class NumbersNewsPublisher : NewsPublisher<NumbersAction, NumbersEffect, NumbersState, NumbersNews> {
    override fun invoke(action: NumbersAction?, effect: NumbersEffect?, state: NumbersState): NumbersNews? {
        return when (action) {
            is NumbersAction.SelectedItem -> NumbersNews.SelectedItem(action.item)
            else -> null
        }
    }
}
