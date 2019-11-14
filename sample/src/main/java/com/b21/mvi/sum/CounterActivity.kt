package com.b21.mvi.sum

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.b21.mvi.R
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

class CounterActivity : AppCompatActivity(), CounterView {

    companion object {
        fun getCallingIntent(context: Context): Intent {
            return Intent(context, CounterActivity::class.java)
        }
    }

    private lateinit var resultTextView: TextView
    private lateinit var addButton: Button
    private lateinit var subtractButton: Button

    private lateinit var presenter: CounterPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sum)

        resultTextView = findViewById(R.id.counter_result)
        addButton = findViewById(R.id.add_button)
        subtractButton = findViewById(R.id.subtract_button)

        presenter = CounterPresenter(
            this,
            CounterFeature(null, AndroidSchedulers.mainThread())
        )
        lifecycle.addObserver(presenter)
    }

    override val userIntents: Observable<UserIntent>
        get() = Observable.merge(
            addButton.clicks()
                .map { UserIntent.Sum },
            subtractButton.clicks()
                .map { UserIntent.Subtract }
        )

    override fun render(state: CounterState) {
        when (state) {
            is CounterState.Counter -> resultTextView.text = state.count.toString()
            CounterState.Empty -> resultTextView.text = getString(R.string.empty_counter)
        }
    }
}
