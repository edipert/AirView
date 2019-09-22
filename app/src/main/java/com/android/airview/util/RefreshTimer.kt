package com.android.airview.util

import android.os.CountDownTimer

class RefreshTimer {
    companion object {
        private lateinit var timer: CountDownTimer
        private var secondsUntilFinished: Long = 0

        // Set and start timer with second info
        fun startTimer(
            seconds: Long,
            callback: (secondsUntilFinished: Int) -> Unit
        ): CountDownTimer {
            secondsUntilFinished = seconds
            if (!::timer.isInitialized) {
                timer = object : CountDownTimer(seconds * 1000, 1000) {
                    override fun onFinish() = callback(secondsUntilFinished.toInt())

                    override fun onTick(millisUntilFinished: Long) {
                        secondsUntilFinished = millisUntilFinished / 1000
                    }
                }.start()
            } else {
                timer.start()
            }

            return timer
        }

        // Stop timer if it is initialized
        fun stopTimer() {
            if (::timer.isInitialized) {
                timer.cancel()
                timer.onFinish()
            }
        }
    }
}