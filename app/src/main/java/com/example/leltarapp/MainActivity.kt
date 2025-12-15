package com.inline.leltarapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnNewInventory = findViewById<MaterialButton>(R.id.btnNewInventory)
        val btnContinue = findViewById<MaterialButton>(R.id.btnContinue)
        val btnHistory = findViewById<MaterialButton>(R.id.btnHistory)

        btnNewInventory.setOnClickListener {
        }

        btnContinue.setOnClickListener {
        }

        btnHistory.setOnClickListener {
        }
    }
}