package com.customgit.presentation.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.customgit.R
import com.customgit.databinding.ActivityMainBinding
import com.customgit.presentation.auth.FragmentAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    //счетчики для задания логики выхода по двойному нажатию "назад"
    private var backButtonPressedTime: Long = 0
    private val backButtonInterval: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT //фиксированная портретная ориентация экрана

        clickToBackAndExit()

        supportFragmentManager
            .beginTransaction().replace(R.id.frame, FragmentAuth.newInstance()).commit()
    }

    //обработка двойного и одиночного нажатия кнопки назад
    private fun clickToBackAndExit() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentTime = System.currentTimeMillis()

                if (currentTime - backButtonPressedTime < backButtonInterval) {
                    finish()
                } else {
                    backButtonPressedTime = currentTime

                    Toast.makeText(this@MainActivity, "Press back again to exit", Toast.LENGTH_SHORT).show()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }
}