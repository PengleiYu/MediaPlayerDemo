package com.example.playerdemo.surface

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import timber.log.Timber

/**
 * 简单的surfaceDemo
 * 在surface上使用canvas绘制
 */
class SurfaceDrawer : SurfaceView {
    private val paint = Paint()
    private val surfaceCallback = object : SurfaceHolder.Callback {
        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            Timber.d("surfaceChanged")
            draw()
        }

        override fun surfaceDestroyed(holder: SurfaceHolder?) {
            Timber.d("surfaceDestroyed")
        }

        override fun surfaceCreated(holder: SurfaceHolder?) {
            Timber.d("surfaceCreated")
        }
    }

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView()
    }

    private fun initView() {
        holder.addCallback(surfaceCallback)
    }

    fun draw() {
        Timber.d("draw")
        val canvas = holder.lockCanvas()
        canvas.drawColor(Color.GREEN)
        paint.color = Color.YELLOW
        canvas.drawCircle(width / 2f, height / 2f, width / 3f, paint)
        holder.unlockCanvasAndPost(canvas)
    }
}