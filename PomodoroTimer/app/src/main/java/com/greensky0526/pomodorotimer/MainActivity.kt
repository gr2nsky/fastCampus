package com.greensky0526.pomodorotimer

import android.annotation.SuppressLint
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val remainMinutesTextView: TextView by lazy{
        findViewById(R.id.remainMinutesTextView)
    }
    private val remainSecondTextView: TextView by lazy {
        findViewById(R.id.remainSecondTextView)
    }
    private val seekBar: SeekBar by lazy{
        findViewById(R.id.seekBar)
    }
    private val soundPool = SoundPool.Builder().build()

    private var currentCountDownTimer: CountDownTimer? = null
    private var tickingSoundId: Int? = null
    private var bellSoundId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        initSounds()
    }

    override fun onResume() {
        super.onResume()
        soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()
        soundPool.autoPause()
    }

    //비용이 크므로, 해지 해줘야 함
    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    private fun bindViews(){
        seekBar.setOnSeekBarChangeListener(
            object: SeekBar.OnSeekBarChangeListener{
                @SuppressLint("SetTextI18n")
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if(fromUser) {
                        updateRemainTime(progress * 60 * 1000L)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    stopCountDown()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    seekBar ?: return

                    if(seekBar.progress == 0){
                        stopCountDown()
                    }else{
                        startCountDown()
                    }
                }

            }
        )
    }//bindViews

    private fun initSounds(){
        tickingSoundId = soundPool.load(this, R.raw.timer_ticking, 1)
        bellSoundId = soundPool.load(this, R.raw.timer_bell, 1)
    }

    private fun createCountDownTimer(initialMillis: Long): CountDownTimer{
        return object: CountDownTimer(initialMillis, 1000){
            override fun onTick(millisUntilFinished: Long) {
                updateRemainTime(millisUntilFinished)
                updateSeekbar(millisUntilFinished)
            }

            override fun onFinish() {
                completeCountDown()
            }

        }
    }//createCountDownTimer

    private fun startCountDown(){
        currentCountDownTimer = createCountDownTimer(seekBar.progress * 60 * 1000L)
        currentCountDownTimer?.start()

        tickingSoundId?.let { soundId ->
            soundPool.play(soundId, 1F, 1F, 0, -1, 1F)
        }
    }

    private fun stopCountDown(){
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
        soundPool.autoPause()
    }

    private fun completeCountDown(){
        updateRemainTime(0)
        updateSeekbar(0)

        soundPool.autoPause()
        bellSoundId?.let { soundId ->
            soundPool.play(soundId, 1F, 1F, 0, 0, 1F)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateRemainTime(remainMillis: Long){
        val remainSeconds = remainMillis / 1000

        remainMinutesTextView.text = "%02d'".format(remainSeconds / 60)
        remainSecondTextView.text = "%02d".format(remainSeconds % 60)
    }//updateRemainTime

    private fun updateSeekbar(remainMillis: Long){
        seekBar.progress = (remainMillis / 1000 / 60).toInt()
    }
}