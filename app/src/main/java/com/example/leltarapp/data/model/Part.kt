package com.inline.leltarapp.data.model

/**
 * Alkatrész adatmodell
 * Egy motor alkatrészét reprezentálja
 */
data class Part(
    val id: String,                    // Cikkszám (pl. "EB11.200.134")
    val name: String,                  // Megnevezés (pl. "STH PX")
    val category: PartCategory,        // Kategória
    val quantityPerMotor: Int = 1,     // Mennyiség egy komplett motorban
    val hasVariants: Boolean = false,  // Van-e variánsa (pl. Reich/Serrano)
    val variants: List<PartVariant>? = null,  // Variánsok listája
    val isQuantityVariable: Boolean = false,  // Változó mennyiség? (pl. csavarok)
    val possibleQuantities: List<Int>? = null // Lehetséges mennyiségek (pl. [0,2,3,5])
)

/**
 * Alkatrész kategóriák
 */
enum class PartCategory {
    HOUSING,           // Ház (STH PX/PL)
    BEARING,           // Csapágy
    ROTOR,             // Rotor
    SENSOR,            // Szenzor
    CABLE,             // Kábel
    PCB,               // PCB
    SCREW,             // Csavar
    GEARMODULE,        // Fogaskerék
    SHAFT,             // Tengely
    COVER,             // Fedél
    BUSHING,           // Persely
    PLUG,              // Dugó
    SEAL,              // Tömítés
    LABEL              // Címke
}

/**
 * Alkatrész variáns
 * Pl. Reich/Serrano rotor, Method/PME-TS szenzor
 */
data class PartVariant(
    val id: String,           // Variáns azonosító
    val name: String,         // Variáns megnevezés (pl. "Reich", "Method")
    val partNumber: String,   // Teljes cikkszám (pl. "EB11.200.0SK")
    val description: String?  // Leírás (pl. "fekete szenzor", "piros kábel")
)

/**
 * Becsült érték (fedeles motorok miatt)
 */
data class EstimatedValue(
    val visible: Int,      // Látható mennyiség (biztos)
    val estimated: Int,    // Becsült mennyiség (fedeles motorokból)
    val total: Int         // Összesen
) {
    fun toDisplayString(): String {
        return if (estimated > 0) {
            "$visible + $estimated? = $total?"
        } else {
            "$visible"
        }
    }
    
    fun toStrikeString(): String {
        val visibleStrikes = "|".repeat(visible)
        val estimatedStrikes = "?".repeat(estimated)
        return if (estimated > 0) {
            "$visibleStrikes $estimatedStrikes"
        } else {
            visibleStrikes
        }
    }
}
