package com.b21.mvi.loaddata

import com.b21.mvi.DataResponse
import com.b21.mvi.sum.assertValuesAndClear
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Test

class NumbersFeatureTest {
    private val useCase: NumbersUseCase = mock {
        on { getNumbers() } doReturn Flowable.just(DataResponse(listOf(1L, 2L, 4L)))
    }

    private val feature = NumbersFeature(useCase, Schedulers.trampoline())

    @After
    fun tearDown() {
        verifyNoMoreInteractions(useCase)
    }

    @Test
    fun testInitialState() {
        feature.test()
            .assertValuesAndClear(
                NumbersState.Loading,
                NumbersState.Data(listOf(1, 2, 4))
            )
        verify(useCase).getNumbers()
    }

    @Test
    fun testRefresh() {
        val testSubscriber = feature.test()
        testSubscriber
            .assertValuesAndClear(
                NumbersState.Loading,
                NumbersState.Data(listOf(1, 2, 4))
            )
        feature.accept(NumbersWish.Refresh)

        testSubscriber
            .assertValuesAndClear(
                NumbersState.Loading,
                NumbersState.Data(listOf(1, 2, 4))
            )

        verify(useCase, times(2)).getNumbers()
    }

    @Test
    fun testError() {
        whenever(useCase.getNumbers()).thenReturn(Flowable.just(DataResponse(null)))
        val feature = NumbersFeature(useCase, Schedulers.trampoline())
        val testSubscriber = feature.test()
        testSubscriber
            .assertValuesAndClear(
                NumbersState.Loading,
                NumbersState.Error
            )
        verify(useCase).getNumbers()
    }

    @Test
    fun testSelectItem() {
        feature.test()
        val testNewsSubscriber = feature.news.test()

        feature.accept(NumbersWish.SelectedItem("2"))

        testNewsSubscriber
            .assertValue(NumbersNews.SelectedItem("2"))
        verify(useCase).getNumbers()
    }
}
