package com.wykh4hn.week_4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AdminActivity : AppCompatActivity() {

    private lateinit var txtUserInfo: TextView
    private lateinit var btnExitAdmin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtUserInfo = findViewById(R.id.txtUserInfo)
        btnExitAdmin = findViewById(R.id.btnExitAdmin)

        // ============================================================
        // 1. NHẬN THAM SỐ TỪ MAINACTIVITY (Login) GỬI SANG
        // Slide: val intent = getIntent() / val user = intent.getStringExtra("Username")
        // ============================================================
        val user = intent.getStringExtra("Username")
        val pass = intent.getStringExtra("Pass")
        txtUserInfo.text = "User: $user | Pass: $pass"

        // ============================================================
        // 2. NÚT EXIT: TRẢ DỮ LIỆU KẾT QUẢ VỀ CHO MAINACTIVITY
        // Slide: Exit button -> setResult(RESULT_OK, intent) -> finish()
        // ============================================================
        btnExitAdmin.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("Test1", "Hello")
            resultIntent.putExtra("Test2", "Value2")

            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}
