package com.ltei.lauopengl

import android.opengl.GLES20
import com.ltei.ljuutils.LMath
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class GLShapeSquare(
        x0: Float, y0: Float, x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float, color: GLColor
) : GLShape(color) {

    companion object {
        private const val NB_VERTEX = 4
        private const val VERTEX_BUFFER_SIZE = NB_VERTEX * GLConstants.BYTES_PER_VERTEX

        private const val NB_DRAW_INDEX = 6
        private const val DRAW_INDEX_BUFFER_SIZE = NB_DRAW_INDEX * GLConstants.BYTES_PER_DRAW_INDEX
        private val DRAW_INDEXES = shortArrayOf(0, 1, 2, 0, 2, 3)
    }


    private var vertexBuffer: FloatBuffer
    private var drawIndexBuffer: ShortBuffer

    init {
        val coords = floatArrayOf(x0, y0, 0f, x1, y1, 0f, x2, y2, 0f, x3, y3, 0f)
        val vertexByteBuffer = ByteBuffer.allocateDirect(VERTEX_BUFFER_SIZE)
        vertexByteBuffer.order(ByteOrder.nativeOrder())
        vertexBuffer = vertexByteBuffer.asFloatBuffer()
        vertexBuffer.put(coords)
        vertexBuffer.position(0)
        val drawIndexByteBuffer = ByteBuffer.allocateDirect(DRAW_INDEX_BUFFER_SIZE)
        drawIndexByteBuffer.order(ByteOrder.nativeOrder())
        drawIndexBuffer = drawIndexByteBuffer.asShortBuffer()
        drawIndexBuffer.put(DRAW_INDEXES)
        drawIndexBuffer.position(0)
    }

    fun newRectangle(centerX: Float, centerY: Float, sizeX: Float, sizeY: Float, color: GLColor): GLShapeSquare {
        val semiSizeX = sizeX / 2f
        val semiSizeY = sizeY / 2f
        return GLShapeSquare(centerX - semiSizeX, centerY - semiSizeY,
                centerX - semiSizeX, centerY + semiSizeY,
                centerX + semiSizeX, centerY + semiSizeY,
                centerX + semiSizeX, centerY - semiSizeY,
                color)
    }

    fun newSquare(centerX: Float, centerY: Float, size: Float, color: GLColor): GLShapeSquare {
        return newRectangle(centerX, centerY, size, size, color)
    }

    fun newLine(x0: Float, y0: Float, x1: Float, y1: Float, width: Float, color: GLColor): GLShapeSquare {
        val angle = com.ltei.ljuutils.LMath.angleRadFromVec(x1 - x0, y1 - y0)
        val widthAngle = angle + Math.PI / 2.0

        val semiWidth = width / 2f

        val deltaX = (semiWidth * Math.cos(widthAngle)).toFloat()
        val deltaY = (semiWidth * Math.sin(widthAngle)).toFloat()

        return GLShapeSquare(x0 + deltaX, y0 + deltaY,
                x0 - deltaX, y0 - deltaY,
                x1 - deltaX, y1 - deltaY,
                x1 + deltaX, y1 + deltaY,
                color)
    }


    override fun draw(surfaceView: GLSurfaceView) {
        GLES20.glVertexAttribPointer(surfaceView.positionHandle, GLConstants.VERTEX_DIMENSION, GLES20.GL_FLOAT, false, GLConstants.BYTES_PER_VERTEX, vertexBuffer)
        GLES20.glUniform4fv(surfaceView.colorHandle, 1, color.rgba, 0)
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, NB_DRAW_INDEX, GLES20.GL_UNSIGNED_SHORT, drawIndexBuffer)
    }

}