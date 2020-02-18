package com.ltei.lauutils._deprecated

@Deprecated("Shouldn't be here..")
interface BoundingBox<T : Comparable<T>> {
    val left: T
    val top: T
    val right: T
    val bottom: T

    fun area(): T {
        return mult(abs(sub(right, left)), abs(sub(top, bottom)))
    }

    fun contains(box: BoundingBox<T>): Boolean {
        return box.left >= left && box.top <= top && box.right <= right && box.bottom >= bottom
    }

    fun intersects(box: BoundingBox<T>): Boolean {
        return intersects(box.left, box.top, box.right, box.bottom)
    }

    fun intersects(left: T, top: T, right: T, bottom: T): Boolean {
        return left < this.right && right > this.left && top > this.bottom && bottom < this.top
    }

    fun intersection(box: BoundingBox<T>): BoundingBox<T>? {
        return intersection(box.left, box.top, box.right, box.bottom)
    }

    fun intersection(left: T, top: T, right: T, bottom: T): BoundingBox<T>? {
        return if (left < this.right && right > this.left && top > this.bottom && bottom < this.top) {
            new<T>(
                if (left < this.left) this.left else left,
                if (top > this.top) this.top else top,
                if (right > this.right) this.right else right,
                if (bottom < this.bottom) this.bottom else bottom
            )
        } else {
            null
        }
    }

    fun mult(left: T, right: T): T
    fun sub(left: T, right: T): T
    fun abs(value: T): T

    fun getAsString(): String = "BoundingBox[left=$left,top=$top,right=$right,bottom=$bottom]"

    companion object {
        fun <T : Comparable<T>> new(left: T, top: T, right: T, bottom: T): BoundingBox<T> =
            newMutable(left, top, right, bottom)

        fun <T : Comparable<T>> newMutable(left: T, top: T, right: T, bottom: T): Mutable<T> {
            return when (left) {
                is Int -> IntImpl(
                    left,
                    top as Int,
                    right as Int,
                    bottom as Int
                ) as Mutable<T>
                is Long -> LongImpl(
                    left,
                    top as Long,
                    right as Long,
                    bottom as Long
                ) as Mutable<T>
                is Float -> FloatImpl(
                    left,
                    top as Float,
                    right as Float,
                    bottom as Float
                ) as Mutable<T>
                is Double -> DoubleImpl(
                    left,
                    top as Double,
                    right as Double,
                    bottom as Double
                ) as Mutable<T>
                else -> TODO("Unimplemented")
            }
        }
    }

    interface Mutable<T: Comparable<T>> : BoundingBox<T> {
        override var left: T
        override var top: T
        override var right: T
        override var bottom: T
    }

    class IntImpl(
        override var left: Int,
        override var top: Int,
        override var right: Int,
        override var bottom: Int
    ) : Mutable<Int> {
        companion object {
            fun fromCenterAndSize(centerX: Int, centerY: Int, sizeX: Int, sizeY: Int): IntImpl {
                val hsx = sizeX / 2
                val hsy = sizeY / 2
                return IntImpl(
                    centerX - hsx,
                    centerY + hsy,
                    centerX + hsx,
                    centerY - hsy
                )
            }
        }

        init {
            assert(left < right)
            assert(bottom < top)
        }

        override fun mult(left: Int, right: Int) = left * right
        override fun sub(left: Int, right: Int) = left - right
        override fun abs(value: Int) = kotlin.math.abs(value)
        override fun toString() = getAsString()
    }

    class LongImpl(
        override var left: Long,
        override var top: Long,
        override var right: Long,
        override var bottom: Long
    ) : Mutable<Long> {
        companion object {
            fun fromCenterAndSize(centerX: Long, centerY: Long, sizeX: Long, sizeY: Long): LongImpl {
                val hsx = sizeX / 2
                val hsy = sizeY / 2
                return LongImpl(
                    centerX - hsx,
                    centerY + hsy,
                    centerX + hsx,
                    centerY - hsy
                )
            }
        }

        init {
            assert(left < right)
            assert(bottom < top)
        }

        override fun mult(left: Long, right: Long) = left * right
        override fun sub(left: Long, right: Long) = left - right
        override fun abs(value: Long) = kotlin.math.abs(value)
        override fun toString() = getAsString()
    }

    class FloatImpl(
        override var left: Float,
        override var top: Float,
        override var right: Float,
        override var bottom: Float
    ) : Mutable<Float> {
        companion object {
            fun fromCenterAndSize(centerX: Float, centerY: Float, sizeX: Float, sizeY: Float): FloatImpl {
                val hsx = sizeX / 2
                val hsy = sizeY / 2
                return FloatImpl(
                    centerX - hsx,
                    centerY + hsy,
                    centerX + hsx,
                    centerY - hsy
                )
            }
        }

        init {
            assert(left < right)
            assert(bottom < top)
        }

        override fun mult(left: Float, right: Float) = left * right
        override fun sub(left: Float, right: Float) = left - right
        override fun abs(value: Float) = kotlin.math.abs(value)
        override fun toString() = getAsString()
    }

    class DoubleImpl(
        override var left: Double,
        override var top: Double,
        override var right: Double,
        override var bottom: Double
    ) : Mutable<Double> {
        companion object {
            fun fromCenterAndSize(centerX: Double, centerY: Double, sizeX: Double, sizeY: Double): DoubleImpl {
                val hsx = sizeX / 2
                val hsy = sizeY / 2
                return DoubleImpl(
                    centerX - hsx,
                    centerY + hsy,
                    centerX + hsx,
                    centerY - hsy
                )
            }
        }

        init {
            assert(left < right)
            assert(bottom < top)
        }

        override fun mult(left: Double, right: Double) = left * right
        override fun sub(left: Double, right: Double) = left - right
        override fun abs(value: Double) = kotlin.math.abs(value)
        override fun toString() = getAsString()
    }

}