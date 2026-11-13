package com.wykh4hn.assigment_2.logic

import com.wykh4hn.assigment_2.model.Car
import com.wykh4hn.assigment_2.model.IdType
import com.wykh4hn.assigment_2.model.SortOption

object RentalRules {
    const val STARTING_BALANCE = 500
    const val MAX_PER_RENTAL = 300

    fun totalCost(dailyCost: Int, days: Int): Int = dailyCost * days

    fun checkCredit(totalCost: Int, balance: Int): CreditCheck {
        return when {
            totalCost > MAX_PER_RENTAL -> CreditCheck.OVER_RENTAL_LIMIT
            totalCost > balance -> CreditCheck.INSUFFICIENT_BALANCE
            else -> CreditCheck.OK
        }
    }

    fun searchCars(cars: List<Car>, query: String): List<Car> {
        if (query.isBlank()) return cars
        val q = query.trim().lowercase()
        return cars.filter {
            it.brand.lowercase().contains(q) ||
                    it.model.lowercase().contains(q) ||
                    it.category.lowercase().contains(q) ||
                    it.fullName.lowercase().contains(q)
        }
    }

    fun sortCars(cars: List<Car>, sortOption: SortOption): List<Car> {
        return when (sortOption) {
            SortOption.DEFAULT -> cars
            SortOption.TOP_RATED -> cars.sortedByDescending { it.rating }
            SortOption.NEWEST_FIRST -> cars.sortedByDescending { it.year }
            SortOption.CHEAPEST_FIRST -> cars.sortedBy { it.dailyCost }
        }
    }

    fun checkName(name: String): FieldError? {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return FieldError.REQUIRED
        if (trimmed.length < 2 || !trimmed.all { it.isLetter() || it.isWhitespace() }) {
            return FieldError.INVALID
        }
        return null
    }

    fun checkPhone(phone: String): FieldError? {
        val cleaned = phone.replace(" ", "").trim()
        if (cleaned.isEmpty()) return FieldError.REQUIRED
        if (!cleaned.matches(Regex("^0\\d{9}$"))) return FieldError.INVALID
        return null
    }

    fun checkEmail(email: String): FieldError? {
        val trimmed = email.trim()
        if (trimmed.isEmpty()) return null
        if (!trimmed.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) {
            return FieldError.INVALID
        }
        return null
    }

    fun checkIdNumber(idType: IdType, idNumber: String): FieldError? {
        val trimmed = idNumber.trim()
        if (trimmed.isEmpty()) return FieldError.REQUIRED
        return if (idType == IdType.PASSPORT) {
            if (!trimmed.matches(Regex("^[A-Za-z0-9]{6,9}$"))) FieldError.INVALID else null
        } else {
            if (!trimmed.matches(Regex("^\\d{12}$"))) FieldError.INVALID else null
        }
    }

    fun checkLicence(licence: String): FieldError? {
        val trimmed = licence.trim()
        if (trimmed.isEmpty()) return FieldError.REQUIRED
        if (!trimmed.matches(Regex("^\\d{12}$"))) return FieldError.INVALID
        return null
    }
}
