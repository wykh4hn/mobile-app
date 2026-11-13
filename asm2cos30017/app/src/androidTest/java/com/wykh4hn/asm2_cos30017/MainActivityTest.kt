package com.wykh4hn.asm2_cos30017

import androidx.test.espresso.Espresso.onData
import android.content.pm.ActivityInfo
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.`is`
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests with Espresso. They need a running emulator.
 * RatingBars are not tested here: the assignment notes they are hard to test with Espresso.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    // ----- Small helpers so each test reads like the steps a user takes -----

    private fun tap(id: Int) {
        onView(withId(id)).perform(scrollTo(), click())
    }

    private fun type(id: Int, text: String) {
        onView(withId(id)).perform(scrollTo(), replaceText(text), closeSoftKeyboard())
    }

    private fun assertText(id: Int, text: String) {
        onView(withId(id)).check(matches(withText(text)))
    }

    private fun assertCounter(position: Int, total: Int) {
        onView(withId(R.id.txtCounter)).check(matches(withText(containsString("$position/$total"))))
    }

    private fun search(text: String) {
        onView(withId(androidx.appcompat.R.id.search_src_text)).perform(replaceText(text), closeSoftKeyboard())
    }

    /** Turns the screen. The Activity is recreated and Android loads res/layout-land. */
    private fun rotate(orientation: Int) {
        activityRule.scenario.onActivity { it.requestedOrientation = orientation }
    }


    // ----- Tests -----

    @Test
    fun nextButton_goesThroughAllFiveCarsAndWrapsAround() {
        assertCounter(1, 5)
        repeat(4) { tap(R.id.btnNext) }
        assertCounter(5, 5)
        tap(R.id.btnNext)
        assertCounter(1, 5)
    }

    @Test
    fun search_findsCarByModel() {
        search("corolla")
        assertText(R.id.txtCarName, "Toyota Corolla")
        assertCounter(1, 1)
    }

    @Test
    fun search_withNoMatch_showsEmptyMessage() {
        search("zzz")
        onView(withId(R.id.txtEmptyCars)).check(matches(allOf(isDisplayed(), withText(containsString("zzz")))))
    }

    @Test
    fun sortByCost_showsCheapestCarFirst() {
        onView(withId(R.id.spSort)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Cheapest first"))).perform(click())
        assertText(R.id.txtCarName, "Honda City")
    }

    @Test
    fun saveWithEmptyForm_staysOnBookingScreenAndShowsErrors() {
        tap(R.id.btnRent)
        tap(R.id.btnSave)
        onView(withId(R.id.txtFormError)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.tilName)).check(matches(isDisplayed()))
    }

    @Test
    fun validBooking_lowersBalanceAndAppearsInMyBooking() {
        tap(R.id.btnRent) // first car: Toyota Corolla, 45 credits/day, 1 day by default
        type(R.id.etName, "Nguyen Van A")
        type(R.id.etPhone, "0901234567")
        type(R.id.etIdNumber, "012345678901")
        type(R.id.etLicence, "123456789012")
        tap(R.id.cbTerms)
        tap(R.id.btnSave)

        assertText(R.id.txtBalance, "455 credits")
        assertCounter(1, 4)
        onView(withId(R.id.rvBookings)).perform(scrollTo())
            .check(matches(hasDescendant(withText("Toyota Corolla"))))
    }

    @Test
    fun backButton_cancelsBookingAndKeepsBalance() {
        tap(R.id.btnRent)
        pressBack()
        assertText(R.id.txtBalance, "500 credits")
        assertText(R.id.txtCarName, "Toyota Corolla")
    }

    @Test
    fun rotateToLandscapeAndBack_keepsCarFavouriteAndBalance() {
        tap(R.id.btnNext)
        tap(R.id.btnNext)                                    // car 3 of 5
        tap(R.id.btnFavourite)

        rotate(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)    // landscape layout
        assertCounter(3, 5)
        assertText(R.id.txtBalance, "500 credits")
        onView(withId(R.id.rvFavourites)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        rotate(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)     // back to portrait
        assertCounter(3, 5)
    }

}