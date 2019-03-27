package com.ltei.lauutils

import android.os.Bundle
import java.io.Serializable

object LBundles {

    fun int(id: String, value: Int): Bundle {
        val bundle = Bundle()
        bundle.putInt(id, value)
        return bundle
    }

    fun serializable(id: String, value: Serializable): Bundle {
        val bundle = Bundle()
        bundle.putSerializable(id, value)
        return bundle
    }

    fun serializableList(id: String, value: List<Serializable>): Bundle {
        return serializable(id, SerializableList(value))
    }

    private class SerializableList(list: List<Serializable>) : ArrayList<Serializable>(list), Serializable

}