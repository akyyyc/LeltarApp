package com.inline.leltarapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.inline.leltarapp.data.model.Part
import com.inline.leltarapp.viewmodel.InventoryViewModel
import kotlinx.coroutines.launch
import android.widget.TextView
import com.inline.leltarapp.data.model.MotorType

class QuickModeActivity : AppCompatActivity() {

    private lateinit var viewModel: InventoryViewModel
    private lateinit var adapter: QuickModeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_mode)

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

        val rvParts = findViewById<RecyclerView>(R.id.rvParts)
        val btnAddCoveredMotor = findViewById<MaterialButton>(R.id.btnAddCoveredMotor)
        val btnFinish = findViewById<MaterialButton>(R.id.btnFinish)

        adapter = QuickModeAdapter(emptyList(), viewModel)
        rvParts.layoutManager = LinearLayoutManager(this)
        rvParts.adapter = adapter

        // Alkatrészlista frissítése
        lifecycleScope.launch {
            viewModel.partsList.collect { parts ->
                adapter.updateParts(parts)
            }
        }

        // Mennyiségek frissítése
        lifecycleScope.launch {
            viewModel.inventory.collect { inventory ->
                inventory?.let {
                    adapter.updateQuantities(it.totalParts)
                }
            }
        }

        btnAddCoveredMotor.setOnClickListener {
            viewModel.addCoveredMotor()
        }

        btnFinish.setOnClickListener {
            val intent = Intent(this, SummaryActivity::class.java)
            startActivity(intent)
        }
    }
}

class QuickModeAdapter(
    private var parts: List<Part>,
    private val viewModel: InventoryViewModel
) : RecyclerView.Adapter<QuickModeAdapter.PartViewHolder>() {

    private val quantities = mutableMapOf<String, Int>()

    class PartViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val tvPartName: TextView = itemView.findViewById(R.id.tvPartName)
        val tvPartId: TextView = itemView.findViewById(R.id.tvPartId)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val btnIncrement: MaterialButton = itemView.findViewById(R.id.btnIncrement)
        val btnDecrement: MaterialButton = itemView.findViewById(R.id.btnDecrement)
        val chipGroupVariants: ChipGroup = itemView.findViewById(R.id.chipGroupVariants)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_part_quick, parent, false)
        return PartViewHolder(view)
    }

    override fun onBindViewHolder(holder: PartViewHolder, position: Int) {
        val part = parts[position]

        holder.tvPartName.text = part.name
        holder.tvPartId.text = part.id

        // Mennyiség megjelenítése
        val currentQuantity = quantities[part.id] ?: 0
        holder.tvQuantity.text = currentQuantity.toString()

        holder.btnIncrement.setOnClickListener {
            val selectedVariant = getSelectedVariant(holder.chipGroupVariants, part)
            viewModel.incrementPart(part.id, selectedVariant, 1)
        }

        holder.btnDecrement.setOnClickListener {
            val selectedVariant = getSelectedVariant(holder.chipGroupVariants, part)
            viewModel.decrementPart(part.id, selectedVariant, 1)
        }

        // Variánsok kezelése
        if (part.hasVariants && part.variants != null) {
            holder.chipGroupVariants.visibility = android.view.View.VISIBLE
            holder.chipGroupVariants.removeAllViews()

            part.variants.forEach { variant ->
                val chip = Chip(holder.itemView.context)
                chip.text = variant.name
                chip.isCheckable = true
                chip.tag = variant.id

                // Chip stílus
                chip.textSize = 11f
                chip.chipMinHeight = 32f

                holder.chipGroupVariants.addView(chip)
            }

            // Első variáns auto kiválasztása
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

    fun updateQuantities(newQuantities: Map<String, Int>) {
        quantities.clear()
        quantities.putAll(newQuantities)
        notifyDataSetChanged()
    }

    private fun getSelectedVariant(chipGroup: ChipGroup, part: Part): String? {
        if (!part.hasVariants) return null

        val selectedChipId = chipGroup.checkedChipId
        if (selectedChipId == -1) return null

        val selectedChip = chipGroup.findViewById<Chip>(selectedChipId)
        return selectedChip?.tag as? String
    }
}
