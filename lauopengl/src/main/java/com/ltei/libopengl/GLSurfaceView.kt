package com.ltei.lauopengl

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLES20
import android.util.AttributeSet
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

open class GLSurfaceView : android.opengl.GLSurfaceView, android.opengl.GLSurfaceView.Renderer {

    private var shapeDrawer: ShapeDrawer? = null
    private var backgroundColor: GLColor? = null

    internal var shaderProgram: Int = 0
    internal var positionHandle: Int = 0
    internal var colorHandle: Int = 0
    internal var mvpMatrixHandle: Int = 0

    internal var camera: GLCamera? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)


    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }

    private fun checkGlError(glOperation: String) {
        val error = GLES20.glGetError()
        if (error != GLES20.GL_NO_ERROR) {
            Log.e("GLRenderer", "$glOperation: glError $error")
            throw RuntimeException("$glOperation: glError $error")
        }
    }

    open fun setup(backgroundColor: GLColor, drawer: ShapeDrawer) {
        this.shapeDrawer = drawer
        this.backgroundColor = backgroundColor

        setEGLContextClientVersion(2)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        setZOrderOnTop(true)
        setRenderer(this)
        renderMode = RENDERMODE_WHEN_DIRTY
        holder.setFormat(PixelFormat.TRANSLUCENT)
    }

    //
    // GLSurfaceView.Renderer

    override fun onSurfaceCreated(_unused: GL10, config: EGLConfig) {
//        GLES20.glDisable(GLES20.GL_CULL_FACE) // Draw hidden faces (useful for a translucent cube)
        GLES20.glDisable(GLES20.GL_DEPTH_TEST)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glBlendEquation(GLES20.GL_FUNC_ADD)
        checkGlError("setup")

        // Setup shader program
        val defaultVertexShaderCode = "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                "void main() {" +
                "  gl_Position = uMVPMatrix * vPosition;" +
                "}"
        val defaultFragmentShaderCode = "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

        val defaultVertexShader = loadShader(GLES20.GL_VERTEX_SHADER, defaultVertexShaderCode)
        val defaultFragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, defaultFragmentShaderCode)

        shaderProgram = GLES20.glCreateProgram()
        GLES20.glAttachShader(shaderProgram, defaultVertexShader)
        GLES20.glAttachShader(shaderProgram, defaultFragmentShader)
        GLES20.glLinkProgram(shaderProgram)

        // Get shader vars handle
        positionHandle = GLES20.glGetAttribLocation(shaderProgram, "vPosition")
        colorHandle = GLES20.glGetUniformLocation(shaderProgram, "vColor")
        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix")
        checkGlError("glGetUniformLocation")

        // Setup OpenGL
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        checkGlError("glClearColor")
    }

    override fun onDrawFrame(_unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        checkGlError("glClear")
        //backgroundColor?.let { GLES20.glClearColor(it.red(), it.green(), it.blue(), it.alpha()) } // todo
        GLES20.glUseProgram(shaderProgram)
        checkGlError("glUseProgram")
        GLES20.glEnableVertexAttribArray(positionHandle)
        checkGlError("glEnableVertexAttribArray")
        camera?.let {
            GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, it.mvpMatrix, 0)
        }
        checkGlError("glUniformMatrix4fv")
        shapeDrawer?.drawShapes(this)
        checkGlError("drawShapes")
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    override fun onSurfaceChanged(_unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        camera = GLCamera(width.toFloat(), height.toFloat(), 100f)
    }


    //
    //

    interface ShapeDrawer {
        fun drawShapes(surfaceView: GLSurfaceView)
    }

}
