package com.wing.tree.bruni.stopwatch.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wing.tree.bruni.stopwatch.Stopwatch
import com.wing.tree.bruni.stopwatch.constant.ONE
import com.wing.tree.bruni.stopwatch.constant.THREE
import com.wing.tree.bruni.stopwatch.constant.TWO
import com.wing.tree.bruni.stopwatch.constant.ZERO
import com.wing.tree.bruni.stopwatch.model.Lap
import com.wing.tree.bruni.stopwatch.model.Update
import com.wing.tree.bruni.stopwatch.state.MainState
import com.wing.tree.bruni.stopwatch.viewmodel.delegate.LapUpdater
import com.wing.tree.bruni.stopwatch.viewmodel.delegate.LapUpdaterImpl
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application), LapUpdater by LapUpdaterImpl() {
    private val _lapTime = MutableStateFlow(ZERO)
    val lapTime: StateFlow<Long> get() = _lapTime

    private val _laps = MutableStateFlow<List<Lap>>(emptyList())
    val laps: StateFlow<List<Lap>> get() = _laps

    val isContentCopyEnabled: StateFlow<Boolean> = laps
        .map { it.isNotEmpty() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    private val _state = MutableStateFlow<MainState>(MainState.Reset)
    val state: StateFlow<MainState> get() = _state

    private val _overallTime = MutableStateFlow(ZERO)
    val overallTime: StateFlow<Long> get() = _overallTime

    private val stopwatch = Stopwatch(
        DELAY,
        PERIOD,
        object : Stopwatch.Callback {
            override fun onStart() {
                _state.value = MainState.Start
            }

            override fun onStop() {
                _state.value = MainState.Stop
            }

            override fun onReset() {
                clear()
                _lapTime.value = ZERO
                _laps.value = emptyList()
                _overallTime.value = ZERO
                _state.value = MainState.Reset
            }

            override fun onTick(period: Long) {
                updateTime(period)
            }
        }
    )

    private fun updateTime(period: Long) {
        viewModelScope.launch {
            _lapTime.value = lapTime.value.plus(period)
            _overallTime.value = overallTime.value.plus(period)
        }
    }

    fun lap() {
        val lap = laps.count().inc()
        val lapTime = lapTime.value
        val laps = laps.value.toMutableList()
        val overallTime = overallTime.value
        val update = update(lap, lapTime)

        _laps.value = laps.apply {
            update?.let { update(it) }

            with(
                Lap(
                    lap = lap,
                    lapTime = lapTime,
                    overallTime = overallTime,
                    update = if (laps.count() > TWO) {
                        update
                    } else {
                        null
                    }
                )
            ) {
                add(this)

                if (laps.count() == THREE) {
                    replaceAll {
                        when(it.lap) {
                            longestLap.get() -> it.copy(update = Update.Longest)
                            shortestLap.get() -> it.copy(update = Update.Shortest)
                            else -> it.copy(update = null)
                        }
                    }
                }
            }
        }

        _lapTime.value = ZERO
    }

    fun reset() {
        stopwatch.reset()
    }

    fun start() {
        stopwatch.start()
    }

    fun stop() {
        stopwatch.stop()
    }

    @Suppress("unused")
    private fun MutableList<Lap>.replaceAt(lap: Int, apply: Lap.() -> Lap) {
        find { it.lap == lap }?.let {
            val index = indexOf(it)

            if (index > -ONE) {
                set(index, apply(it))
            }
        }
    }

    private fun MutableList<Lap>.update(update: Update) {
        find { it.update == update }?.let {
            val index = indexOf(it)

            if (index > -ONE) {
                set(index, it.copy(update = null))
            }
        }
    }

    private fun StateFlow<List<*>>.count() = value.count()

    companion object {
        private const val DELAY = ZERO
        private const val PERIOD = 10L
    }
}
