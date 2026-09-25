package com.wykh4hn.week1

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController

import com.google.android.material.snackbar.Snackbar
import com.wykh4hn.week1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    lateinit var button3: Button
    lateinit var nameKhanh: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // 1. Tạo binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        // 2. Đưa layout lên màn hình
        setContentView(binding.root)
        // 3. Sau setContentView mới được findViewById
        button3 = findViewById(R.id.button3)
        nameKhanh = findViewById(R.id.nameKhanh)

        // 4. Gán listener
        button3.setOnClickListener(this)

        button3.setOnClickListener {
            var text = nameKhanh.text.toString()
            nameKhanh.setText("Hello $text")
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(
                R.id.nav_host_fragment_content_main
            ) as NavHostFragment

        val navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(navController.graph)

        setupActionBarWithNavController(
            navController,
            appBarConfiguration
        )

        binding.fab.setOnClickListener { view ->
            Snackbar.make(
                view,
                "Replace with your own action",
                Snackbar.LENGTH_LONG
            )
                .setAction("Action", null)
                .setAnchorView(R.id.fab)
                .show()
        }
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.button3 -> {
                // Code khi bấm button3
            }

            R.id.nameKhanh -> {
                // Code khi click vào EditText
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController =
            findNavController(R.id.nav_host_fragment_content_main)

        return navController.navigateUp(appBarConfiguration) ||
                super.onSupportNavigateUp()
    }
}