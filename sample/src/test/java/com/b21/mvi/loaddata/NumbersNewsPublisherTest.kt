package com.b21.mvi.loaddata

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NumbersNewsPublisherTest {
    private val newsPublisher = NumbersNewsPublisher()

    @Test
    fun testWithSelectedItem() {
        val news = newsPublisher.invoke(NumbersAction.SelectedItem("a"), NumbersEffect.Data(listOf()), NumbersState.Loading)
        assertEquals(news, NumbersNews.SelectedItem("a"))
    }

    @Test
    fun testWithLoad() {
        val news = newsPublisher.invoke(NumbersAction.Load, NumbersEffect.Data(listOf()), NumbersState.Loading)
        assertNull(news)
    }
}
