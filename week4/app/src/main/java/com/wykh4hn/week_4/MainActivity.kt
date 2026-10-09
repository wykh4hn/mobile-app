package com.wykh4hn.week_4

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var btnLogin: Button
    lateinit var txtUser: EditText
    lateinit var txtPass: EditText
    lateinit var btnExit: Button
    lateinit var txtResult: TextView
    var result = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnLogin = findViewById(R.id.btnLogin)
        btnExit = findViewById(R.id.btnExit)
        txtUser = findViewById(R.id.txtUser)
        txtPass = findViewById(R.id.txtPass)

        btnLogin.setOnClickListener(this)
        btnExit.setOnClickListener(this)

        txtResult = findViewById(R.id.txtResult)

        Log.d("Checking", "run onCreate ")
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnLogin -> {
                if (txtUser.text.toString() == "Nguyen Quy Khanh" && txtPass.text.toString() == "3011") {
                    Toast.makeText(this, "Login successfully", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Not correct", Toast.LENGTH_LONG).show()
                }
                txtResult.setText(result)
            }
            R.id.btnExit -> {
                Log.d("Checking", "run finish")
                finish()
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        Log.d("Checking", "run onStart: ")
    }
    override fun onResume() {
        super.onResume()
        Log.d("Checking", "Run onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Checking", "Run onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Checking", "Run onStop")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("Checking", "Run onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Checking", "Run onDestroy")
    }
}