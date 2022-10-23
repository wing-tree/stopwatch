package com.wing.tree.bruni.stopwatch.model

sealed interface Update {
    object Longest : Update
    object Shortest : Update
}
