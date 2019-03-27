package com.ltei.lauopengl

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLES20
import android.util.AttributeSet
import com.ltei.ljubase.LLog
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


open class GLSurfaceView2 : android.opengl.GLSurfaceView, android.opengl.GLSurfaceView.Renderer {

    private var drawableProvider: DrawableProvider? = null
    private var backgroundColor: GLColor? = null

    internal var shaderProgram: Int = 0

    internal var mvpMatrixHandle: Int = 0
    internal var positionHandle: Int = 0
    internal var colorHandle: Int = 0

    internal var camera: GLCamera? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    open fun setup(backgroundColor: GLColor, drawableProvider: DrawableProvider) {
        this.backgroundColor = backgroundColor
        this.drawableProvider = drawableProvider

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
        GLUtils.checkGlError {
            GLES20.glDisable(GLES20.GL_DEPTH_TEST)
            GLES20.glEnable(GLES20.GL_BLEND)
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
            GLES20.glBlendEquation(GLES20.GL_FUNC_ADD)
        }

        // Setup shader program

        val vertexShaderHandle = GLUtils.loadShader(vertexShaderCode, GLES20.GL_VERTEX_SHADER)
        val fragmentShaderHandle = GLUtils.loadShader(fragmentShaderCode, GLES20.GL_FRAGMENT_SHADER)

        shaderProgram = GLES20.glCreateProgram()
        GLUtils.checkGlError { GLES20.glAttachShader(shaderProgram, vertexShaderHandle) }
        GLUtils.checkGlError { GLES20.glAttachShader(shaderProgram, fragmentShaderHandle) }
        GLUtils.checkGlError { GLES20.glLinkProgram(shaderProgram) }

        // Get shader vars handle
        GLUtils.checkGlError { mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix") }
        GLUtils.checkGlError { positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition") }
        GLUtils.checkGlError { colorHandle = GLES20.glGetAttribLocation(shaderProgram, "aColor") }

        // Setup OpenGL
        GLUtils.checkGlError { GLES20.glClearColor(0f, 0f, 0f, 0f) }
    }

    override fun onDrawFrame(_unused: GL10) {
        LLog.debug(javaClass, "Rendering")

        GLUtils.checkGlError {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
            backgroundColor?.let { GLES20.glClearColor(it.red(), it.green(), it.blue(), it.alpha()) } // todo
        }

        GLUtils.checkGlError { GLES20.glUseProgram(shaderProgram) }
        GLUtils.checkGlError { GLES20.glEnableVertexAttribArray(positionHandle) }
        GLUtils.checkGlError { GLES20.glEnableVertexAttribArray(colorHandle) }

        camera?.let {
            GLUtils.checkGlError {
                GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, it.mvpMatrix, 0)
            }
        }

        drawableProvider?.get()?.forEach { drawable ->
            GLES20.glVertexAttribPointer(
                positionHandle,
                VALUES_PER_VERTEX_POSITION,
                GLES20.GL_FLOAT,
                false,
                4 * VALUES_PER_VERTEX_POSITION,
                drawable.positionVertices
            )
            GLES20.glVertexAttribPointer(
                colorHandle,
                VALUES_PER_VERTEX_COLOR,
                GLES20.GL_FLOAT,
                false,
                4 * VALUES_PER_VERTEX_COLOR,
                drawable.colorVertices
            )
            GLES20.glDrawElements(
                GLES20.GL_TRIANGLES,
                drawable.drawIndexCount,
                GLES20.GL_UNSIGNED_SHORT,
                drawable.drawIndices
            )
        }
        GLUtils.checkGlError()

        GLUtils.checkGlError {
            GLES20.glDisableVertexAttribArray(positionHandle)
            GLES20.glDisableVertexAttribArray(colorHandle)
        }

        // todo
        Thread.sleep(500)
        LLog.debug(javaClass, "Requesting render")
        requestRender()
    }

    override fun onSurfaceChanged(_unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        camera = GLCamera(width.toFloat(), height.toFloat(), 100f)
    }


    //
    //

    interface Drawable {
        val positionVertices: FloatBuffer
        val colorVertices: FloatBuffer
        val drawIndexCount: Int
        val drawIndices: ShortBuffer
    }

    interface DrawableProvider {
        fun get(): List<Drawable>
    }

    companion object {
        const val VALUES_PER_VERTEX_POSITION = 3
        const val VALUES_PER_VERTEX_COLOR = 4

        val vertexShaderCode = """
            uniform mat4 uMVPMatrix;
            attribute vec4 aPosition;
            attribute vec4 aColor;
            varying vec4 vColor;
            void main() {
                gl_Position = uMVPMatrix * aPosition;
                vColor = aColor;
            }
        """.trimIndent()

        val fragmentShaderCode = """
            precision mediump float;
            varying vec4 vColor;
            void main() {
                gl_FragColor = vColor;
            }
        """.trimIndent()
    }

}