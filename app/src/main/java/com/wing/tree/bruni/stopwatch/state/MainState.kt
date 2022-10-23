package com.wing.tree.bruni.stopwatch.state

sealed class MainState {
    abstract val isLapResetEnabled: Boolean

    object Start: MainState() {
        override val isLapResetEnabled: Boolean = true
    }

    object Stop: MainState() {
        override val isLapResetEnabled: Boolean = true
    }

    object Reset: MainState() {
        override val isLapResetEnabled: Boolean = false
    }
}
