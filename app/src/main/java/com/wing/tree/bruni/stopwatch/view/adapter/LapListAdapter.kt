package com.wing.tree.bruni.stopwatch.view.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wing.tree.bruni.stopwatch.R
import com.wing.tree.bruni.stopwatch.constant.DURATION_FADE_IN_OUT
import com.wing.tree.bruni.stopwatch.constant.ONE
import com.wing.tree.bruni.stopwatch.constant.PATTERN
import com.wing.tree.bruni.stopwatch.databinding.LapListItemBinding
import com.wing.tree.bruni.stopwatch.extension.fadeIn
import com.wing.tree.bruni.stopwatch.extension.fadeOut
import com.wing.tree.bruni.stopwatch.extension.gone
import com.wing.tree.bruni.stopwatch.extension.string
import com.wing.tree.bruni.stopwatch.model.Lap
import com.wing.tree.bruni.stopwatch.model.Update
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class LapListAdapter(context: Context) :
    ListAdapter<Lap, LapListAdapter.ViewHolder>(DiffCallback())
{
    private val lapColor = context.getColor(R.color.tertiary_text)
    private val longestLap = AtomicInteger(-ONE)
    private val longestLapColor = context.getColor(R.color.red_400)
    private val shortestLap = AtomicInteger(-ONE)
    private val shortestLapColor = context.getColor(R.color.blue_400)
    private val simpleDateFormat = SimpleDateFormat(PATTERN, Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LapListItemBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun reset() {
        longestLap.set(-ONE)
        shortestLap.set(-ONE)
    }

    inner class ViewHolder(private val binding: LapListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Lap) = with(binding) {
            textViewLap.text = item.lap.string

            when (item.update) {
                is Update.Longest -> {
                    with(longestLap) {
                        if (get() < item.lap) {
                            imageViewArrowDownward.gone()
                            imageViewArrowUpward.fadeInOut(DURATION_FADE_IN_OUT)
                            set(item.lap)
                        }
                    }

                    textViewLap.setTextColor(longestLapColor)
                }
                is Update.Shortest -> {
                    with(shortestLap) {
                        if (get() < item.lap) {
                            imageViewArrowUpward.gone()
                            imageViewArrowDownward.fadeInOut(DURATION_FADE_IN_OUT)
                            set(item.lap)
                        }
                    }

                    textViewLap.setTextColor(shortestLapColor)
                }
                else -> {
                    imageViewArrowUpward.isVisible = false
                    imageViewArrowDownward.isVisible = false
                    textViewLap.setTextColor(lapColor)
                }
            }

            textViewLapTime.text = simpleDateFormat.format(item.lapTime)
            textViewOverallTime.text = simpleDateFormat.format(item.overallTime)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Lap>() {
        override fun areItemsTheSame(oldItem: Lap, newItem: Lap): Boolean {
            return oldItem.lap == newItem.lap
        }

        override fun areContentsTheSame(oldItem: Lap, newItem: Lap): Boolean {
            return oldItem == newItem
        }
    }

    private fun View.fadeInOut(duration: Long) {
        fadeIn(duration) {
            fadeOut(duration)
        }
    }
}
