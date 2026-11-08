package com.wykh4hn.assigment_2

import androidx.lifecycle.ViewModel
import com.wykh4hn.assigment_2.data.CarData
import com.wykh4hn.assigment_2.logic.CreditCheck
import com.wykh4hn.assigment_2.logic.RentalRules
import com.wykh4hn.assigment_2.model.Booking
import com.wykh4hn.assigment_2.model.Car
import com.wykh4hn.assigment_2.model.SortOption

/**
 * Holds every piece of app data in memory (Week 4: ViewModel to save state).
 *
 * Android destroys and recreates the Activity on rotation and when dark mode is
 * switched, but this object survives, so the balance, bookings, favourites,
 * search text and current car are not lost. Nothing is written to disk.
 */
class CarViewModel : ViewModel() {

    /** The five cars. Never modified: search, sort and renting only build new lists from it. */
    val allCars: List<Car> = CarData.cars

    /** Credits left. Only addBooking and cancelBooking may change it. */
    var balance: Int = RentalRules.STARTING_BALANCE
        private set

    /** Text currently typed in the search bar. */
    var query: String = ""

    /** Order chosen in the sort menu. */
    var sortOption: SortOption = SortOption.DEFAULT

    /** Position of the car shown on the card, inside displayedCars(). */
    var currentIndex: Int = 0

    private val rentedIds = mutableSetOf<Int>()
    private val favouriteIds = mutableSetOf<Int>()
    private val activeBookings = mutableListOf<Booking>()

    /** Read-only copy of the saved bookings, newest last. */
    val bookings: List<Booking>
        get() = activeBookings.toList()

    /** Cars the user can see now: not rented, matching the search, in the chosen order. */
    fun displayedCars(): List<Car> {
        val available = allCars.filter { it.id !in rentedIds }
        return RentalRules.sortCars(RentalRules.searchCars(available, query), sortOption)
    }

    /** The car on the card, or null when the list is empty. */
    fun currentCar(): Car? = displayedCars().getOrNull(currentIndex)

    /** Moves to the next car; after the last one it goes back to the first. */
    fun showNext() {
        val count = displayedCars().size
        if (count > 0) currentIndex = (currentIndex + 1) % count
    }

    /** Moves to the previous car; before the first one it goes to the last. */
    fun showPrevious() {
        val count = displayedCars().size
        if (count > 0) currentIndex = (currentIndex - 1 + count) % count
    }

    /** Keeps currentIndex valid after a car leaves the list (rented or filtered out). */
    fun keepIndexInRange() {
        val count = displayedCars().size
        currentIndex = currentIndex.coerceIn(0, maxOf(count - 1, 0))
    }

    fun isFavourite(car: Car): Boolean = car.id in favouriteIds

    /** Adds or removes [car] from favourites and returns the new state. */
    fun toggleFavourite(car: Car): Boolean {
        if (!favouriteIds.remove(car.id)) favouriteIds.add(car.id)
        return isFavourite(car)
    }

    /** Favourite cars in base-list order, including any that are rented right now. */
    fun favouriteCars(): List<Car> = allCars.filter { it.id in favouriteIds }

    fun isRented(car: Car): Boolean = car.id in rentedIds

    /**
     * Saves a booking: the car leaves the available list and the balance goes down.
     * The credit rules are checked again here, so a wrong booking can never be stored.
     * Returns false if the booking was refused.
     */
    fun addBooking(booking: Booking): Boolean {
        val check = RentalRules.checkCredit(booking.totalCost, balance)
        if (check != CreditCheck.OK || isRented(booking.car)) return false
        activeBookings.add(booking)
        rentedIds.add(booking.car.id)
        balance -= booking.totalCost
        keepIndexInRange()
        return true
    }

    /** Cancels a saved booking: the car is available again and the credits come back. */
    fun cancelBooking(booking: Booking): Boolean {
        if (!activeBookings.remove(booking)) return false
        rentedIds.remove(booking.car.id)
        balance += booking.totalCost
        return true
    }

    /** Moves the card to [car]. Returns false when the car is not in the visible list. */
    fun jumpTo(car: Car): Boolean {
        val index = displayedCars().indexOfFirst { it.id == car.id }
        if (index >= 0) currentIndex = index
        return index >= 0
    }
}