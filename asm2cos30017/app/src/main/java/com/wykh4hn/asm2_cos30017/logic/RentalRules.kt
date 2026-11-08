package com.wykh4hn.assigment_2.logic

import com.wykh4hn.assigment_2.model.Car
import com.wykh4hn.assigment_2.model.IdType
import com.wykh4hn.assigment_2.model.SortOption

/** Result of checking a rental's total cost against the credit rules. */
enum class CreditCheck { OK, OVER_RENTAL_LIMIT, INSUFFICIENT_BALANCE }

/** Why a form field was rejected. */
enum class FieldError { REQUIRED, INVALID }

/**
 * The app's rules, written in plain Kotlin with no Android classes,
 * so they can be checked quickly with JUnit (see RentalRulesTest).
 */
object RentalRules {

    /** Credits every user starts with. */
    const val STARTING_BALANCE = 500

    /** No single rental may cost more than this. */
    const val MAX_PER_RENTAL = 400

    private val PHONE = Regex("^0\\d{9}$")
    private val EMAIL = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val TWELVE_DIGITS = Regex("^\\d{12}$")
    private val PASSPORT = Regex("^[A-Za-z0-9]{6,9}$")

    /**
     * Returns the cars whose maker or model contains [query], ignoring upper/lower case.
     * A blank query returns the list unchanged.
     */
    fun searchCars(cars: List<Car>, query: String): List<Car> {
        val text = query.trim()
        if (text.isEmpty()) return cars
        return cars.filter { car ->
            car.name.contains(text, ignoreCase = true) ||
                    car.model.contains(text, ignoreCase = true) ||
                    car.fullName.contains(text, ignoreCase = true)
        }
    }

    /**
     * Returns a NEW list in the chosen order. sortedBy / sortedByDescending never
     * change the list they are called on, so the base data stays as it was.
     */
    fun sortCars(cars: List<Car>, option: SortOption): List<Car> = when (option) {
        SortOption.DEFAULT -> cars
        SortOption.RATING -> cars.sortedByDescending { it.rating }
        SortOption.YEAR -> cars.sortedByDescending { it.year }
        SortOption.COST -> cars.sortedBy { it.dailyCost }
    }

    /** Price of a rental: daily cost times number of days. */
    fun totalCost(dailyCost: Int, days: Int): Int = dailyCost * days

    /**
     * Applies both credit rules: one rental may not exceed [MAX_PER_RENTAL],
     * and it may not cost more than the user's current [balance].
     */
    fun checkCredit(total: Int, balance: Int): CreditCheck = when {
        total > MAX_PER_RENTAL -> CreditCheck.OVER_RENTAL_LIMIT
        total > balance -> CreditCheck.INSUFFICIENT_BALANCE
        else -> CreditCheck.OK
    }

    /** Full name: required, at least 2 characters, letters (any language), spaces, . ' - only. */
    fun checkName(name: String): FieldError? {
        val text = name.trim()
        return when {
            text.isEmpty() -> FieldError.REQUIRED
            text.length < 2 || !text.all { it.isLetter() || it in " .'-" } -> FieldError.INVALID
            else -> null
        }
    }

    /** Vietnamese mobile number: 10 digits starting with 0. Spaces are ignored. */
    fun checkPhone(phone: String): FieldError? = checkPattern(phone.replace(" ", ""), PHONE)

    /** Email is optional, but if something is typed it must look like an email address. */
    fun checkEmail(email: String): FieldError? {
        val text = email.trim()
        return if (text.isEmpty() || EMAIL.matches(text)) null else FieldError.INVALID
    }

    /** Citizen ID: exactly 12 digits. Passport: 6 to 9 letters or digits. */
    fun checkIdNumber(type: IdType, number: String): FieldError? =
        checkPattern(number.trim(), if (type == IdType.CITIZEN_ID) TWELVE_DIGITS else PASSPORT)

    /** Driver licence number: exactly 12 digits. */
    fun checkLicence(number: String): FieldError? = checkPattern(number.trim(), TWELVE_DIGITS)

    private fun checkPattern(value: String, pattern: Regex): FieldError? = when {
        value.isEmpty() -> FieldError.REQUIRED
        !pattern.matches(value) -> FieldError.INVALID
        else -> null
    }
}