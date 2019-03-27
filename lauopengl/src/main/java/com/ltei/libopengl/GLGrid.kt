package com.ltei.lauopengl

import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.random.Random
import com.ltei.lauopengl.GLSurfaceView2.Companion.VALUES_PER_VERTEX_POSITION
import com.ltei.lauopengl.GLSurfaceView2.Companion.VALUES_PER_VERTEX_COLOR

class GLGrid(val xS: FloatArray, val yS: FloatArray) : GLSurfaceView2.Drawable {

    val width = xS.size
    fun getVertexIndex(row: Int, col: Int) = row + col * width
    fun vertexIndexToRow(idx: Int) = idx % width
    fun vertexIndexToCol(idx: Int) = idx / width

    val vertexCount = xS.size * yS.size
    override val drawIndexCount = (xS.size - 1) * (yS.size - 1) * 6

    override val positionVertices: FloatBuffer = GLUtils.createFloatBuffer(VALUES_PER_VERTEX_POSITION * vertexCount)
    override val colorVertices: FloatBuffer = GLUtils.createFloatBuffer(VALUES_PER_VERTEX_COLOR * vertexCount)
    override var drawIndices: ShortBuffer = GLUtils.createShortBuffer(drawIndexCount)

    init {
        println("${xS.size} ${yS.size}")
        xS.sort()
        yS.sort()

        val rand = Random(242)
        for (i in xS.indices) {
            for (j in yS.indices) {
                val idx = getVertexIndex(i, j)
                positionVertices.put(VALUES_PER_VERTEX_POSITION * idx + 0, xS[i])
                positionVertices.put(VALUES_PER_VERTEX_POSITION * idx + 1, yS[j])
                positionVertices.put(VALUES_PER_VERTEX_POSITION * idx + 2, 0f)
                colorVertices.put(VALUES_PER_VERTEX_COLOR * idx + 0, 0f/*rand.nextFloat()*/)
                colorVertices.put(VALUES_PER_VERTEX_COLOR * idx + 1, 0f/*rand.nextFloat()*/)
                colorVertices.put(VALUES_PER_VERTEX_COLOR * idx + 2, 0f/*rand.nextFloat()*/)
                colorVertices.put(VALUES_PER_VERTEX_COLOR * idx + 3, 1f)
            }
        }

        var indexBufferIdx = 0
        for (i in 0 until xS.size - 1) {
            for (j in 0 until yS.size - 1) {
                drawIndices.put(indexBufferIdx++, getVertexIndex(i, j).toShort())
                drawIndices.put(indexBufferIdx++, getVertexIndex(i + 1, j).toShort())
                drawIndices.put(indexBufferIdx++, getVertexIndex(i + 1, j + 1).toShort())
                drawIndices.put(indexBufferIdx++, getVertexIndex(i, j).toShort())
                drawIndices.put(indexBufferIdx++, getVertexIndex(i + 1, j + 1).toShort())
                drawIndices.put(indexBufferIdx++, getVertexIndex(i, j + 1).toShort())
            }
        }

        positionVertices.position(0)
        colorVertices.position(0)
        drawIndices.position(0)
    }

}