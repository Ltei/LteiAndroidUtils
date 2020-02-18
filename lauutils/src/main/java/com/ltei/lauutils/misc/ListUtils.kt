package com.ltei.lauutils.misc

object ListUtils {

    fun <T> getItem(position: Int, vararg lists: List<T>): T {
        if (position < 0) throw IndexOutOfBoundsException()
        var position = position
        for (list in lists) {
            if (position < list.size) {
                return list[position]
            } else {
                position -= list.size
            }
        }
        throw IndexOutOfBoundsException()
    }

}