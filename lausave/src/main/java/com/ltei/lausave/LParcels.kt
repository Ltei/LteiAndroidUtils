package com.ltei.lausave

import android.os.Parcel
import android.os.Parcelable

@Deprecated("lausave will be removed")
object LParcels {

    fun writeNullableString(parcel: Parcel, str: String?) {
        if (str == null) {
            parcel.writeInt(0)
        } else {
            parcel.writeInt(1)
            parcel.writeString(str)
        }
    }

    fun readNullableString(parcel: Parcel): String? {
        val isNull = parcel.readInt() == 0
        return if (isNull) {
            null
        } else {
            parcel.readString()
        }
    }

    fun writeNullableParcelable(parcel: Parcel, parcelable: Parcelable?, flags: Int) {
        if (parcelable == null) {
            parcel.writeInt(0)
        } else {
            parcel.writeInt(1)
            parcel.writeParcelable(parcelable, flags)
        }
    }

    fun readNullableParcelable(parcel: Parcel, classLoader: ClassLoader): Parcelable? {
        val isNull = parcel.readInt() == 0
        return if (isNull) {
            null
        } else {
            parcel.readParcelable(classLoader)
        }
    }
    
}