package com.wykh4hn.asm2_cos30017.model

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.DrawableRes

/**
 * One car the company can rent. The data lives in memory only (see CarData).
 *
 * Parcelable so a whole Car object can travel inside an Intent to BookingActivity.
 * Written by hand (instead of @Parcelize) because the kotlin-parcelize plugin is not
 * picked up by AGP 9's built-in Kotlin; the fields are written and read in the same order.
 */
data class Car(
    val id: Int,
    val name: String,
    val model: String,
    val year: Int,
    val rating: Float,
    val kilometres: Int,
    val dailyCost: Int,
    val category: String,
    @DrawableRes val imageRes: Int
) : Parcelable {

    /** Maker and model together, e.g. "Toyota Corolla". */
    val fullName: String
        get() = "$name $model"

    /** Rebuilds a Car from a Parcel, reading the fields in the order writeToParcel wrote them. */
    constructor(parcel: Parcel) : this(
        parcel.readInt(),               // id
        parcel.readString().orEmpty(),  // name
        parcel.readString().orEmpty(),  // model
        parcel.readInt(),               // year
        parcel.readFloat(),             // rating
        parcel.readInt(),               // kilometres
        parcel.readInt(),               // dailyCost
        parcel.readString().orEmpty(),  // category
        parcel.readInt()                // imageRes
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(model)
        parcel.writeInt(year)
        parcel.writeFloat(rating)
        parcel.writeInt(kilometres)
        parcel.writeInt(dailyCost)
        parcel.writeString(category)
        parcel.writeInt(imageRes)
    }

    override fun describeContents(): Int = 0

    /** Android uses CREATOR to turn a Parcel back into a Car on the other screen. */
    companion object CREATOR : Parcelable.Creator<Car> {
        override fun createFromParcel(parcel: Parcel): Car = Car(parcel)
        override fun newArray(size: Int): Array<Car?> = arrayOfNulls(size)
    }
}