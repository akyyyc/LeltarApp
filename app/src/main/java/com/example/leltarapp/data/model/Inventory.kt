package com.inline.leltarapp.data.model

import java.util.Date

/**
 * Motor adatmodell
 * Egy konkrét motort reprezentál a leltárban
 */
data class Motor(
    val id: String,                              // Motor egyedi azonosító
    val motorNumber: Int,                        // Motor sorszáma (pl. 1, 2, 3...)
    val motorType: MotorType,                    // Motor típus
    val parts: MutableMap<String, Int>,          // Alkatrész ID -> mennyiség
    val partVariants: MutableMap<String, String>, // Alkatrész ID -> választott variáns ID
    val isCovered: Boolean = false,              // Van-e rajta fedél? (nem látható belül)
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Leltár adatmodell
 * Egy paletta/bowden leltározását reprezentálja
 */
data class Inventory(
    val id: String,                              // Leltár egyedi azonosító
    val motorType: MotorType,                    // Motor típus
    val paletteId: String = "",                  // Paletta azonosító (kézi)
    val serialNumber: String = "",               // Sorszám
    val date: Date = Date(),                     // Dátum
    val signature: String = "",                  // Aláírás
    var mode: InventoryMode = InventoryMode.QUICK, // Leltározási mód
    val motors: MutableList<Motor> = mutableListOf(), // Részletes motorok (pontos mód)
    val totalParts: MutableMap<String, Int> = mutableMapOf(), // Összesítés (gyors mód)
    val totalPartVariants: MutableMap<String, MutableMap<String, Int>> = mutableMapOf(), // Variánsok összesítése
    val coveredMotorCount: Int = 0,              // Fedeles motorok száma
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Becsült összesítés számítása (fedeles motorok miatt)
     */
    fun calculateEstimatedTotals(): Map<String, Map<String, EstimatedValue>> {
        val result = mutableMapOf<String, MutableMap<String, EstimatedValue>>()
        
        // Minden variánsos alkatrészre
        totalPartVariants.forEach { (partId, variantCounts) ->
            val variantEstimations = mutableMapOf<String, EstimatedValue>()
            
            // Összes látható darab
            val totalVisible = variantCounts.values.sum()
            
            // Domináns variáns (legtöbb látható)
            val dominantVariant = variantCounts.maxByOrNull { it.value }?.key
            
            // Minden variánshoz számítunk becsült értéket
            variantCounts.forEach { (variantId, visibleCount) ->
                val estimatedCount = if (variantId == dominantVariant) {
                    coveredMotorCount  // Fedeles motorokat a domináns típushoz adjuk
                } else {
                    0
                }
                
                variantEstimations[variantId] = EstimatedValue(
                    visible = visibleCount,
                    estimated = estimatedCount,
                    total = visibleCount + estimatedCount
                )
            }
            
            result[partId] = variantEstimations
        }
        
        return result
    }
}

/**
 * Leltározási mód
 */
enum class InventoryMode {
    QUICK,      // Gyors pötyögős mód
    DETAILED    // Pontos motor-által-motor mód
}
