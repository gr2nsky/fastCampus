package com.example.voicerecoder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

/**
 * @author Yoon
 * @created 2022-03-02
 */
class SoundVisualizerView(
    context: Context,
    attrs: AttributeSet? = null
): View(context, attrs) {

    var onRequestCurrentAmplitude: (() -> Int)? = null

    //계단화 방지 flag : ANTI_ALIAS_FLAG
    private val amplitudePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.purple_500) //색 지정
        strokeWidth = LINE_WIDTH //너비 지정
        strokeCap = Paint.Cap.ROUND //라인 양끝 둥글게
    }

    private var drawingWidth: Int = 0
    private var drawingHeight: Int = 0
    private var drawingAmplitudes: List<Int> = emptyList()
    private var isReplaying = false
    private var replayingPosition = 0

    private val visualizeRepeatAction: Runnable = object : Runnable {
        override fun run() {
            if(isReplaying.not()){
                val currentAmplitude = onRequestCurrentAmplitude?.invoke() ?: 0

                //amplitude, draw
                drawingAmplitudes = listOf(currentAmplitude) + drawingAmplitudes
            } else {
                replayingPosition ++
            }

            //view 갱신
            invalidate()

            //20millis 딜레이를 주며  반복
            handler?.postDelayed(this, ACTION_INTERVAL)
        }

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawingHeight = h
        drawingWidth = w
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        val centerY = drawingHeight / 2f
        var offsetX = drawingWidth.toFloat()

        drawingAmplitudes
            .let { amplitude ->
                if(isReplaying){
                    amplitude.takeLast(replayingPosition)
                }else {
                    amplitude
                }
            }
            .forEach{ amplitude ->
            //현재 증폭값 / 최대값 * 그릴 높이 * 최대치의 80%까지만
            val lineLength = amplitude / MAX_AMPLITUDE * drawingHeight * 0.8F

            offsetX -= LINE_SPACE

            //뷰 밖으로 넘어갔을 시 그리기 중단
            if(offsetX < 0) return@forEach

            //라인은 x값은 그대로, y값만 차이 남
            canvas.drawLine(
                offsetX,
                centerY - lineLength / 2F,
                offsetX,
                centerY + lineLength / 2F,
                amplitudePaint
            )

            //fps
        }

    }

    fun startVisualizing(isReplaying: Boolean){
        this.isReplaying = isReplaying
        handler?.post(visualizeRepeatAction)
    }

    fun stopVisualizing(){
        //재생할 위치 초기화, 다회차 리플레이 구현
        replayingPosition = 0
        handler?.removeCallbacks(visualizeRepeatAction)
    }

    fun clearVisualization(){
        drawingAmplitudes = emptyList()
        invalidate()
    }

    companion object{
        private const val LINE_WIDTH = 10F
        private const val LINE_SPACE = 15F
        private const val MAX_AMPLITUDE = Short.MAX_VALUE.toFloat()
        private const val ACTION_INTERVAL = 20L
    }

}