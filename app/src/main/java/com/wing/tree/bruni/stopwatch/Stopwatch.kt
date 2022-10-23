package com.wing.tree.bruni.stopwatch

import java.util.*
import kotlin.concurrent.timerTask

internal class Stopwatch(
    private val delay: Long,
    private val period: Long,
    private val callback: Callback
) {
    private var state: State = State.Stop
    private var timer: Timer? = null

    fun start() {
        if (state == State.Start) {
            return
        } else {
            state = State.Start

            timer?.let {
                it.cancel()
                it.purge()
            }

            Timer().also {
                timer = it


                val timerTask = timerTask {
                    callback.onTick(period)
                }

                timer?.scheduleAtFixedRate(timerTask, delay, period)
            }

            callback.onStart()
        }
    }

    fun stop() {
        if (state == State.Stop) {
            return
        } else {
            state = State.Stop

            timer?.let {
                it.cancel()
                it.purge()
            }

            callback.onStop()
        }
    }

    fun reset() {
        if (state == State.Reset) {
            return
        } else {
            state = State.Reset

            timer?.let {
                it.cancel()
                it.purge()
            }

            callback.onReset()
        }
    }

    interface Callback {
        fun onStart()
        fun onStop()
        fun onReset()
        fun onTick(period: Long)
    }

    enum class State {
        Start,
        Stop,
        Reset
    }
}
