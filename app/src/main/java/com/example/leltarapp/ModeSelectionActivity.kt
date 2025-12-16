package com.inline.leltarapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.inline.leltarapp.data.model.InventoryMode
import com.inline.leltarapp.data.model.MotorType

class ModeSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mode_selection)

        val motorTypeName = intent.getStringExtra("MOTOR_TYPE")
        val paletteId = intent.getStringExtra("PALETTE_ID") ?: ""
        val serialNumber = intent.getStringExtra("SERIAL_NUMBER") ?: ""
        val signature = intent.getStringExtra("SIGNATURE") ?: ""

        val btnQuickMode = findViewById<MaterialButton>(R.id.btnQuickMode)
        val btnDetailedMode = findViewById<MaterialButton>(R.id.btnDetailedMode)

        btnQuickMode.setOnClickListener {
            val intent = Intent(this, QuickModeActivity::class.java)
            intent.putExtra("MOTOR_TYPE", motorTypeName)
            intent.putExtra("PALETTE_ID", paletteId)
            intent.putExtra("SERIAL_NUMBER", serialNumber)
            intent.putExtra("SIGNATURE", signature)
            intent.putExtra("MODE", InventoryMode.QUICK.name)
            startActivity(intent)
        }

        btnDetailedMode.setOnClickListener {
            val intent = Intent(this, DetailedModeActivity::class.java)
            intent.putExtra("MOTOR_TYPE", motorTypeName)
            intent.putExtra("PALETTE_ID", paletteId)
            intent.putExtra("SERIAL_NUMBER", serialNumber)
            intent.putExtra("SIGNATURE", signature)
            intent.putExtra("MODE", InventoryMode.DETAILED.name)
            startActivity(intent)
        }
    }
}