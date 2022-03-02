package com.example.voicerecoder

import android.Manifest
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {

    private val soundVisualizerView: SoundVisualizerView by lazy {
        findViewById(R.id.soundVisualizerView)
    }
    private val recordTimeTextView: CoountUpTextView by lazy {
        findViewById(R.id.recordTimeTextView)
    }
    private val recordButton: RecordButton by lazy {
        findViewById(R.id.recordButton)
    }
    private val resetButton: Button by lazy {
        findViewById(R.id.resetButton)
    }

    private var state = State.BEFORE_RECORDING
        set(value){
            field = value
            resetButton.isEnabled = (value == State.AFTER_RECORDING) || (value == State.ON_PLAYING)
            recordButton.updateIconWithState(value)
        }


    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    private val recordingFilePath: String by lazy {
        externalCacheDir?.absolutePath + "/recording.3gp"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestAudioPermission()
        initViews()
        bindViews()
        initVariables()
    }

    fun initViews(){
        recordButton.updateIconWithState(state)
    }

    private fun bindViews(){
        soundVisualizerView.onRequestCurrentAmplitude = {
            recorder?.maxAmplitude ?: 0
        }

        recordButton.setOnClickListener{
            when(state){
                State.BEFORE_RECORDING -> {
                    startRecording()
                }
                State.ON_RECORDING -> {
                    stopRecording()
                }
                State.AFTER_RECORDING -> {
                    startPlaying()
                }
                State.ON_PLAYING -> {
                    stopPlaying()
                }
            }
        }

        resetButton.setOnClickListener {
            stopPlaying()
            soundVisualizerView.clearVisualization()
            recordTimeTextView.clearCountTime()
            state = State.BEFORE_RECORDING
        }
    }

    private fun initVariables(){
        state = State.BEFORE_RECORDING
    }

    private fun startRecording(){
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(recordingFilePath)
            prepare()
        }
        recorder?.start()
        soundVisualizerView.startVisualizing(false)
        recordTimeTextView.startCountUp()
        state = State.ON_RECORDING
    }

    private fun stopRecording(){
        recorder?.run{
            stop()
            release()
        }
        recorder = null
        soundVisualizerView.stopVisualizing()
        recordTimeTextView.stopCountUp()
        state = State.AFTER_RECORDING
    }

    private fun startPlaying(){
        player = MediaPlayer()
            .apply {
                setDataSource(recordingFilePath)
                prepare()
            }

        player?.setOnCompletionListener {
            stopPlaying()
            state = State.AFTER_RECORDING
        }

        player?.start()
        soundVisualizerView.startVisualizing(true)
        recordTimeTextView.startCountUp()
        state = State.ON_PLAYING
    }

    private fun stopPlaying(){
        player?.release()
        player = null
        soundVisualizerView.stopVisualizing()
        recordTimeTextView.stopCountUp()
        state = State.AFTER_RECORDING
    }












    private fun requestAudioPermission() {
        var requiredPermissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                isGranted: Boolean ->
            if(!isGranted){
                Toast.makeText(this,"해당 권한은 필수입니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        for(permission in requiredPermissions){
            permissionLauncher.launch(permission)
        }
    }
}