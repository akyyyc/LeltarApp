package com.inline.leltarapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.inline.leltarapp.data.model.EstimatedValue
import com.inline.leltarapp.data.model.Part
import com.inline.leltarapp.viewmodel.InventoryViewModel
import kotlinx.coroutines.launch

class SummaryActivity : AppCompatActivity() {

    private lateinit var viewModel: InventoryViewModel
    private lateinit var adapter: SummaryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        viewModel = ViewModelProvider(this)[InventoryViewModel::class.java]

        val tvMotorType = findViewById<TextView>(R.id.tvMotorType)
        val tvPaletteId = findViewById<TextView>(R.id.tvPaletteId)
        val tvSerialNumber = findViewById<TextView>(R.id.tvSerialNumber)
        val tvMode = findViewById<TextView>(R.id.tvMode)
        val rvSummary = findViewById<RecyclerView>(R.id.rvSummary)
        val btnExportPdf = findViewById<MaterialButton>(R.id.btnExportPdf)
        val btnFinish = findViewById<MaterialButton>(R.id.btnFinish)

        adapter = SummaryAdapter(emptyList(), emptyMap())
        rvSummary.layoutManager = LinearLayoutManager(this)
        rvSummary.adapter = adapter

        lifecycleScope.launch {
            viewModel.inventory.collect { inventory ->
                inventory?.let {
                    tvMotorType.text = "Motor típus: ${it.motorType.displayName}"
                    tvPaletteId.text = "Paletta: ${it.paletteId}"
                    tvSerialNumber.text = "Sorszám: ${it.serialNumber}"
                    tvMode.text = "Mód: ${if (it.mode.name == "QUICK") "Gyors" else "Pontos"}"

                    val summary = viewModel.getSummary()
                    adapter.updateSummary(viewModel.partsList.value, summary)
                }
            }
        }

        btnExportPdf.setOnClickListener {
            // TODO: PDF export
        }

        btnFinish.setOnClickListener {
            // Vissza a főmenübe
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }
}

class SummaryAdapter(
    private var parts: List<Part>,
    private var summary: Map<String, Map<String, EstimatedValue>>
) : RecyclerView.Adapter<SummaryAdapter.SummaryViewHolder>() {

    class SummaryViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val tvPartName: TextView = itemView.findViewById(R.id.tvPartName)
        val tvPartId: TextView = itemView.findViewById(R.id.tvPartId)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val layoutVariants: LinearLayout = itemView.findViewById(R.id.layoutVariants)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummaryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_summary, parent, false)
        return SummaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SummaryViewHolder, position: Int) {
        val part = parts[position]

        holder.tvPartName.text = part.name
        holder.tvPartId.text = part.id

        // Ha van variáns, akkor variánsonként mutatjuk
        if (part.hasVariants && summary.containsKey(part.id)) {
            holder.tvQuantity.text = ""
            holder.layoutVariants.visibility = android.view.View.VISIBLE
            holder.layoutVariants.removeAllViews()

            val variantEstimations = summary[part.id]
            variantEstimations?.forEach { (variantId, estimatedValue) ->
                val variant = part.variants?.find { it.id == variantId }

                val variantView = TextView(holder.itemView.context)
                variantView.text = "  ${variant?.name ?: variantId}: ${estimatedValue.toDisplayString()}"
                variantView.textSize = 12f
                variantView.setTextColor(holder.itemView.context.getColor(R.color.text_secondary))
                variantView.setPadding(16, 4, 0, 4)

                holder.layoutVariants.addView(variantView)
            }
        } else {
            // Nincs variáns, egyszerű mennyiség
            holder.layoutVariants.visibility = android.view.View.GONE

            // TODO: Total quantity from ViewModel
            holder.tvQuantity.text = "0"
        }
    }

    override fun getItemCount() = parts.size

    fun updateSummary(newParts: List<Part>, newSummary: Map<String, Map<String, EstimatedValue>>) {
        parts = newParts
        summary = newSummary
        notifyDataSetChanged()
    }
}