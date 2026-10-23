package com.wykh4hn.week_4

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var btnLogin: Button
    lateinit var txtUser: EditText
    lateinit var txtPass: EditText
    lateinit var btnExit: Button
    lateinit var txtResult: TextView

    // ============================================================
    // CÁCH 2: NHẬN KẾT QUẢ TRẢ VỀ TỪ ADMINACTIVITY (2-way)
    // Slide (dưới bên trái): registerForActivityResult(...)
    // ============================================================
    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val value = result.data?.getStringExtra("Test1")
            Toast.makeText(this, value, Toast.LENGTH_SHORT).show()
            txtResult.text = "Result: $value"
        }
    }

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
        txtResult = findViewById(R.id.txtResult)

        btnLogin.setOnClickListener(this)
        btnExit.setOnClickListener(this)

        Log.d("Checking", "run onCreate ")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnLogin -> {
                if (txtUser.text.toString() == "Nguyen Quy Khanh" && txtPass.text.toString() == "3011") {
                    Toast.makeText(this, "Login successfully", Toast.LENGTH_LONG).show()

                    // ============================================================
                    // CÁCH 1: Gọi Intent 1 chiều (không cần nhận kết quả về)
                    // Slide (trên bên trái):
                    // val intent = Intent(this, AdminActivity::class.java)
                    // intent.putExtra("Username", txtUser.text.toString())
                    // startActivity(intent)
                    // ============================================================

                    // ============================================================
                    // CÁCH 2: Gọi Intent có nhận kết quả về (2 chiều)
                    // Slide (dưới bên trái):
                    // getResult.launch(intent)
                    // ============================================================
                    val intent = Intent(this, AdminActivity::class.java)
                    intent.putExtra("Username", txtUser.text.toString())
                    intent.putExtra("Pass", txtPass.text.toString())
                    getResult.launch(intent)
                } else {
                    Toast.makeText(this, "Not correct", Toast.LENGTH_LONG).show()
                }
            }
            R.id.btnExit -> {
                Log.d("Checking", "run finish")
                finish()
            }
        }
    }

    override fun onStart() {
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
