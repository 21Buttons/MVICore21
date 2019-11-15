package com.b21.mvi.loaddata

import com.b21.mvi.DataResponse
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit
import kotlin.random.Random

open class NumbersUseCase {
    open fun getNumbers(): Flowable<DataResponse<List<Long>>> {
        val data = List(20) { Random.nextLong(0, 100) }
        return Flowable.just(DataResponse(data))
            .delay(500, TimeUnit.MILLISECONDS)
    }
}
