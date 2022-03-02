package com.example.voicerecoder

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton

/**
 * @author Yoon
 * @created 2022-03-02
 */
class RecordButton(
    context: Context,
    attrs: AttributeSet
): AppCompatImageButton(context, attrs) {

    init {
        setBackgroundResource(R.drawable.shape_oval_button)
    }


    fun updateIconWithState(state: State){
        when(state){
            State.BEFORE_RECORDING -> {
                setImageResource(R.drawable.ic_recordd)
            }
            State.ON_RECORDING -> {

                setImageResource(R.drawable.ic_stop)
            }
            State.AFTER_RECORDING -> {
                setImageResource(R.drawable.ic_play)
            }
            State.ON_PLAYING -> {
                setImageResource(R.drawable.ic_stop)
            }
        }
    }
}