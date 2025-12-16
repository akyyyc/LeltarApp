package com.inline.leltarapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.inline.leltarapp.data.model.MotorType
import com.inline.leltarapp.data.model.Part
import com.inline.leltarapp.viewmodel.InventoryViewModel
import kotlinx.coroutines.launch

class DetailedModeActivity : AppCompatActivity() {

    private lateinit var viewModel: InventoryViewModel
    private lateinit var adapter: DetailedModeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_mode)

        viewModel = ViewModelProvider(this)[InventoryViewModel::class.java]

        val motorTypeName = intent.getStringExtra("MOTOR_TYPE")
        val paletteId = intent.getStringExtra("PALETTE_ID") ?: ""
        val serialNumber = intent.getStringExtra("SERIAL_NUMBER") ?: ""
        val signature = intent.getStringExtra("SIGNATURE") ?: ""

        val motorType = MotorType.valueOf(motorTypeName ?: "TYPE_00L")

        viewModel.startNewInventory(
            motorType = motorType,
            paletteId = paletteId,
            serialNumber = serialNumber,
            signature = signature
        )

        val tvMotorNumber = findViewById<TextView>(R.id.tvMotorNumber)
        val rvParts = findViewById<RecyclerView>(R.id.rvParts)
        val btnNextMotor = findViewById<MaterialButton>(R.id.btnNextMotor)
        val btnFinish = findViewById<MaterialButton>(R.id.btnFinish)

        adapter = DetailedModeAdapter(emptyList(), viewModel)
        rvParts.layoutManager = LinearLayoutManager(this)
        rvParts.adapter = adapter

        viewModel.startNewMotor()

        lifecycleScope.launch {
            viewModel.partsList.collect { parts ->
                adapter.updateParts(parts)
            }
        }

        lifecycleScope.launch {
            viewModel.currentMotor.collect { motor ->
                motor?.let {
                    tvMotorNumber.text = getString(R.string.motor_number_format, it.motorNumber)
                }
            }
        }

        btnNextMotor.setOnClickListener {
            viewModel.saveCurrentMotor()
            viewModel.startNewMotor()
        }

        btnFinish.setOnClickListener {
            viewModel.saveCurrentMotor()
            val intent = Intent(this, SummaryActivity::class.java)
            startActivity(intent)
        }
    }
}

class DetailedModeAdapter(
    private var parts: List<Part>,
    private val viewModel: InventoryViewModel
) : RecyclerView.Adapter<DetailedModeAdapter.PartViewHolder>() {

    class PartViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val checkboxPart: MaterialCheckBox = itemView.findViewById(R.id.checkboxPart)
        val tvPartName: TextView = itemView.findViewById(R.id.tvPartName)
        val tvPartId: TextView = itemView.findViewById(R.id.tvPartId)
        val chipGroupVariants: ChipGroup = itemView.findViewById(R.id.chipGroupVariants)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_part_detailed, parent, false)
        return PartViewHolder(view)
    }

    override fun onBindViewHolder(holder: PartViewHolder, position: Int) {
        val part = parts[position]

        holder.tvPartName.text = part.name
        holder.tvPartId.text = part.id

        holder.checkboxPart.setOnCheckedChangeListener { _, isChecked ->
            viewModel.togglePartInMotor(part.id, isChecked)
        }

        if (part.hasVariants && part.variants != null) {
            holder.chipGroupVariants.visibility = android.view.View.VISIBLE
            holder.chipGroupVariants.removeAllViews()

            part.variants.forEach { variant ->
                val chip = Chip(holder.itemView.context)
                chip.text = variant.name
                chip.isCheckable = true
                chip.tag = variant.id
                chip.textSize = 11f
                chip.chipMinHeight = 32f
                chip.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        viewModel.selectVariant(part.id, variant.id)
                    }
                }
                holder.chipGroupVariants.addView(chip)
            }

            if (holder.chipGroupVariants.childCount > 0) {
                (holder.chipGroupVariants.getChildAt(0) as Chip).isChecked = true
            }
        } else {
            holder.chipGroupVariants.visibility = android.view.View.GONE
        }
    }

    override fun getItemCount() = parts.size

    fun updateParts(newParts: List<Part>) {
        parts = newParts
        notifyDataSetChanged()
    }
}
