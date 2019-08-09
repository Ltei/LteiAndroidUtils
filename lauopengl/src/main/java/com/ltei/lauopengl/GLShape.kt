package com.ltei.lauopengl


abstract class GLShape(protected val color: GLColor = GLColor.white()) {
    abstract fun draw(surfaceView: GLSurfaceView)
}