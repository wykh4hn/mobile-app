package com.wykh4hn.asm2_cos30017.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wykh4hn.asm2_cos30017.R
import com.wykh4hn.asm2_cos30017.model.Car

/**
 * Horizontal list of favourite cars on the home screen (RecyclerView pattern from Week 7).
 *
 * @param isRented asks the ViewModel whether a car is rented, to show "Rented" instead of the price
 * @param onCarClick called when the user taps a favourite, so MainActivity can show that car
 */
class FavouriteAdapter(
    private val isRented: (Car) -> Boolean,
    private val onCarClick: (Car) -> Unit
) : RecyclerView.Adapter<FavouriteAdapter.ViewHolderClass>() {

    private var mList: List<Car> = emptyList()

    /** Replaces the list. It holds at most five cars, so redrawing every row is cheap. */
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Car>) {
        mList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_favourite, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val car = mList[position]
        val context = holder.itemView.context
        holder.image.setImageResource(car.imageRes)
        holder.name.text = car.fullName
        holder.status.text = if (isRented(car)) {
            context.getString(R.string.status_rented)
        } else {
            context.getString(R.string.price_per_day_short, car.dailyCost)
        }
        holder.itemView.setOnClickListener { onCarClick(car) }
    }

    override fun getItemCount(): Int = mList.size

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imgFavCar)
        val name: TextView = itemView.findViewById(R.id.txtFavName)
        val status: TextView = itemView.findViewById(R.id.txtFavStatus)
    }
}