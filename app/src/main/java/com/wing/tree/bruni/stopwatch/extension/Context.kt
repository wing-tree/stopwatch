package com.wing.tree.bruni.stopwatch.extension

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.TypedValue
import androidx.annotation.ColorInt
import com.wing.tree.bruni.stopwatch.R
import com.wing.tree.bruni.stopwatch.constant.LABEL_TEXT_PLAIN

internal fun Context.copyToClipboard(text: CharSequence) {
    val clipboardManager = getSystemService(ClipboardManager::class.java)
    val clipData = ClipData.newPlainText(LABEL_TEXT_PLAIN, text)

    clipboardManager.setPrimaryClip(clipData)
}

internal val Context.disabledAlpha: Float get() = with(TypedValue()) {
    resources.getValue(
        R.dimen.disabled_alpha,
        this,
        true
    )

    float
}

internal val Context.colorOnPrimary: Int @ColorInt get() = with(TypedValue()) {
    theme.resolveAttribute(
        com.google.android.material.R.attr.colorOnPrimary,
        this,
        true
    )

    data
}

internal val Context.colorPrimary: Int @ColorInt get() = with(TypedValue()) {
    theme.resolveAttribute(
        com.google.android.material.R.attr.colorPrimary,
        this,
        true
    )

    data
}

internal val Context.colorOnTertiary: Int @ColorInt get() = with(TypedValue()) {
    theme.resolveAttribute(
        com.google.android.material.R.attr.colorOnTertiary,
        this,
        true
    )

    data
}

internal val Context.colorTertiary: Int @ColorInt get() = with(TypedValue()) {
    theme.resolveAttribute(
        com.google.android.material.R.attr.colorTertiary,
        this,
        true
    )

    data
}
