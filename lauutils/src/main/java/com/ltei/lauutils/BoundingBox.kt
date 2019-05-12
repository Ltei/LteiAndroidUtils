package com.ltei.lauutils

import kotlin.math.abs

class BoundingBoxInt(val left: Int, val top: Int, val right: Int, val bottom: Int) {

    companion object {
        fun fromCenterAndSize(centerX: Int, centerY: Int, sizeX: Int, sizeY: Int): BoundingBoxInt {
            val hsx = sizeX / 2
            val hsy = sizeY / 2
            return BoundingBoxInt(centerX - hsx, centerY + hsy, centerX + hsx, centerY - hsy)
        }
    }

    init {
        assert(left < right)
        assert(bottom < top)
    }

    fun area(): Int {
        return abs(right - left) * abs(top - bottom)
    }

    fun contains(box: BoundingBoxInt): Boolean {
        return box.left >= left && box.top <= top && box.right <= right && box.bottom >= bottom
    }

    fun intersects(box: BoundingBoxInt): Boolean {
        return intersects(box.left, box.top, box.right, box.bottom)
    }

    fun intersects(left: Int, top: Int, right: Int, bottom: Int): Boolean {
        return left < this.right && right > this.left && top > this.bottom && bottom < this.top
    }

    fun intersection(box: BoundingBoxInt): BoundingBoxInt? {
        return intersection(box.left, box.top, box.right, box.bottom)
    }

    fun intersection(left: Int, top: Int, right: Int, bottom: Int): BoundingBoxInt? {
        return if (left < this.right && right > this.left && top > this.bottom && bottom < this.top) {
            BoundingBoxInt(
                    if (left < this.left) this.left else left,
                    if (top > this.top) this.top else top,
                    if (right > this.right) this.right else right,
                    if (bottom < this.bottom) this.bottom else bottom
            )
        } else {
            null
        }
    }

    override fun toString(): String {
        return "BoundingBoxInt[left=$left,top=$top,right=$right,bottom=$bottom]"
    }
}

class BoundingBoxLong(val left: Long, val top: Long, val right: Long, val bottom: Long) {

    companion object {
        fun fromCenterAndSize(centerX: Long, centerY: Long, sizeX: Long, sizeY: Long): BoundingBoxLong {
            val hsx = sizeX / 2
            val hsy = sizeY / 2
            return BoundingBoxLong(centerX - hsx, centerY + hsy, centerX + hsx, centerY - hsy)
        }
    }

    init {
        assert(left < right)
        assert(bottom < top)
    }

    fun area(): Long {
        return abs(right - left) * abs(top - bottom)
    }

    fun contains(box: BoundingBoxLong): Boolean {
        return box.left >= left && box.top <= top && box.right <= right && box.bottom >= bottom
    }

    fun intersects(box: BoundingBoxLong): Boolean {
        return intersects(box.left, box.top, box.right, box.bottom)
    }

    fun intersects(left: Long, top: Long, right: Long, bottom: Long): Boolean {
        return left < this.right && right > this.left && top > this.bottom && bottom < this.top
    }

    fun intersection(box: BoundingBoxLong): BoundingBoxLong? {
        return intersection(box.left, box.top, box.right, box.bottom)
    }

    fun intersection(left: Long, top: Long, right: Long, bottom: Long): BoundingBoxLong? {
        return if (left < this.right && right > this.left && top > this.bottom && bottom < this.top) {
            BoundingBoxLong(
                    if (left < this.left) this.left else left,
                    if (top > this.top) this.top else top,
                    if (right > this.right) this.right else right,
                    if (bottom < this.bottom) this.bottom else bottom
            )
        } else {
            null
        }
    }

    override fun toString(): String {
        return "BoundingBoxLong[left=$left,top=$top,right=$right,bottom=$bottom]"
    }
}

class BoundingBoxFloat(val left: Float, val top: Float, val right: Float, val bottom: Float) {

    companion object {
        fun fromCenterAndSize(centerX: Float, centerY: Float, sizeX: Float, sizeY: Float): BoundingBoxFloat {
            val hsx = sizeX / 2
            val hsy = sizeY / 2
            return BoundingBoxFloat(centerX - hsx, centerY + hsy, centerX + hsx, centerY - hsy)
        }
    }

    init {
        assert(left < right)
        assert(bottom < top)
    }

    fun area(): Float {
        return abs(right - left) * abs(top - bottom)
    }

    fun contains(box: BoundingBoxFloat): Boolean {
        return box.left >= left && box.top <= top && box.right <= right && box.bottom >= bottom
    }

    fun intersects(box: BoundingBoxFloat): Boolean {
        return intersects(box.left, box.top, box.right, box.bottom)
    }

    fun intersects(left: Float, top: Float, right: Float, bottom: Float): Boolean {
        return left < this.right && right > this.left && top > this.bottom && bottom < this.top
    }

    fun intersection(box: BoundingBoxFloat): BoundingBoxFloat? {
        return intersection(box.left, box.top, box.right, box.bottom)
    }

    fun intersection(left: Float, top: Float, right: Float, bottom: Float): BoundingBoxFloat? {
        return if (left < this.right && right > this.left && top > this.bottom && bottom < this.top) {
            BoundingBoxFloat(
                    if (left < this.left) this.left else left,
                    if (top > this.top) this.top else top,
                    if (right > this.right) this.right else right,
                    if (bottom < this.bottom) this.bottom else bottom
            )
        } else {
            null
        }
    }

    override fun toString(): String {
        return "BoundingBoxFloat[left=$left,top=$top,right=$right,bottom=$bottom]"
    }
}

class BoundingBoxDouble(val left: Double, val top: Double, val right: Double, val bottom: Double) {

    companion object {
        fun fromCenterAndSize(centerX: Double, centerY: Double, sizeX: Double, sizeY: Double): BoundingBoxDouble {
            val hsx = sizeX / 2
            val hsy = sizeY / 2
            return BoundingBoxDouble(centerX - hsx, centerY + hsy, centerX + hsx, centerY - hsy)
        }
    }

    init {
        assert(left < right)
        assert(bottom < top)
    }

    fun area(): Double {
        return abs(right - left) * abs(top - bottom)
    }

    fun contains(box: BoundingBoxDouble): Boolean {
        return box.left >= left && box.top <= top && box.right <= right && box.bottom >= bottom
    }

    fun intersects(box: BoundingBoxDouble): Boolean {
        return intersects(box.left, box.top, box.right, box.bottom)
    }

    fun intersects(left: Double, top: Double, right: Double, bottom: Double): Boolean {
        return left < this.right && right > this.left && top > this.bottom && bottom < this.top
    }

    fun intersection(box: BoundingBoxDouble): BoundingBoxDouble? {
        return intersection(box.left, box.top, box.right, box.bottom)
    }

    fun intersection(left: Double, top: Double, right: Double, bottom: Double): BoundingBoxDouble? {
        return if (left < this.right && right > this.left && top > this.bottom && bottom < this.top) {
            BoundingBoxDouble(
                    if (left < this.left) this.left else left,
                    if (top > this.top) this.top else top,
                    if (right > this.right) this.right else right,
                    if (bottom < this.bottom) this.bottom else bottom
            )
        } else {
            null
        }
    }

    override fun toString(): String {
        return "BoundingBoxDouble[left=$left,top=$top,right=$right,bottom=$bottom]"
    }
}