package com.b21.mvi.loaddata

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.b21.mvi.R
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

class NumbersActivity : AppCompatActivity(), NumbersView {

    companion object {
        fun getCallingIntent(context: Context): Intent {
            return Intent(context, NumbersActivity::class.java)
        }
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView
    private lateinit var refreshButton: Button

    private lateinit var presenter: NumbersPresenter

    private val relay: Relay<UserIntent> = PublishRelay.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_numbers)

        recyclerView = findViewById(R.id.rv_numbers)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        progressBar = findViewById(R.id.progress_bar)
        errorText = findViewById(R.id.error_text)
        refreshButton = findViewById(R.id.button_refresh)
        refreshButton.setOnClickListener { relay.accept(UserIntent.Refresh) }

        presenter = NumbersPresenter(this, NumbersFeature(NumbersUseCase(), AndroidSchedulers.mainThread()))

        lifecycle.addObserver(presenter)
    }

    override val userIntents: Observable<UserIntent>
        get() = relay

    override fun render(state: NumbersState) {
        when (state) {
            is NumbersState.Data -> {
                setVisibilities(listOf(progressBar, errorText), listOf(recyclerView))
                val adapter = recyclerView.adapter as? NumbersAdapter
                        ?: NumbersAdapter { relay.accept(UserIntent.SelectedItem(it)) }
                adapter.numbersList = state.data
                if (recyclerView.adapter == null) recyclerView.adapter = adapter
            }
            NumbersState.Loading -> setVisibilities(listOf(recyclerView, errorText), listOf(progressBar))
            NumbersState.Error -> setVisibilities(listOf(recyclerView, progressBar), listOf(errorText))
        }
    }

    override fun showSelectedItem(item: String) {
        Toast.makeText(this, "Selected item $item", Toast.LENGTH_SHORT).show()
    }

    private fun setVisibilities(goneViews: List<View>, visibleViews: List<View>) {
        goneViews.forEach { it.visibility = View.GONE }
        visibleViews.forEach { it.visibility = View.VISIBLE }
    }
}
