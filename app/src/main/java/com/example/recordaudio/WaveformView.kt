package com.example.recordaudio

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class WaveformView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var paint = Paint()
    private var amplitudes = ArrayList<Float>()
    private var spikes = ArrayList<RectF>()

    private var radius = 6f
    private var width = 9f

    private var d = 6f //distance from a spike and another one
    private var screenWidth = 0f
    private var screenHigh = 400f //same as xml

    private var maxSpikes = 0

    init {
        screenWidth = resources.displayMetrics.widthPixels.toFloat()

        paint.color = Color.rgb(244, 81, 30)

        maxSpikes = (screenWidth / (width + d)).toInt()
    }


    fun addAmplitude(amp: Float) {
        val norm = min((amp / 7), screenHigh)
        amplitudes.add(norm)
        spikes.clear()
        val amps = amplitudes.takeLast(maxSpikes)
        for (i in amps.indices) {

            val left = screenWidth - (i * (width + d))
            val top = screenHigh / 2 - amps[i] / 2
            val right = left + width
            val bottom = top + amps[i]

            spikes.add(RectF(left, top, right, bottom))
        }

        invalidate()
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        spikes.forEach {
            canvas?.drawRoundRect(it, radius, radius, paint)
        }
    }


}