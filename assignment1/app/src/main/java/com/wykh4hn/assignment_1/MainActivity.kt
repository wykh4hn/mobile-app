package com.wykh4hn.assignment_1

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var btnPerform: Button
    lateinit var btnDeduction: Button
    lateinit var btnReset: Button
    lateinit var txtScore: TextView
    lateinit var txtZone: TextView
    lateinit var txtElement: TextView
    lateinit var txtMessage: TextView
    lateinit var barProgress: ProgressBar

    var score = 0
    var element = 0
    var hasDeduction = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnPerform = findViewById(R.id.btnPerform)
        btnDeduction = findViewById(R.id.btnDeduction)
        btnReset = findViewById(R.id.btnReset)
        txtScore = findViewById(R.id.txtScore)
        txtZone = findViewById(R.id.txtZone)
        txtElement = findViewById(R.id.txtElement)
        txtMessage = findViewById(R.id.txtMessage)
        barProgress = findViewById(R.id.barProgress)

        btnPerform.setOnClickListener(this)
        btnDeduction.setOnClickListener(this)
        btnReset.setOnClickListener(this)

        Log.d("Checking", "Run onCreate")
        updateUI()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnPerform -> {
                element = element + 1
                score = score + getPoints(element)
                if (score > 20) score = 20
                Log.d("Checking", "Perform clicked, element=" + element + ", score=" + score)
            }
            R.id.btnDeduction -> {
                score = score - 2
                if (score < 0) score = 0
                hasDeduction = true
                Log.d("Checking", "Deduction clicked, score=" + score)
            }
            R.id.btnReset -> {
                score = 0
                element = 0
                hasDeduction = false
                Log.d("Checking", "Reset clicked")
            }
        }
        updateUI()
    }

    // Tra ve so diem cua element thu i theo nhom do kho
    fun getPoints(i: Int): Int {
        if (i <= 3) return 1
        else if (i <= 7) return 2
        else return 3
    }

    // Ve lai toan bo man hinh theo gia tri cua 3 bien
    fun updateUI() {
        txtScore.setText(score.toString())
        txtElement.setText(getString(R.string.element_label, element))
        barProgress.setProgress(element)

        if (element == 0) {
            txtZone.setText(R.string.zone_none)
            txtZone.setTextColor(getColor(R.color.text_secondary))
            txtScore.setTextColor(getColor(R.color.text_primary))
        } else if (element <= 3) {
            txtZone.setText(R.string.zone_basic)
            txtZone.setTextColor(getColor(R.color.zone_basic))
            txtScore.setTextColor(getColor(R.color.zone_basic))
        } else if (element <= 7) {
            txtZone.setText(R.string.zone_intermediate)
            txtZone.setTextColor(getColor(R.color.zone_intermediate))
            txtScore.setTextColor(getColor(R.color.zone_intermediate))
        } else {
            txtZone.setText(R.string.zone_advanced)
            txtZone.setTextColor(getColor(R.color.zone_advanced))
            txtScore.setTextColor(getColor(R.color.zone_advanced))
        }

        if (element == 10) {
            txtMessage.setText(getString(R.string.msg_complete, score))
        } else if (hasDeduction) {
            txtMessage.setText(R.string.msg_deduction)
        } else if (element == 0) {
            txtMessage.setText(R.string.msg_start)
        } else {
            txtMessage.setText(getString(R.string.msg_next, element + 1, getPoints(element + 1)))
        }

        btnPerform.isEnabled = (hasDeduction == false) && (element < 10)
        btnDeduction.isEnabled = (element > 0) && (hasDeduction == false) && (element < 10)

        // Lam mo nut dang bi khoa de nguoi dung nhin la biet
        if (btnPerform.isEnabled) btnPerform.alpha = 1f else btnPerform.alpha = 0.4f
        if (btnDeduction.isEnabled) btnDeduction.alpha = 1f else btnDeduction.alpha = 0.4f
    }
}