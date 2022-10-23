package com.wing.tree.bruni.stopwatch.viewmodel.delegate

import com.wing.tree.bruni.stopwatch.model.Update
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

interface LapUpdater {
    val longestLap: AtomicInteger
    val longestLapTime: AtomicLong
    val shortestLap: AtomicInteger
    val shortestLapTime: AtomicLong

    fun clear()
    fun update(lap: Int, lapTime: Long): Update?
}
