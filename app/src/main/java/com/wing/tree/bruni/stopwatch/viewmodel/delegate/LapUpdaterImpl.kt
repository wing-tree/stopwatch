package com.wing.tree.bruni.stopwatch.viewmodel.delegate

import com.wing.tree.bruni.stopwatch.constant.ONE
import com.wing.tree.bruni.stopwatch.model.Update
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

class LapUpdaterImpl : LapUpdater {
    override val longestLap = AtomicInteger(-ONE)
    override val longestLapTime = AtomicLong(Long.MIN_VALUE)
    override val shortestLap = AtomicInteger(-ONE)
    override val shortestLapTime = AtomicLong(Long.MAX_VALUE)

    override fun clear() {
        longestLap.set(-ONE)
        longestLapTime.set(Long.MIN_VALUE)
        shortestLap.set(-ONE)
        shortestLapTime.set(Long.MAX_VALUE)
    }

    override fun update(lap: Int, lapTime: Long): Update? {
        with(longestLapTime) {
            com.google.android.material.R.attr.behavior_peekHeight
            if (get() < lapTime) {
                set(lapTime)
                longestLap.set(lap)
                shortestLap.compareAndSet(-ONE, lap)

                return Update.Longest
            }
        }

        with(shortestLapTime) {
            if (get() > lapTime) {
                set(lapTime)
                shortestLap.set(lap)
                longestLap.compareAndSet(-ONE, lap)

                return Update.Shortest
            }
        }

        return null
    }
}
