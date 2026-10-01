package com.wykh4hn.week1
import android.os.Bundle
import androidx.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class testActivity : AppCompatActivity() {
    override fun onCreate(saveInstanceState: Bundle?) {
        super.onCreate(save)
        enableEdgetoEdge()
        setContentView(R.layout.text_activity)
    }

    private fun enableEdgetoEdge() {
        TODO("Not yet implemented")
    }
}