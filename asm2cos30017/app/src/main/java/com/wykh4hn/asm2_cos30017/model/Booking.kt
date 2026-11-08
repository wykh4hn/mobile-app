package com.wykh4hn.assigment_2.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** The two kinds of identity document the form accepts. */
enum class IdType { CITIZEN_ID, PASSPORT }

/**
 * A saved booking. BookingActivity builds it when the user presses "Save booking"
 * and sends it back to MainActivity inside the result Intent, so it is Parcelable too.
 * It is kept in memory only (no files or database), as the assignment requires.
 */
@Parcelize
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
) : Parcelable