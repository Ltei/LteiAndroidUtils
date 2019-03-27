package com.ltei.lauopengl

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class GLShapeCircle(
        centerX: Float, centerY: Float, rayon: Float, color: GLColor,
        private val nbVertex: Int = 25
) : GLShape(color) {

    private val vertexBufferSize = this.nbVertex * GLConstants.BYTES_PER_VERTEX
    private val vertexBuffer: FloatBuffer

    init {
        val coords = FloatArray(this.nbVertex * GLConstants.VERTEX_DIMENSION)
        val cosMult = 2.0 * Math.PI / this.nbVertex
        for (i in 0 until this.nbVertex) {
            coords[i * 3 + 0] = centerX + (rayon * Math.cos(i.toDouble() * cosMult)).toFloat()
            coords[i * 3 + 1] = centerY + (rayon * Math.sin(i.toDouble() * cosMult)).toFloat()
            coords[i * 3 + 2] = 0f
        }

        val vertexByteBuffer = ByteBuffer.allocateDirect(vertexBufferSize)
        vertexByteBuffer.order(ByteOrder.nativeOrder())
        vertexBuffer = vertexByteBuffer.asFloatBuffer()
        vertexBuffer.put(coords)
        vertexBuffer.position(0)
    }


    override fun draw(surfaceView: GLSurfaceView) {
        GLES20.glVertexAttribPointer(surfaceView.positionHandle, GLConstants.VERTEX_DIMENSION, GLES20.GL_FLOAT,
                false, GLConstants.BYTES_PER_VERTEX, vertexBuffer)
        GLES20.glUniform4fv(surfaceView.colorHandle, 1, color.rgba, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, nbVertex)
    }

}