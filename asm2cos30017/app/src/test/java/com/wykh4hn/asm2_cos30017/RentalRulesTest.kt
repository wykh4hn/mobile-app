package com.wykh4hn.asm2_cos30017

import com.wykh4hn.asm2_cos30017.data.CarData
import com.wykh4hn.asm2_cos30017.logic.CreditCheck
import com.wykh4hn.asm2_cos30017.logic.FieldError
import com.wykh4hn.asm2_cos30017.logic.RentalRules
import com.wykh4hn.asm2_cos30017.model.IdType
import com.wykh4hn.asm2_cos30017.model.SortOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Unit tests for search, sort, the credit rules and form checks (no emulator needed). */
class RentalRulesTest {

    private val cars = CarData.cars

    // ----- Search -----

    @Test
    fun search_ignoresCaseAndMatchesModel() {
        val result = RentalRules.searchCars(cars, "COROLLA")
        assertEquals(listOf("Toyota Corolla"), result.map { it.fullName })
    }

    @Test
    fun search_matchesMakerName() {
        val result = RentalRules.searchCars(cars, "tesla")
        assertEquals(listOf("Tesla Model 3"), result.map { it.fullName })
    }

    @Test
    fun search_blankQuery_returnsEveryCar() {
        assertEquals(cars, RentalRules.searchCars(cars, "   "))
    }

    @Test
    fun search_noMatch_returnsEmptyList() {
        assertEquals(emptyList<Any>(), RentalRules.searchCars(cars, "bmw"))
    }

    // ----- Sort -----

    @Test
    fun sortByRating_highestFirst() {
        val ids = RentalRules.sortCars(cars, SortOption.RATING).map { it.id }
        assertEquals(listOf(2, 4, 1, 5, 3), ids) // 4.8, 4.6, 4.5, 4.4, 4.2
    }

    @Test
    fun sortByYear_newestFirst() {
        val ids = RentalRules.sortCars(cars, SortOption.YEAR).map { it.id }
        assertEquals(listOf(5, 2, 4, 1, 3), ids) // 2024, 2023, 2022, 2021, 2020
    }

    @Test
    fun sortByCost_cheapestFirst() {
        val ids = RentalRules.sortCars(cars, SortOption.COST).map { it.id }
        assertEquals(listOf(3, 1, 4, 2, 5), ids) // 35, 45, 60, 70, 80
    }

    @Test
    fun sorting_doesNotChangeTheBaseList() {
        val before = cars.map { it.id }
        RentalRules.sortCars(cars, SortOption.COST)
        RentalRules.sortCars(cars, SortOption.RATING)
        assertEquals(before, cars.map { it.id })
    }

    // ----- Credit -----

    @Test
    fun totalCost_isDailyCostTimesDays() {
        assertEquals(350, RentalRules.totalCost(70, 5))
    }

    @Test
    fun credit_exactly400_isAllowed() {
        assertEquals(CreditCheck.OK, RentalRules.checkCredit(400, 500))
    }

    @Test
    fun credit_over400_isRefused() {
        assertEquals(CreditCheck.OVER_RENTAL_LIMIT, RentalRules.checkCredit(401, 500))
    }

    @Test
    fun credit_moreThanBalance_isRefused() {
        assertEquals(CreditCheck.INSUFFICIENT_BALANCE, RentalRules.checkCredit(180, 150))
    }

    // ----- Form fields -----

    @Test
    fun name_acceptsVietnameseLetters_rejectsDigits() {
        assertNull(RentalRules.checkName("Nguyễn Văn A"))
        assertEquals(FieldError.INVALID, RentalRules.checkName("A1"))
        assertEquals(FieldError.REQUIRED, RentalRules.checkName("   "))
    }

    @Test
    fun phone_needsTenDigitsStartingWithZero() {
        assertNull(RentalRules.checkPhone("0901234567"))
        assertNull(RentalRules.checkPhone("090 123 4567"))
        assertEquals(FieldError.INVALID, RentalRules.checkPhone("901234567"))
        assertEquals(FieldError.REQUIRED, RentalRules.checkPhone(""))
    }

    @Test
    fun email_isOptionalButMustLookValid() {
        assertNull(RentalRules.checkEmail(""))
        assertNull(RentalRules.checkEmail("you@mail.com"))
        assertEquals(FieldError.INVALID, RentalRules.checkEmail("you@mail"))
    }

    @Test
    fun citizenId_needsExactlyTwelveDigits() {
        assertNull(RentalRules.checkIdNumber(IdType.CITIZEN_ID, "012345678901"))
        assertEquals(FieldError.INVALID, RentalRules.checkIdNumber(IdType.CITIZEN_ID, "12345"))
        assertEquals(FieldError.REQUIRED, RentalRules.checkIdNumber(IdType.CITIZEN_ID, ""))
    }

    @Test
    fun passport_needsSixToNineLettersOrDigits() {
        assertNull(RentalRules.checkIdNumber(IdType.PASSPORT, "C1234567"))
        assertEquals(FieldError.INVALID, RentalRules.checkIdNumber(IdType.PASSPORT, "C12"))
        assertEquals(FieldError.INVALID, RentalRules.checkIdNumber(IdType.PASSPORT, "C123-4567"))
    }

    @Test
    fun licence_needsExactlyTwelveDigits() {
        assertNull(RentalRules.checkLicence("123456789012"))
        assertEquals(FieldError.INVALID, RentalRules.checkLicence("1234567890"))
    }
}