package com.wykh4hn.asm2_cos30017

import com.wykh4hn.asm2_cos30017.model.Booking
import com.wykh4hn.asm2_cos30017.model.Car
import com.wykh4hn.asm2_cos30017.model.IdType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Unit tests for renting, cancelling, favourites and the Prev/Next position. */
class CarViewModelTest {

    private fun bookingFor(car: Car, days: Int) = Booking(
        car = car, renterName = "Test User", phone = "0901234567", email = "",
        address = "", idType = IdType.CITIZEN_ID, idNumber = "012345678901",
        licenceNumber = "123456789012", days = days, totalCost = car.dailyCost * days,
        pickupDate = "today", returnDate = "later"
    )

    @Test
    fun renting_lowersBalanceAndHidesTheCar() {
        val vm = CarViewModel()
        val tesla = vm.allCars.first { it.model == "Model 3" }

        assertTrue(vm.addBooking(bookingFor(tesla, 5)))     // 70 x 5 = 350

        assertEquals(150, vm.balance)
        assertFalse(vm.displayedCars().contains(tesla))
        assertEquals(4, vm.displayedCars().size)
    }

    @Test
    fun secondRental_overRemainingBalance_isRefused() {
        val vm = CarViewModel()
        val tesla = vm.allCars.first { it.model == "Model 3" }
        val mazda = vm.allCars.first { it.model == "CX-5" }
        vm.addBooking(bookingFor(tesla, 5))                  // balance 150

        assertFalse(vm.addBooking(bookingFor(mazda, 3)))     // 180 > 150
        assertEquals(150, vm.balance)
    }

    @Test
    fun cancelling_givesCreditsAndCarBack() {
        val vm = CarViewModel()
        val booking = bookingFor(vm.allCars[0], 3)
        vm.addBooking(booking)

        assertTrue(vm.cancelBooking(booking))

        assertEquals(500, vm.balance)
        assertEquals(5, vm.displayedCars().size)
    }

    @Test
    fun next_afterLastCar_goesBackToFirst() {
        val vm = CarViewModel()
        repeat(5) { vm.showNext() }
        assertEquals(0, vm.currentIndex)
        vm.showPrevious()
        assertEquals(4, vm.currentIndex)
    }

    @Test
    fun favourite_toggleOnAndOff() {
        val vm = CarViewModel()
        val car = vm.allCars[2]
        assertTrue(vm.toggleFavourite(car))
        assertEquals(listOf(car), vm.favouriteCars())
        assertFalse(vm.toggleFavourite(car))
        assertTrue(vm.favouriteCars().isEmpty())
    }
}