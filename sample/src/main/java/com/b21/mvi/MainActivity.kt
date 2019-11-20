package com.b21.mvi

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.b21.mvi.loaddata.NumbersActivity
import com.b21.mvi.sum.CounterActivity

class MainActivity : AppCompatActivity() {

    private lateinit var counterButton: Button
    private lateinit var numbersButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        counterButton = findViewById(R.id.button_counter)
        counterButton.setOnClickListener {
            startActivity(CounterActivity.getCallingIntent(this))
        }

        numbersButton = findViewById(R.id.button_numbers)
        numbersButton.setOnClickListener {
            startActivity(NumbersActivity.getCallingIntent(this))
        }
    }
}
