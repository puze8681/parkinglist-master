package kr.puze.parkinglist

import android.os.Parcel
import android.os.Parcelable

class RecyclerData(var phone: String?, var car: String?, var out: Boolean) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
        1 == source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(phone)
        writeString(car)
        writeInt((if (out) 1 else 0))
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<RecyclerData> = object : Parcelable.Creator<RecyclerData> {
            override fun createFromParcel(source: Parcel): RecyclerData = RecyclerData(source)
            override fun newArray(size: Int): Array<RecyclerData?> = arrayOfNulls(size)
        }
    }
}