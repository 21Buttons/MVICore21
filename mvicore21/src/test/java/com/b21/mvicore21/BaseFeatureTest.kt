package com.b21.mvicore21

import io.reactivex.Flowable
import org.junit.Test

class BaseFeatureTest {

    private val feature = BaseFeature(
        initialState = TestState.Counter(0),
        bootstrapperAction = TestAction.Increment,
        actor = TestActor(),
        wishToAction = TestWishToAction(),
        newsPublisher = TestNewsPublisher()
    )

    @Test
    fun testInitialState() {
        feature
            .test()
            .assertValues(
                TestState.Counter(0),
                TestState.Counter(1)
            )

        feature.news
            .test()
            .assertNoValues()
    }

    @Test
    fun testInitialStateNoBootstrap() {
        val featureNoBootstrap = BaseFeature(
            initialState = TestState.Counter(15),
            bootstrapperAction = null,
            actor = TestActor(),
            wishToAction = TestWishToAction(),
            newsPublisher = TestNewsPublisher()
        )
        featureNoBootstrap
            .test()
            .assertValues(TestState.Counter(15))

        featureNoBootstrap.news
            .test()
            .assertNoValues()
    }

    @Test
    fun testIncrement() {
        val testSubscriber = feature.test()
        val testNewsSubscriber = feature.news.test()

        feature.accept(TestWish.Increment)

        testSubscriber.assertValues(
            TestState.Counter(0),
            TestState.Counter(1),
            TestState.Counter(2)
        )

        testNewsSubscriber.assertNoValues()
    }

    @Test
    fun testSetStateIncrement() {
        val testSubscriber = feature.test()
        val testNewsSubscriber = feature.news.test()

        feature.accept(TestWish.Increment)

        feature.state = TestState.Counter(44)

        feature.accept(TestWish.Increment)

        testSubscriber.assertValues(
            TestState.Counter(0),
            TestState.Counter(1),
            TestState.Counter(2),
            TestState.Counter(44),
            TestState.Counter(45)
        )

        testNewsSubscriber.assertNoValues()
    }

    @Test
    fun testNews() {
        val testSubscriber = feature.test()
        val testNewsSubscriber = feature.news.test()

        feature.accept(TestWish.Increment)
        feature.accept(TestWish.Increment)
        feature.accept(TestWish.Increment)

        testSubscriber.assertValues(
            TestState.Counter(0),
            TestState.Counter(1),
            TestState.Counter(2),
            TestState.Counter(3),
            TestState.Counter(4)
        )

        testNewsSubscriber.assertValues(TestNews.Reached3)
    }

    sealed class TestState {
        data class Counter(val count: Int) : TestState()
    }

    sealed class TestAction {
        object Increment : TestAction()
    }

    sealed class TestEffect : (TestState) -> TestState {
        data class NewData(val increment: Int) : TestEffect() {
            override fun invoke(previousState: TestState): TestState {
                return when (previousState) {
                    is TestState.Counter -> previousState.copy(previousState.count + increment)
                }
            }
        }
    }

    sealed class TestWish {
        object Increment : TestWish()
    }

    class TestWishToAction : WishToAction<TestWish, TestState, TestAction> {
        override fun invoke(wish: TestWish, state: TestState): TestAction? {
            return when (state) {
                is TestState.Counter -> TestAction.Increment
            }
        }
    }

    class TestActor : Actor<TestAction, TestEffect> {
        override fun invoke(testAction: TestAction): Flowable<out TestEffect> {
            return when (testAction) {
                is TestAction.Increment -> {
                    Flowable.just(TestEffect.NewData(1))
                }
            }
        }
    }

    sealed class TestNews {
        object Reached3 : TestNews()
    }

    class TestNewsPublisher : NewsPublisher<TestAction, TestEffect, TestState, TestNews> {
        override fun invoke(action: TestAction?, effect: TestEffect?, state: TestState): TestNews? {
            return when (state) {
                is TestState.Counter -> if (state.count == 3) return TestNews.Reached3 else null
            }
        }
    }
}
