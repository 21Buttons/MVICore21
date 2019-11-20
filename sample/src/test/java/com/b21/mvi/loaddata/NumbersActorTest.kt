package com.b21.mvi.loaddata

import com.b21.mvi.DataResponse
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Test

class NumbersActorTest {

    private val useCase: NumbersUseCase = mock {
        on { getNumbers() } doReturn Flowable.just(DataResponse(listOf(1L, 2L)))
    }

    private val actor = NumbersActor(useCase, Schedulers.trampoline())

    @After
    fun tearDown() {
        verifyNoMoreInteractions(useCase)
    }

    @Test
    fun testGetNumbers() {
        actor.invoke(NumbersAction.Load)
            .test()
            .assertValues(
                NumbersEffect.Loading,
                NumbersEffect.Data(listOf(1L, 2L))
            )
        verify(useCase).getNumbers()
    }

    @Test
    fun testGetNumbersError() {
        whenever(useCase.getNumbers()).thenReturn(Flowable.just(DataResponse(null)))
        actor.invoke(NumbersAction.Load)
            .test()
            .assertValues(
                NumbersEffect.Loading,
                NumbersEffect.Error
            )
        verify(useCase).getNumbers()
    }

    @Test
    fun testSelectItem() {
        actor.invoke(NumbersAction.SelectedItem("3"))
            .test()
            .assertValue(NumbersEffect.Empty)
    }
}
