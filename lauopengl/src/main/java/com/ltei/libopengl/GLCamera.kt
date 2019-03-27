package com.ltei.lauopengl

import android.opengl.Matrix

class GLCamera(
        private val surfaceWidth: Float,
        surfaceHeight: Float,
        private val projectedWidth: Float
) {

    private val surfaceHWRatio = surfaceHeight / surfaceWidth

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    val mvpMatrix = FloatArray(16)

    private var centerX: Float = 0.toFloat()
    private var centerY: Float = 0.toFloat()
    var zoomFactor: Float = 0.toFloat()

    init {
        this.centerX = 0f
        this.centerY = 0f
        this.zoomFactor = 1f

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        update()
    }


    fun move(x: Float, y: Float) {
        val moveScale = (2f * projectedWidth) / (surfaceWidth * zoomFactor)
        centerX += x * moveScale
        centerY += y * moveScale
        update()
    }

    fun zoom(zoom: Float) {
        this.zoomFactor *= zoom
        update()
    }


    private fun update() {
        val frustumWidth = projectedWidth / zoomFactor
        val frustumHeight = projectedWidth * surfaceHWRatio / zoomFactor
        Matrix.frustumM(projectionMatrix, 0,
                centerX + frustumWidth, centerX - frustumWidth,
                centerY - frustumHeight, centerY + frustumHeight,
                3f, 7f)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }

}