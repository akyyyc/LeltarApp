package com.inline.leltarapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.inline.leltarapp.data.model.MotorType
import com.inline.leltarapp.viewmodel.InventoryViewModel

class TypeSelectionActivity : AppCompatActivity() {

    private lateinit var viewModel: InventoryViewModel
    private var selectedType: MotorType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_type_selection)

        viewModel = ViewModelProvider(this)[InventoryViewModel::class.java]

        val btn00L = findViewById<MaterialButton>(R.id.btn00L)
        val btn013 = findViewById<MaterialButton>(R.id.btn013)
        val btn01H = findViewById<MaterialButton>(R.id.btn01H)
        val btn01P = findViewById<MaterialButton>(R.id.btn01P)
        val btnStart = findViewById<MaterialButton>(R.id.btnStart)

        val etPaletteId = findViewById<TextInputEditText>(R.id.etPaletteId)
        val etSerialNumber = findViewById<TextInputEditText>(R.id.etSerialNumber)
        val etSignature = findViewById<TextInputEditText>(R.id.etSignature)

        btn00L.setOnClickListener {
            selectedType = MotorType.TYPE_00L
            highlightSelected(btn00L, btn013, btn01H, btn01P)
        }

        btn013.setOnClickListener {
            selectedType = MotorType.TYPE_013
            highlightSelected(btn013, btn00L, btn01H, btn01P)
        }

        btn01H.setOnClickListener {
            selectedType = MotorType.TYPE_01H
            highlightSelected(btn01H, btn00L, btn013, btn01P)
        }

        btn01P.setOnClickListener {
            selectedType = MotorType.TYPE_01P
            highlightSelected(btn01P, btn00L, btn013, btn01H)
        }

        btnStart.setOnClickListener {
            if (selectedType != null) {
                val paletteId = etPaletteId.text.toString()
                val serialNumber = etSerialNumber.text.toString()
                val signature = etSignature.text.toString()

                val intent = Intent(this, ModeSelectionActivity::class.java)
                intent.putExtra("MOTOR_TYPE", selectedType!!.name)
                intent.putExtra("PALETTE_ID", paletteId)
                intent.putExtra("SERIAL_NUMBER", serialNumber)
                intent.putExtra("SIGNATURE", signature)
                startActivity(intent)
            }
        }
    }

    private fun highlightSelected(selected: MaterialButton, vararg others: MaterialButton) {
        selected.strokeWidth = 4
        others.forEach { it.strokeWidth = 0 }
    }
}