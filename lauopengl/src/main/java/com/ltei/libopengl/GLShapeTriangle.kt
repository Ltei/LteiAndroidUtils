package com.ltei.lauopengl

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class GLShapeTriangle(
        x0: Float, y0: Float, x1: Float, y1: Float, x2: Float, y2: Float, color: GLColor
): GLShape(color) {

    companion object {
        private const val NB_VERTEX = 3
        private const val VERTEX_BUFFER_SIZE = NB_VERTEX * GLConstants.BYTES_PER_VERTEX
    }


    private var vertexBuffer: FloatBuffer

    init {
        val coords = floatArrayOf(x0, y0, 0.0f, x1, y1, 0.0f, x2, y2, 0.0f)
        val vertexByteBuffer = ByteBuffer.allocateDirect(VERTEX_BUFFER_SIZE)
        vertexByteBuffer.order(ByteOrder.nativeOrder())
        vertexBuffer = vertexByteBuffer.asFloatBuffer()
        vertexBuffer.put(coords)
        vertexBuffer.position(0)
    }


    override fun draw(surfaceView: GLSurfaceView) {
        GLES20.glVertexAttribPointer(surfaceView.positionHandle, GLConstants.VERTEX_DIMENSION, GLES20.GL_FLOAT,
                false, GLConstants.BYTES_PER_VERTEX, vertexBuffer)
        GLES20.glUniform4fv(surfaceView.colorHandle, 1, color.rgba, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, NB_VERTEX)
    }

}