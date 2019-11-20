package com.b21.mvi.loaddata

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.b21.mvi.R

class NumbersAdapter(private val onClick: (String) -> Unit) : RecyclerView.Adapter<NumbersAdapter.NumbersViewHolder>() {

    var numbersList: List<Long> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumbersViewHolder {
        return NumbersViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_numbers, parent, false))
    }

    override fun getItemCount() = numbersList.size

    override fun onBindViewHolder(holder: NumbersViewHolder, position: Int) {
        holder.bind(numbersList[position], onClick)
    }

    inner class NumbersViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val numberTextView: TextView = itemView.findViewById(R.id.text_view)

        fun bind(number: Long, onClick: (String) -> Unit) {
            val text = number.toString()
            numberTextView.text = text
            numberTextView.setOnClickListener { onClick(text) }
        }
    }
}
