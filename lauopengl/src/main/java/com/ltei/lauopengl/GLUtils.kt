package com.ltei.lauopengl

import android.opengl.GLES20
import android.util.Log
import com.ltei.ljubase.Logger
import java.nio.*

object GLUtils {

    private val logger = Logger(GLUtils::class.java)

    fun createFloatBuffer(capacity: Int): FloatBuffer {
        val result = ByteBuffer.allocateDirect(4 * capacity)
        result.order(ByteOrder.nativeOrder())
        return result.asFloatBuffer()
    }

    fun createShortBuffer(capacity: Int): ShortBuffer {
        val result = ByteBuffer.allocateDirect(2 * capacity)
        result.order(ByteOrder.nativeOrder())
        return result.asShortBuffer()
    }

    fun createIntBuffer(capacity: Int): IntBuffer {
        val result = ByteBuffer.allocateDirect(4 * capacity)
        result.order(ByteOrder.nativeOrder())
        return result.asIntBuffer()
    }

    fun loadShader(code: String, type: Int): Int {
        val shader = GLES20.glCreateShader(type)
        checkGlError {
            GLES20.glShaderSource(shader, code)
            GLES20.glCompileShader(shader)
        }

        val buffer = GLUtils.createIntBuffer(10)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, buffer)
        var status = buffer.get(0)
        if (status == 0) {
            GLES20.glGetShaderiv(shader, GLES20.GL_INFO_LOG_LENGTH, buffer)
            status = buffer.get(0)
            if (status > 1) {
                logger.debug("Vertex Shader: " + GLES20.glGetShaderInfoLog(shader))
            }
            GLES20.glDeleteShader(shader)
            logger.debug("Vertex Shader error.")
        }

        return shader
    }

    inline fun checkGlError(block: () -> Unit) {
        block.invoke()
        checkGlError()
    }

    fun checkGlError() {
        val error = GLES20.glGetError()
        if (error != GLES20.GL_NO_ERROR) {
            Log.e("GLRenderer", "glError $error")
            throw RuntimeException("glError $error")
        }
    }

}