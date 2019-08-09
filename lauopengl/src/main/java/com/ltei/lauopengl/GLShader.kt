package com.ltei.lauopengl

import android.opengl.GLES20

abstract class GLShader(val type: Int, val code: String) {

    var handle: Int? = null
        private set

    fun load() {
        if (handle != null) throw IllegalStateException()
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, code)
        GLES20.glCompileShader(shader)
        handle = shader
    }

}

class GLFragmentShader(code: String) : GLShader(GLES20.GL_FRAGMENT_SHADER, code)

class GLVertexShader(
    code: String,
    val mvpMatrixUniformName: String = "uMVPMatrix"
) : GLShader(GLES20.GL_VERTEX_SHADER, code)