package com.enenworld.android.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import com.enenworld.enenworld.R


class DragRemoveLayout(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private lateinit var deleteButton: View
    private lateinit var contentView: View
    private var initialX: Float = 0f
    private var isSwiping = false
    private var swipeMax: Int = 0
    private val interpolator: Interpolator by lazy { AccelerateInterpolator() }

    override fun onFinishInflate() {
        super.onFinishInflate()
        deleteButton = findViewById(R.id.btn_remove_item)
        contentView = findViewById(R.id.layer)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (isSwiping) {
            return true
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = event.x
                isSwiping = false
                if (swipeMax == 0) {
                    swipeMax =
                        deleteButton.width + (((deleteButton.layoutParams) as LayoutParams).marginEnd).shl(
                            1
                        )
                }
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val deltaX = (event.x - initialX).toInt()
                initialX = event.x
                var currentTx = contentView.translationX.toInt()
                if (deltaX < 0 || currentTx < 0) {
                    requestDisallowInterceptTouchEvent(true)
                    isSwiping = true
                    currentTx = if (deltaX <= -swipeMax) {
                        -swipeMax
                    } else {
                        currentTx + deltaX
                    }
                    contentView.translationX = currentTx.toFloat()
                    return true
                } else if (isSwiping) {
                    return true
                }
            }

            MotionEvent.ACTION_UP -> {
                if (isSwiping) {
                    playAnim()
                    return true
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                if (isSwiping) {
                    playAnim()
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun playAnim() {
        if (isOpen()) {
            // 展开
            ViewCompat.animate(contentView).translationX(-swipeMax.toFloat())
                .setInterpolator(interpolator).start()
        } else {
            // 收起
            ViewCompat.animate(contentView).translationX(0f).setInterpolator(interpolator).start()
        }
        isSwiping = false
    }

    fun isOpen(): Boolean {
        if (swipeMax == 0) return false
        val currentTx = contentView.translationX.toInt()
        val middleLine = swipeMax.shr(1)
        return currentTx < -middleLine
    }

}
