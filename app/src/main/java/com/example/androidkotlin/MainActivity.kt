package com.example.androidkotlin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
    }
//        override fun onBackPressed() {
//            val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView6)
//            if (fragment is LoginFragment) {
//                if (fragment.onBackPressed()) {
//                    return
//                }
//            }
//            if (fragment is HomeFragment) {
//                if (fragment.onBackPressed()) {
//                    return
//                }
//            }
//            super.onBackPressed()
//        }
override fun onBackPressed() {
    super.onBackPressed()
    finish() // Exit the app when back button is pressed
}

}