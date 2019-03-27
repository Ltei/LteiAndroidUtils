package com.ltei.lauopengl

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector

open class GLSurfaceViewNavigable : GLSurfaceView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)


    private var scaleDetector: ScaleGestureDetector? = null

    private var activePointerID: Int? = null
    private var lastTouchX: Float = 0f
    private var lastTouchY: Float = 0f


    override fun setup(backgroundColor: GLColor, drawer: GLSurfaceView.ShapeDrawer) {
        super.setup(backgroundColor, drawer)
        this.scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                camera?.zoom(detector.scaleFactor)
                return true
            }
        })
    }


    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val lastScaleFactor = camera?.zoomFactor
        scaleDetector?.onTouchEvent(ev)

        val action = ev.action

        if (action == MotionEvent.ACTION_DOWN) {
            val pointerIndex = ev.actionIndex
            lastTouchX = ev.getX(pointerIndex)
            lastTouchY = ev.getY(pointerIndex)
            activePointerID = ev.getPointerId(pointerIndex)

        } else if (action == MotionEvent.ACTION_MOVE) {
            camera?.let {
                if (lastScaleFactor == it.zoomFactor) {
                    val pointerIndex = ev.actionIndex
                    val pointerId = ev.getPointerId(pointerIndex)
                    if (pointerId == activePointerID) {
                        val evX = ev.getX(pointerIndex)
                        val evY = ev.getY(pointerIndex)
                        it.move(evX - lastTouchX, evY - lastTouchY)
                        lastTouchX = evX
                        lastTouchY = evY
                    }
                }
            }

        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            activePointerID = null

        } else if (action == MotionEvent.ACTION_POINTER_UP) {
            val pointerIndex = ev.actionIndex
            val pointerId = ev.getPointerId(pointerIndex)
            if (pointerId == activePointerID) {
                val newPointerIndex = if (pointerIndex == 0) 1 else 0
                lastTouchX = ev.getX(newPointerIndex)
                lastTouchY = ev.getY(newPointerIndex)
                activePointerID = ev.getPointerId(newPointerIndex)
            }

        }

        if (renderMode != RENDERMODE_CONTINUOUSLY) {
            requestRender()
        }
        return true
    }


}