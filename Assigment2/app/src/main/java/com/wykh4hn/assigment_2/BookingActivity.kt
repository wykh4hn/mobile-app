package com.wykh4hn.assigment_2

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.wykh4hn.assigment_2.logic.CreditCheck
import com.wykh4hn.assigment_2.logic.FieldError
import com.wykh4hn.assigment_2.logic.RentalRules
import com.wykh4hn.assigment_2.model.Booking
import com.wykh4hn.assigment_2.model.Car
import com.wykh4hn.assigment_2.model.IdType
import com.wykh4hn.assigment_2.util.ThemeHelper
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputLayout
import java.text.DateFormat
import java.util.Calendar

/**
 * Second screen: renter details, ID, number of days and a live price summary.
 *
 * It receives a Car (Parcelable) and the current balance from MainActivity.
 * "Save booking" checks every field and the credit rules; the screen cannot be left
 * with Save until everything is correct. Cancel, the Back arrow and the phone's Back
 * gesture all count as a cancellation and return RESULT_CANCELED.
 */
class BookingActivity : AppCompatActivity(), View.OnClickListener, Slider.OnChangeListener,
    MaterialButtonToggleGroup.OnButtonCheckedListener {

    companion object {
        /** Keys for the Intent extras, shared with MainActivity. */
        const val EXTRA_CAR = "com.wykh4hn.assigment_2.EXTRA_CAR"
        const val EXTRA_BALANCE = "com.wykh4hn.assigment_2.EXTRA_BALANCE"
        const val EXTRA_BOOKING = "com.wykh4hn.assigment_2.EXTRA_BOOKING"

        private const val TAG = "Checking"
    }

    private lateinit var car: Car
    private var balance = 0

    // Header and car details
    private lateinit var scrollBooking: ScrollView
    private lateinit var btnBack: Button
    private lateinit var txtBalance: TextView
    private lateinit var swDarkMode: MaterialSwitch
    private lateinit var imgCar: ImageView
    private lateinit var txtCategory: TextView
    private lateinit var txtCarName: TextView
    private lateinit var txtCarMeta: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var txtCarDetails: TextView

    // Form fields
    private lateinit var tilName: TextInputLayout
    private lateinit var tilPhone: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilAddress: TextInputLayout
    private lateinit var toggleIdType: MaterialButtonToggleGroup
    private lateinit var txtIdLabel: TextView
    private lateinit var tilIdNumber: TextInputLayout
    private lateinit var tilLicence: TextInputLayout

    // Rental period and summary
    private lateinit var txtPickupDate: TextView
    private lateinit var txtReturnDate: TextView
    private lateinit var txtDaysValue: TextView
    private lateinit var sliderDays: Slider
    private lateinit var txtCostLine: TextView
    private lateinit var txtCostValue: TextView
    private lateinit var txtBalanceAfter: TextView
    private lateinit var txtTotal: TextView
    private lateinit var txtCreditError: TextView
    private lateinit var cbTerms: CheckBox
    private lateinit var txtTermsError: TextView
    private lateinit var txtFormError: TextView
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_booking)
        // Keep the form clear of the system bars AND the keyboard, so fields are never hidden
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        // 1. Read what MainActivity put in the Intent
        val receivedCar = IntentCompat.getParcelableExtra(intent, EXTRA_CAR, Car::class.java)
        if (receivedCar == null) {
            Log.e(TAG, "BookingActivity opened without a car, closing")
            finish()
            return
        }
        car = receivedCar
        balance = intent.getIntExtra(EXTRA_BALANCE, 0)
        Log.d(TAG, "BookingActivity onCreate: ${car.fullName}, balance $balance")

        // 2. Connect the views and show the car
        findViews()
        showCarDetails()
        swDarkMode.isChecked = ThemeHelper.isDarkMode(this)
        txtPickupDate.text = formatDate(0)

        // 3. Listeners
        btnBack.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        swDarkMode.setOnClickListener(this)
        cbTerms.setOnClickListener(this)
        sliderDays.addOnChangeListener(this)
        toggleIdType.addOnButtonCheckedListener(this)
        // An error message disappears as soon as the user starts fixing that field
        listOf(tilName, tilPhone, tilEmail, tilAddress, tilIdNumber, tilLicence).forEach { layout ->
            layout.editText?.doAfterTextChanged { layout.error = null }
        }

        updateIdField()
        updateSummary()
    }

    /**
     * The Slider and the ID toggle restore their own values AFTER onCreate (Week 4 lifecycle),
     * so the parts of the screen that depend on them are redrawn here.
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        updateIdField()
        updateSummary()
        Log.d(TAG, "BookingActivity restored: ${selectedDays()} day(s)")
    }

    private fun findViews() {
        scrollBooking = findViewById(R.id.scrollBooking)
        btnBack = findViewById(R.id.btnBack)
        txtBalance = findViewById(R.id.txtBalance)
        swDarkMode = findViewById(R.id.swDarkMode)
        imgCar = findViewById(R.id.imgCar)
        txtCategory = findViewById(R.id.txtCategory)
        txtCarName = findViewById(R.id.txtCarName)
        txtCarMeta = findViewById(R.id.txtCarMeta)
        ratingBar = findViewById(R.id.ratingBar)
        txtCarDetails = findViewById(R.id.txtCarDetails)

        tilName = findViewById(R.id.tilName)
        tilPhone = findViewById(R.id.tilPhone)
        tilEmail = findViewById(R.id.tilEmail)
        tilAddress = findViewById(R.id.tilAddress)
        toggleIdType = findViewById(R.id.toggleIdType)
        txtIdLabel = findViewById(R.id.txtIdLabel)
        tilIdNumber = findViewById(R.id.tilIdNumber)
        tilLicence = findViewById(R.id.tilLicence)

        txtPickupDate = findViewById(R.id.txtPickupDate)
        txtReturnDate = findViewById(R.id.txtReturnDate)
        txtDaysValue = findViewById(R.id.txtDaysValue)
        sliderDays = findViewById(R.id.sliderDays)
        txtCostLine = findViewById(R.id.txtCostLine)
        txtCostValue = findViewById(R.id.txtCostValue)
        txtBalanceAfter = findViewById(R.id.txtBalanceAfter)
        txtTotal = findViewById(R.id.txtTotal)
        txtCreditError = findViewById(R.id.txtCreditError)
        cbTerms = findViewById(R.id.cbTerms)
        txtTermsError = findViewById(R.id.txtTermsError)
        txtFormError = findViewById(R.id.txtFormError)
        btnCancel = findViewById(R.id.btnCancel)
        btnSave = findViewById(R.id.btnSave)
    }

    /** The "further details" of the car that the assignment asks for on this screen. */
    private fun showCarDetails() {
        txtBalance.text = getString(R.string.balance_credits, balance)
        txtBalance.contentDescription = getString(R.string.cd_balance, balance)
        imgCar.setImageResource(car.imageRes)
        imgCar.contentDescription = getString(R.string.cd_car_photo, car.fullName)
        txtCategory.text = car.category
        txtCarName.text = car.fullName
        txtCarMeta.text = getString(R.string.car_meta, car.year, car.dailyCost)
        ratingBar.rating = car.rating
        ratingBar.contentDescription = getString(R.string.cd_rating, car.rating)
        txtCarDetails.text = getString(R.string.car_details, car.rating, car.kilometres)
    }

    // ===================== Events =====================

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnBack, R.id.btnCancel -> cancelBooking()
            R.id.btnSave -> saveBooking()
            R.id.swDarkMode -> {
                Log.d(TAG, "Dark mode switched ${if (swDarkMode.isChecked) "on" else "off"} (booking screen)")
                ThemeHelper.setDarkMode(swDarkMode.isChecked)
            }
            R.id.cbTerms -> if (cbTerms.isChecked) txtTermsError.isVisible = false
        }
    }

    /** Slider moved: recalculate the return date, total and credit check. */
    override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
        updateSummary()
    }

    /** Citizen ID / Passport switched: change the label, keyboard and length limit. */
    override fun onButtonChecked(group: MaterialButtonToggleGroup, checkedId: Int, isChecked: Boolean) {
        if (isChecked) {
            tilIdNumber.error = null
            updateIdField()
        }
    }

    // ===================== Leaving the screen =====================

    /**
     * Cancel or the Back arrow. The phone's own Back gesture does the same thing
     * automatically: finish() without setResult(RESULT_OK) returns RESULT_CANCELED.
     */
    private fun cancelBooking() {
        Log.d(TAG, "Booking cancelled on the booking screen")
        setResult(RESULT_CANCELED)
        finish()
    }

    /** Save: only leaves the screen when every check passes. */
    private fun saveBooking() {
        val problems = validateForm()
        if (problems.isNotEmpty()) {
            txtFormError.text = resources.getQuantityString(R.plurals.form_errors, problems.size, problems.size)
            txtFormError.isVisible = true
            scrollToView(problems.first())
            Log.d(TAG, "Save blocked: ${problems.size} problem(s)")
            return
        }
        txtFormError.isVisible = false

        val days = selectedDays()
        val booking = Booking(
            car = car,
            renterName = textOf(tilName),
            phone = textOf(tilPhone).replace(" ", ""),
            email = textOf(tilEmail),
            address = textOf(tilAddress),
            idType = selectedIdType(),
            idNumber = textOf(tilIdNumber),
            licenceNumber = textOf(tilLicence),
            days = days,
            totalCost = RentalRules.totalCost(car.dailyCost, days),
            pickupDate = formatDate(0),
            returnDate = formatDate(days)
        )
        // Personal details are deliberately NOT written to the log
        Log.d(TAG, "Saving booking: ${car.fullName}, $days day(s), ${booking.totalCost} credits")

        val result = Intent()
        result.putExtra(EXTRA_BOOKING, booking)
        setResult(RESULT_OK, result)
        finish()
    }

    // ===================== Checks =====================

    /**
     * Checks every field, shows a message under each wrong one, and returns the
     * views that need fixing in screen order. An empty list means the form is valid.
     */
    private fun validateForm(): List<View> {
        val problems = mutableListOf<View>()

        // Local helper: turn a FieldError into the right message on one field
        fun check(layout: TextInputLayout, error: FieldError?, requiredMessage: Int, invalidMessage: Int) {
            layout.error = when (error) {
                FieldError.REQUIRED -> getString(requiredMessage)
                FieldError.INVALID -> getString(invalidMessage)
                null -> null
            }
            if (error != null) problems.add(layout)
        }

        check(tilName, RentalRules.checkName(textOf(tilName)),
            R.string.error_name_required, R.string.error_name_invalid)
        check(tilPhone, RentalRules.checkPhone(textOf(tilPhone)),
            R.string.error_phone_required, R.string.error_phone_invalid)
        check(tilEmail, RentalRules.checkEmail(textOf(tilEmail)),
            R.string.error_email_invalid, R.string.error_email_invalid)

        val passport = selectedIdType() == IdType.PASSPORT
        check(tilIdNumber, RentalRules.checkIdNumber(selectedIdType(), textOf(tilIdNumber)),
            if (passport) R.string.error_passport_required else R.string.error_citizen_required,
            if (passport) R.string.error_passport_invalid else R.string.error_citizen_invalid)
        check(tilLicence, RentalRules.checkLicence(textOf(tilLicence)),
            R.string.error_licence_required, R.string.error_licence_invalid)

        val total = RentalRules.totalCost(car.dailyCost, selectedDays())
        if (RentalRules.checkCredit(total, balance) != CreditCheck.OK) problems.add(sliderDays)

        if (!cbTerms.isChecked) {
            txtTermsError.isVisible = true
            problems.add(cbTerms)
        }
        return problems
    }

    /** Recalculates the rental period and price every time the number of days changes. */
    private fun updateSummary() {
        val days = selectedDays()
        val total = RentalRules.totalCost(car.dailyCost, days)
        val daysText = resources.getQuantityString(R.plurals.days, days, days)

        txtDaysValue.text = daysText
        txtReturnDate.text = formatDate(days)
        txtCostLine.text = getString(R.string.summary_cost_line, car.dailyCost, daysText)
        txtCostValue.text = getString(R.string.credits_short, total)
        txtBalanceAfter.text = getString(R.string.credits_short, balance - total)
        txtTotal.text = getString(R.string.credits_long, total)

        when (RentalRules.checkCredit(total, balance)) {
            CreditCheck.OK -> txtCreditError.isVisible = false
            CreditCheck.OVER_RENTAL_LIMIT ->
                showCreditError(getString(R.string.error_over_limit, RentalRules.MAX_PER_RENTAL))
            CreditCheck.INSUFFICIENT_BALANCE ->
                showCreditError(getString(R.string.error_insufficient, total, balance))
        }
    }

    private fun showCreditError(message: String) {
        txtCreditError.text = message
        txtCreditError.isVisible = true
    }

    /** Label, keyboard type and maximum length follow the chosen document. */
    private fun updateIdField() {
        val editText = tilIdNumber.editText ?: return
        if (selectedIdType() == IdType.PASSPORT) {
            txtIdLabel.setText(R.string.label_passport_number)
            editText.setHint(R.string.hint_passport)
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
            editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(9))
        } else {
            txtIdLabel.setText(R.string.label_citizen_id)
            editText.setHint(R.string.hint_citizen_id)
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(12))
        }
    }

    // ===================== Small helpers =====================

    private fun selectedDays(): Int = sliderDays.value.toInt()

    private fun selectedIdType(): IdType =
        if (toggleIdType.checkedButtonId == R.id.btnPassport) IdType.PASSPORT else IdType.CITIZEN_ID

    private fun textOf(layout: TextInputLayout): String = layout.editText?.text?.toString()?.trim().orEmpty()

    /** A date [daysFromToday] days after today, written the way the phone's language expects. */
    private fun formatDate(daysFromToday: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, daysFromToday)
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.time)
    }

    /** Scrolls so [view] is visible near the top, and focuses it if it is a text field. */
    private fun scrollToView(view: View) {
        val rect = Rect()
        view.getDrawingRect(rect)
        scrollBooking.offsetDescendantRectToMyCoords(view, rect)
        scrollBooking.smoothScrollTo(0, (rect.top - resources.getDimensionPixelSize(R.dimen.touch_target)).coerceAtLeast(0))
        if (view is TextInputLayout) view.editText?.requestFocus()
    }
}
