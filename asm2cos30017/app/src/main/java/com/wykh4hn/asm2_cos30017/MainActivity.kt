package com.wykh4hn.asm2_cos30017

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.IntentCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import com.wykh4hn.asm2_cos30017.adapter.BookingAdapter
import com.wykh4hn.asm2_cos30017.adapter.FavouriteAdapter
import com.wykh4hn.asm2_cos30017.model.Booking
import com.wykh4hn.asm2_cos30017.model.Car
import com.wykh4hn.asm2_cos30017.model.SortOption
import com.wykh4hn.asm2_cos30017.util.ThemeHelper
import kotlin.math.abs

/**
 * Home screen: one car at a time with Prev/Next, search, sort, favourites and bookings.
 *
 * Same pattern as the lectures and Assignment 1: views are found with findViewById,
 * every click goes through one onClick with when(v?.id), and every change ends with
 * updateUI(), which redraws the screen from the data in [CarViewModel].
 *
 * Instead of being broken down bc of the faded run button, i can just ctrl f9
 */
class MainActivity : AppCompatActivity(), View.OnClickListener, View.OnTouchListener,
    SearchView.OnQueryTextListener, AdapterView.OnItemSelectedListener {

    companion object {
        private const val TAG = "Checking"

        /** Minimum finger movement (dp) and speed (dp/s) that count as a swipe. */
        private const val SWIPE_MIN_DISTANCE_DP = 60
        private const val SWIPE_MIN_VELOCITY_DP = 100

        /** Size and spacing of the page dots, in dp. */
        private const val DOT_SIZE_DP = 8
        private const val DOT_MARGIN_DP = 4
    }

    private lateinit var viewModel: CarViewModel

    // Header
    private lateinit var txtBalance: TextView
    private lateinit var swDarkMode: MaterialSwitch
    private lateinit var searchView: SearchView
    private lateinit var spSort: Spinner
    private lateinit var scrollMain: ScrollView

    // Car card
    private lateinit var cardCar: MaterialCardView
    private lateinit var imgCar: ImageView
    private lateinit var btnFavourite: ImageButton
    private lateinit var txtCategory: TextView
    private lateinit var txtCarName: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var txtRating: TextView
    private lateinit var txtYear: TextView
    private lateinit var txtKm: TextView
    private lateinit var txtCost: TextView
    private lateinit var btnRent: Button
    private lateinit var txtEmptyCars: TextView

    // Prev / dots / Next
    private lateinit var layoutPager: LinearLayout
    private lateinit var btnPrev: Button
    private lateinit var btnNext: Button
    private lateinit var layoutDots: LinearLayout
    private lateinit var txtCounter: TextView

    // Favourites and bookings
    private lateinit var rvFavourites: RecyclerView
    private lateinit var txtFavouritesEmpty: TextView
    private lateinit var rvBookings: RecyclerView
    private lateinit var txtBookingsEmpty: TextView
    private lateinit var favouriteAdapter: FavouriteAdapter
    private lateinit var bookingAdapter: BookingAdapter

    private lateinit var gestureDetector: GestureDetector

    /**
     * Starts BookingActivity and receives its answer (Week 6: the replacement for the
     * deprecated startActivityForResult). Must be created before the Activity starts.
     */
    private val bookingLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            onBookingResult(result)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // Keep content clear of the status bar, navigation bar and keyboard
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            // displayCutout() = the camera hole, which sits at the side in landscape
            val types = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.ime()
            val bars = insets.getInsets(types)
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        Log.d(TAG, "MainActivity onCreate (recreated=${savedInstanceState != null})")

        // Week 4: the ViewModel keeps the data when the Activity is recreated
        viewModel = ViewModelProvider(this).get(CarViewModel::class.java)

        findViews()
        setUpSortMenu()
        setUpLists()
        gestureDetector = GestureDetector(this, CardGestureListener())

        // Show the real current mode before listening for clicks
        swDarkMode.isChecked = ThemeHelper.isDarkMode(this)

        btnPrev.setOnClickListener(this)
        btnNext.setOnClickListener(this)
        btnFavourite.setOnClickListener(this)
        btnRent.setOnClickListener(this)
        swDarkMode.setOnClickListener(this)
        imgCar.setOnTouchListener(this)
        searchView.setOnQueryTextListener(this)

        updateUI()
    }

    private fun findViews() {
        txtBalance = findViewById(R.id.txtBalance)
        swDarkMode = findViewById(R.id.swDarkMode)
        searchView = findViewById(R.id.searchView)
        spSort = findViewById(R.id.spSort)
        scrollMain = findViewById(R.id.scrollMain)

        cardCar = findViewById(R.id.cardCar)
        imgCar = findViewById(R.id.imgCar)
        btnFavourite = findViewById(R.id.btnFavourite)
        txtCategory = findViewById(R.id.txtCategory)
        txtCarName = findViewById(R.id.txtCarName)
        ratingBar = findViewById(R.id.ratingBar)
        txtRating = findViewById(R.id.txtRating)
        txtYear = findViewById(R.id.txtYear)
        txtKm = findViewById(R.id.txtKm)
        txtCost = findViewById(R.id.txtCost)
        btnRent = findViewById(R.id.btnRent)
        txtEmptyCars = findViewById(R.id.txtEmptyCars)

        layoutPager = findViewById(R.id.layoutPager)
        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        layoutDots = findViewById(R.id.layoutDots)
        txtCounter = findViewById(R.id.txtCounter)

        rvFavourites = findViewById(R.id.rvFavourites)
        txtFavouritesEmpty = findViewById(R.id.txtFavouritesEmpty)
        rvBookings = findViewById(R.id.rvBookings)
        txtBookingsEmpty = findViewById(R.id.txtBookingsEmpty)
    }

    /** Sort menu: a Spinner filled by an ArrayAdapter (Week 7) from R.array.sort_options. */
    private fun setUpSortMenu() {
        val adapter = ArrayAdapter.createFromResource(this, R.array.sort_options, R.layout.item_sort_selected)
        adapter.setDropDownViewResource(R.layout.item_sort_dropdown)
        spSort.adapter = adapter
        spSort.setSelection(viewModel.sortOption.ordinal, false)
        spSort.onItemSelectedListener = this
    }

    /** Favourites (horizontal) and bookings (vertical) lists, RecyclerView from Week 7. */
    private fun setUpLists() {
        favouriteAdapter = FavouriteAdapter(
            isRented = { car -> viewModel.isRented(car) },
            onCarClick = { car -> showFavourite(car) }
        )
        rvFavourites.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvFavourites.adapter = favouriteAdapter

        bookingAdapter = BookingAdapter(
            onCancel = { booking -> confirmCancel(booking) },
            onEmail = { booking -> emailReceipt(booking) }
        )
        rvBookings.layoutManager = LinearLayoutManager(this)
        rvBookings.adapter = bookingAdapter
    }

    // ===================== Clicks =====================

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnNext -> viewModel.showNext()
            R.id.btnPrev -> viewModel.showPrevious()
            R.id.btnFavourite -> toggleFavourite()
            R.id.btnRent -> openBooking()
            R.id.swDarkMode -> {
                Log.d(TAG, "Dark mode switched ${if (swDarkMode.isChecked) "on" else "off"}")
                ThemeHelper.setDarkMode(swDarkMode.isChecked)
            }
        }
        updateUI()
    }

    private fun toggleFavourite() {
        val car = viewModel.currentCar() ?: return
        val isFavouriteNow = viewModel.toggleFavourite(car)
        Log.d(TAG, "${car.fullName} favourite = $isFavouriteNow")
    }

    /** Rent: send the car (as a Parcelable) and the balance to BookingActivity. */
    private fun openBooking() {
        val car = viewModel.currentCar() ?: return
        val intent = Intent(this, BookingActivity::class.java)
        intent.putExtra(BookingActivity.EXTRA_CAR, car)
        intent.putExtra(BookingActivity.EXTRA_BALANCE, viewModel.balance)
        Log.d(TAG, "Rent pressed: ${car.fullName}, balance ${viewModel.balance}")
        bookingLauncher.launch(intent)
    }

    /**
     * Answer from BookingActivity. RESULT_OK + a Booking means "Save" was pressed.
     * Anything else (Cancel, the Back button, the phone's Back gesture) is a cancellation.
     */
    private fun onBookingResult(result: ActivityResult) {
        val data = result.data
        val booking = if (data != null) {
            IntentCompat.getParcelableExtra(data, BookingActivity.EXTRA_BOOKING, Booking::class.java)
        } else {
            null
        }

        if (result.resultCode == RESULT_OK && booking != null && viewModel.addBooking(booking)) {
            Log.d(TAG, "Booked ${booking.car.fullName}: ${booking.days} day(s), ${booking.totalCost} credits, balance now ${viewModel.balance}")
            Toast.makeText(this, getString(R.string.toast_booked, booking.car.fullName, booking.totalCost), Toast.LENGTH_LONG).show()
        } else {
            Log.d(TAG, "Booking not made (resultCode=${result.resultCode})")
            Toast.makeText(this, R.string.toast_not_booked, Toast.LENGTH_SHORT).show()
        }
        updateUI()
    }

    /** Cancelling a saved booking asks first, because it gives the car away again. */
    private fun confirmCancel(booking: Booking) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_cancel_title)
            .setMessage(getString(R.string.dialog_cancel_message, booking.car.fullName, booking.totalCost))
            .setNegativeButton(R.string.dialog_keep, null)
            .setPositiveButton(R.string.dialog_cancel_confirm) { _, _ -> cancelBooking(booking) }
            .show()
    }

    private fun cancelBooking(booking: Booking) {
        if (viewModel.cancelBooking(booking)) {
            Log.d(TAG, "Cancelled ${booking.car.fullName}, refunded ${booking.totalCost}, balance now ${viewModel.balance}")
            Toast.makeText(this, getString(R.string.toast_booking_cancelled, booking.car.fullName, booking.totalCost), Toast.LENGTH_LONG).show()
            updateUI()
        }
    }

    /** Extra feature: an implicit Intent (Week 6) opens the user's email app with a receipt. */
    private fun emailReceipt(booking: Booking) {
        val daysText = resources.getQuantityString(R.plurals.days, booking.days, booking.days)
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = "mailto:".toUri()
        if (booking.email.isNotBlank()) intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(booking.email))
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject, booking.car.fullName))
        intent.putExtra(
            Intent.EXTRA_TEXT,
            getString(R.string.email_body, booking.renterName, booking.car.fullName,
                booking.pickupDate, booking.returnDate, daysText, booking.totalCost)
        )
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.d(TAG, "No email app: ${e.message}")
            Toast.makeText(this, R.string.toast_no_email_app, Toast.LENGTH_SHORT).show()
        }
    }

    /** Tapping a favourite shows that car on the main card (quick access). */
    private fun showFavourite(car: Car) {
        if (viewModel.isRented(car)) {
            Toast.makeText(this, getString(R.string.toast_favourite_rented, car.fullName), Toast.LENGTH_SHORT).show()
            return
        }
        searchView.setQuery("", false) // clear the search so the car is in the list
        viewModel.query = ""
        viewModel.jumpTo(car)
        updateUI()
        scrollMain.smoothScrollTo(0, 0)
    }

    // ===================== Search and sort =====================

    override fun onQueryTextChange(newText: String?): Boolean {
        val text = newText.orEmpty()
        // Only react to real changes: the SearchView also "types" its old text back after recreation
        if (text != viewModel.query) {
            viewModel.query = text
            viewModel.currentIndex = 0
            Log.d(TAG, "Search \"$text\" -> ${viewModel.displayedCars().size} car(s)")
            updateUI()
        }
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        searchView.clearFocus() // hide the keyboard so the result is visible
        return true
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val option = SortOption.entries[position]
        if (option != viewModel.sortOption) {
            viewModel.sortOption = option
            viewModel.currentIndex = 0
            Log.d(TAG, "Sort by $option")
            updateUI()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) = Unit

    // ===================== Swipe and long press on the photo (extra) =====================

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event == null) return false
        if (event.action == MotionEvent.ACTION_UP) v?.performClick()
        return gestureDetector.onTouchEvent(event)
    }

    private inner class CardGestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean = true

        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (e1 == null) return false
            val density = resources.displayMetrics.density
            val distanceX = e2.x - e1.x
            if (abs(distanceX) < SWIPE_MIN_DISTANCE_DP * density ||
                abs(velocityX) < SWIPE_MIN_VELOCITY_DP * density
            ) return false

            if (distanceX < 0) viewModel.showNext() else viewModel.showPrevious()
            Log.d(TAG, "Swipe ${if (distanceX < 0) "left (next)" else "right (previous)"}")
            updateUI()
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            imgCar.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            toggleFavourite()
            updateUI()
        }
    }

    // ===================== Drawing the screen =====================

    /** Redraws everything from the ViewModel. Called after every change. */
    private fun updateUI() {
        viewModel.keepIndexInRange()
        txtBalance.text = getString(R.string.balance_credits, viewModel.balance)
        txtBalance.contentDescription = getString(R.string.cd_balance, viewModel.balance)

        val cars = viewModel.displayedCars()
        val car = cars.getOrNull(viewModel.currentIndex)
        if (car == null) showNoCars() else showCarCard(car, cars.size)

        val favourites = viewModel.favouriteCars()
        favouriteAdapter.updateList(favourites)
        rvFavourites.isVisible = favourites.isNotEmpty()
        txtFavouritesEmpty.isVisible = favourites.isEmpty()

        val bookings = viewModel.bookings
        bookingAdapter.updateList(bookings)
        rvBookings.isVisible = bookings.isNotEmpty()
        txtBookingsEmpty.isVisible = bookings.isEmpty()
    }

    /** Car details are shown under the image, as the assignment asks. */
    private fun showCarCard(car: Car, total: Int) {
        cardCar.isVisible = true
        layoutPager.isVisible = true
        txtCounter.isVisible = true
        txtEmptyCars.isVisible = false

        imgCar.setImageResource(car.imageRes)
        imgCar.contentDescription = getString(R.string.cd_car_photo, car.fullName)
        txtCategory.text = car.category
        txtCarName.text = car.fullName
        ratingBar.rating = car.rating
        ratingBar.contentDescription = getString(R.string.cd_rating, car.rating)
        txtRating.text = getString(R.string.rating_value, car.rating)
        txtYear.text = car.year.toString()
        txtKm.text = getString(R.string.km_value, car.kilometres)
        txtCost.text = car.dailyCost.toString()

        val favourite = viewModel.isFavourite(car)
        btnFavourite.setImageResource(if (favourite) R.drawable.ic_favorite else R.drawable.ic_favorite_border)
        btnFavourite.contentDescription = getString(
            if (favourite) R.string.cd_remove_favourite else R.string.cd_add_favourite, car.fullName
        )

        txtCounter.text = getString(R.string.counter, viewModel.currentIndex + 1, total)
        btnPrev.isEnabled = total > 1
        btnNext.isEnabled = total > 1
        drawDots(total)
    }

    /** Empty state: the search found nothing, or every car is rented. */
    private fun showNoCars() {
        cardCar.isVisible = false
        layoutPager.isVisible = false
        txtCounter.isVisible = false
        txtEmptyCars.isVisible = true
        txtEmptyCars.text = if (viewModel.query.isNotBlank()) {
            getString(R.string.empty_search, viewModel.query.trim())
        } else {
            getString(R.string.empty_all_rented)
        }
    }

    /** One small dot per car; the current car's dot is pink. */
    private fun drawDots(total: Int) {
        layoutDots.removeAllViews()
        val density = resources.displayMetrics.density
        val size = (DOT_SIZE_DP * density).toInt()
        val margin = (DOT_MARGIN_DP * density).toInt()
        for (i in 0 until total) {
            val dot = View(this)
            val params = LinearLayout.LayoutParams(size, size)
            params.setMargins(margin, 0, margin, 0)
            dot.layoutParams = params
            dot.setBackgroundResource(if (i == viewModel.currentIndex) R.drawable.dot_active else R.drawable.dot_inactive)
            layoutDots.addView(dot)
        }
    }
}