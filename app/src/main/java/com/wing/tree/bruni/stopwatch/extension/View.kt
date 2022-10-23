@file:Suppress("unused")

package com.wing.tree.bruni.stopwatch.extension

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator

internal fun View.visible() {
    visibility = View.VISIBLE
}

internal fun View.invisible() {
    visibility = View.INVISIBLE
}

internal fun View.gone() {
    visibility = View.GONE
}

internal fun View.fadeIn(
    duration: Long,
    onAnimationEnd: (() -> Unit)? = null
): ViewPropertyAnimator {
    this.apply {
        alpha = 0.0F
        visibility = View.VISIBLE

        val listener = object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                alpha = 1.0F
                visibility = View.VISIBLE

                onAnimationEnd?.invoke()
                super.onAnimationEnd(animation)
            }
        }

        return@fadeIn animate()
            .alpha(1.0F)
            .setDuration(duration)
            .setInterpolator(DecelerateInterpolator())
            .setListener(listener).withLayer()
    }
}

internal fun View.fadeOut(
    duration: Long,
    onAnimationEnd: (() -> Unit)? = null
): ViewPropertyAnimator {
    this.apply {
        alpha = 1.0F

        val listener = object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                alpha = 0.0F
                visibility = View.GONE

                onAnimationEnd?.invoke()
                super.onAnimationEnd(animation)
            }
        }

        return@fadeOut animate()
            .alpha(0.0F)
            .setDuration(duration)
            .setInterpolator(AccelerateInterpolator())
            .setListener(listener)
            .withLayer()
    }
}