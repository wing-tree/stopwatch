package com.wing.tree.bruni.stopwatch.view

import android.content.res.ColorStateList
import android.os.Bundle
import android.transition.TransitionManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.isInvisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.wing.tree.bruni.stopwatch.BuildConfig
import com.wing.tree.bruni.stopwatch.R
import com.wing.tree.bruni.stopwatch.constant.ALPHA_MAXIMUM
import com.wing.tree.bruni.stopwatch.constant.NEWLINE
import com.wing.tree.bruni.stopwatch.constant.PATTERN
import com.wing.tree.bruni.stopwatch.databinding.ActivityMainBinding
import com.wing.tree.bruni.stopwatch.extension.*
import com.wing.tree.bruni.stopwatch.state.MainState
import com.wing.tree.bruni.stopwatch.view.adapter.LapItemAnimator
import com.wing.tree.bruni.stopwatch.view.adapter.LapListAdapter
import com.wing.tree.bruni.stopwatch.viewmodel.MainViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val lapListAdapter by lazy { LapListAdapter(this) }

    private val simpleDateFormat = SimpleDateFormat(PATTERN, Locale.getDefault())
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        setSupportActionBar(binding.materialToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.bind()
        observe()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val isEnabled = viewModel.isContentCopyEnabled.value

        val alpha = if (isEnabled) {
            ALPHA_MAXIMUM
        } else {
            disabledAlpha.times(ALPHA_MAXIMUM).int
        }

        menu?.findItem(R.id.content_copy)?.let {
            it.isEnabled = isEnabled
            it.icon?.alpha = alpha
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.content_copy -> {
                val value = viewModel.laps.value
                val text = value.joinToString(NEWLINE) { it.toText(simpleDateFormat) }

                copyToClipboard(text)
                Toast.makeText(
                    this,
                    R.string.copied_to_clipboard,
                    Toast.LENGTH_SHORT
                ).show()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @OptIn(FlowPreview::class)
    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.isContentCopyEnabled.collectLatest {
                    invalidateOptionsMenu()
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.lapTime.sample(PERIOD_MILLIS).collectLatest {
                    binding.textViewLapTime.text = simpleDateFormat.format(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.laps.collectLatest {
                    val recyclerView = binding.recyclerViewLaps
                    val lastIndex = it.lastIndex

                    binding.textViewLapTime.isInvisible = it.isEmpty()
                    lapListAdapter.submitList(it) {
                        if (lastIndex > -1) {
                            recyclerView.scrollToPosition(lastIndex)
                        } else {
                            lapListAdapter.reset()
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.overallTime.sample(PERIOD_MILLIS).collectLatest {
                    binding.textViewOverallTime.text = simpleDateFormat.format(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collectLatest {
                    @ColorInt
                    val backgroundTint: Int
                    val lapReset: String
                    val startStopResume: String

                    @ColorInt
                    val textColor: Int

                    when(it) {
                        MainState.Start -> {
                            backgroundTint = colorTertiary
                            lapReset = getString(R.string.record_lap)
                            startStopResume = getString(R.string.stop)
                            textColor = colorOnTertiary
                        }
                        MainState.Stop -> {
                            backgroundTint = colorPrimary
                            lapReset = getString(R.string.reset)
                            startStopResume = getString(R.string.resume)
                            textColor = colorOnPrimary
                        }
                        MainState.Reset -> {
                            backgroundTint = colorPrimary
                            lapReset = getString(R.string.record_lap)
                            startStopResume = getString(R.string.start)
                            textColor = colorOnPrimary
                        }
                    }

                    invalidateOptionsMenu()

                    with(binding.materialButtonLapReset) {
                        isEnabled = it.isLapResetEnabled
                        text = lapReset
                    }

                    with(binding.materialButtonStartStopResume) {
                        backgroundTintList = ColorStateList.valueOf(backgroundTint)
                        text = startStopResume
                        setTextColor(textColor)
                    }
                }
            }
        }
    }

    private fun ActivityMainBinding.bind() {
        AdView(this@MainActivity).apply {
            val adRequest = AdRequest.Builder().build()

            @StringRes
            val resId = if (BuildConfig.DEBUG) {
                R.string.sample_banner_ad_unit_id
            } else {
                R.string.banner_ad_unit_id
            }

            adUnitId = getString(resId)
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    frameLayoutAd.removeAllViews()
                    TransitionManager.beginDelayedTransition(frameLayoutAd)
                    frameLayoutAd.addView(this@apply)
                }
            }

            setAdSize(AdSize.BANNER)

            loadAd(adRequest)
        }

        recyclerViewLaps.apply {
            adapter = lapListAdapter
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                true
            ).apply {
                initialPrefetchItemCount = INITIAL_PREFETCH_ITEM_COUNT
                stackFromEnd = true
            }

            itemAnimator = LapItemAnimator()

            setHasFixedSize(true)
        }

        materialButtonLapReset.setOnClickListener {
            when(viewModel.state.value) {
                is MainState.Start -> viewModel.lap()
                is MainState.Stop -> viewModel.reset()
                is MainState.Reset -> Unit
            }
        }

        materialButtonStartStopResume.setOnClickListener {
            when(viewModel.state.value) {
                is MainState.Start -> viewModel.stop()
                is MainState.Stop -> viewModel.start()
                is MainState.Reset -> viewModel.start()
            }
        }
    }

    companion object {
        private const val INITIAL_PREFETCH_ITEM_COUNT = 10
        private const val PERIOD_MILLIS = 37L
    }
}
