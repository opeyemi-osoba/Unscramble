package com.example.unscramble.ui


import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.schedule

class GameTimer(private val durationInSeconds: Long, private val intervalInMillis: Long = 1000) {

    private var timeLeftInMillis: Long = durationInSeconds * 1000
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    private var onTick: ((Long) -> Unit)? = null
    private var onFinish: (() -> Unit)? = null

    fun start(onTick: (Long) -> Unit, onFinish: () -> Unit) {
        this.onTick = onTick
        this.onFinish = onFinish
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                timeLeftInMillis -= intervalInMillis
                if (timeLeftInMillis < 0) {
                    stop()
                    this@GameTimer.onFinish?.invoke()
                } else {
                    this@GameTimer.onTick?.invoke(timeLeftInMillis / 1000)
                }
            }
        }
        timer?.schedule(timerTask, intervalInMillis, intervalInMillis)
    }

    fun stop() {
        timerTask?.cancel()
        timer?.cancel()
        timer = null
        timerTask = null
    }

    fun restart(onTick: (Long) -> Unit, onFinish: () -> Unit) {
        stop()
        timeLeftInMillis = durationInSeconds * 1000
        start(onTick, onFinish)
    }

    fun getTimeLeftInSeconds(): Long {
        return timeLeftInMillis / 1000
    }
}
