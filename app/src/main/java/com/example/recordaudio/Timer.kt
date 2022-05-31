package com.example.recordaudio

import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import java.time.Duration


@RequiresApi(Build.VERSION_CODES.S)
class Timer(listener: OnTimerTickListener) {

    interface OnTimerTickListener {
        fun onTimerTick(duration: Duration)
    }

    private var handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    private var duration: Duration = Duration.ZERO
    private var delay : Duration = Duration.ofMillis(100L)


    init {
        runnable = Runnable {
            duration += delay
            handler.postDelayed(runnable, delay.toMillis())
            listener.onTimerTick(duration = duration)
        }
    }


    fun start() {
        handler.postDelayed(runnable, delay.toMillis())
    }

     fun pause() {
        handler.removeCallbacks(runnable)
    }

     fun stop() {
        handler.removeCallbacks(runnable)
        duration = Duration.ZERO
    }

}