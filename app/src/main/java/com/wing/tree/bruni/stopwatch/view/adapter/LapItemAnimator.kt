package com.wing.tree.bruni.stopwatch.view.adapter

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorInflater
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.wing.tree.bruni.stopwatch.R
import com.wing.tree.bruni.stopwatch.constant.DURATION_MOVE
import com.wing.tree.bruni.stopwatch.constant.ONE
import com.wing.tree.bruni.stopwatch.constant.ZERO
import com.wing.tree.bruni.stopwatch.extension.float

class LapItemAnimator : DefaultItemAnimator() {
    private val linearInterpolator = LinearInterpolator()

    init {
        moveDuration = DURATION_MOVE
        supportsChangeAnimations = false
    }

    override fun animateAdd(holder: ViewHolder?): Boolean {
        val itemView = holder?.itemView ?: return true
        val context = itemView.context
        val animator = AnimatorInflater.loadAnimator(context, R.animator.fade_in).also {
            it.addListener(LapAnimatorListener(holder))
            it.interpolator = linearInterpolator
            it.setTarget(itemView)
        }

        itemView.alpha = ZERO.float

        animator.start()

        return true
    }

    inner class LapAnimatorListener(private val holder: ViewHolder) : AnimatorListener {
        private val itemView = holder.itemView

        override fun onAnimationStart(animation: Animator) {
            itemView.alpha = ZERO.float
            dispatchAnimationStarted(holder)
        }

        override fun onAnimationEnd(animation: Animator) {
            dispatchAnimationFinished(holder)
            itemView.alpha = ONE.float
        }

        override fun onAnimationCancel(animation: Animator) = Unit
        override fun onAnimationRepeat(animation: Animator) = Unit
    }
}
