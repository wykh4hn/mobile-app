package com.wykh4hn.asm2_cos30017.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wykh4hn.asm2_cos30017.R
import com.wykh4hn.asm2_cos30017.model.Booking

/**
 * "My booking" list on the home screen (RecyclerView pattern from Week 7).
 *
 * @param onCancel called when "Cancel booking" is tapped on a row
 * @param onEmail called when "Email receipt" is tapped on a row
 */
class BookingAdapter(
    private val onCancel: (Booking) -> Unit,
    private val onEmail: (Booking) -> Unit
) : RecyclerView.Adapter<BookingAdapter.ViewHolderClass>() {

    private var mList: List<Booking> = emptyList()

    /** Replaces the list. There are at most five bookings, so redrawing every row is cheap. */
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Booking>) {
        mList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val booking = mList[position]
        val context = holder.itemView.context
        val daysText = context.resources.getQuantityString(R.plurals.days, booking.days, booking.days)

        holder.image.setImageResource(booking.car.imageRes)
        holder.carName.text = booking.car.fullName
        holder.summary.text = context.getString(R.string.booking_summary, daysText, booking.totalCost)
        holder.dates.text = context.getString(R.string.booking_dates, booking.pickupDate, booking.returnDate)
        holder.renter.text = context.getString(R.string.booking_renter, booking.renterName)

        // Screen readers hear which car each button belongs to
        holder.btnEmail.contentDescription = context.getString(R.string.cd_email_booking, booking.car.fullName)
        holder.btnCancel.contentDescription = context.getString(R.string.cd_cancel_booking, booking.car.fullName)
        holder.btnEmail.setOnClickListener { onEmail(booking) }
        holder.btnCancel.setOnClickListener { onCancel(booking) }
    }

    override fun getItemCount(): Int = mList.size

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imgBookingCar)
        val carName: TextView = itemView.findViewById(R.id.txtBookingCar)
        val summary: TextView = itemView.findViewById(R.id.txtBookingSummary)
        val dates: TextView = itemView.findViewById(R.id.txtBookingDates)
        val renter: TextView = itemView.findViewById(R.id.txtBookingRenter)
        val btnEmail: Button = itemView.findViewById(R.id.btnEmailBooking)
        val btnCancel: Button = itemView.findViewById(R.id.btnCancelBooking)
    }
}