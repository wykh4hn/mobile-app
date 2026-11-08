package com.wykh4hn.asm2_cos30017.model

import android.os.Parcel
import android.os.Parcelable

/** The two kinds of identity document the form accepts. */
enum class IdType { CITIZEN_ID, PASSPORT }

/**
 * A saved booking. BookingActivity builds it when the user presses "Save booking"
 * and sends it back to MainActivity inside the result Intent, so it is Parcelable too.
 * Kept in memory only (no files or database), as the assignment requires.
 */
data class Booking(
    val car: Car,
    val renterName: String,
    val phone: String,
    val email: String,
    val address: String,
    val idType: IdType,
    val idNumber: String,
    val licenceNumber: String,
    val days: Int,
    val totalCost: Int,
    val pickupDate: String,
    val returnDate: String
) : Parcelable {

    /** Rebuilds a Booking from a Parcel, in the same order writeToParcel wrote it. */
    constructor(parcel: Parcel) : this(
        Car(parcel),                                   // the car is written first, in place
        parcel.readString().orEmpty(),                 // renterName
        parcel.readString().orEmpty(),                 // phone
        parcel.readString().orEmpty(),                 // email
        parcel.readString().orEmpty(),                 // address
        IdType.valueOf(parcel.readString() ?: IdType.CITIZEN_ID.name), // idType
        parcel.readString().orEmpty(),                 // idNumber
        parcel.readString().orEmpty(),                 // licenceNumber
        parcel.readInt(),                              // days
        parcel.readInt(),                              // totalCost
        parcel.readString().orEmpty(),                 // pickupDate
        parcel.readString().orEmpty()                  // returnDate
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        car.writeToParcel(parcel, flags)
        parcel.writeString(renterName)
        parcel.writeString(phone)
        parcel.writeString(email)
        parcel.writeString(address)
        parcel.writeString(idType.name)
        parcel.writeString(idNumber)
        parcel.writeString(licenceNumber)
        parcel.writeInt(days)
        parcel.writeInt(totalCost)
        parcel.writeString(pickupDate)
        parcel.writeString(returnDate)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Booking> {
        override fun createFromParcel(parcel: Parcel): Booking = Booking(parcel)
        override fun newArray(size: Int): Array<Booking?> = arrayOfNulls(size)
    }
}