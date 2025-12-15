package com.inline.leltarapp.data.model

/**
 * Motor típusok
 * 4 féle motor létezik a rendszerben
 */
enum class MotorType(
    val code: String,           // Típuskód (pl. ".00L")
    val alternateCode: String,  // Alternatív kód (pl. ".163")
    val housingType: String,    // Ház típus (PL vagy PX)
    val displayName: String     // Megjelenítendő név
) {
    TYPE_00L(
        code = ".00L",
        alternateCode = ".163",
        housingType = "PL",
        displayName = ".00L (.163) - PL ház"
    ),
    TYPE_013(
        code = ".013",
        alternateCode = ".0LS",
        housingType = "PL",
        displayName = ".013 (.0LS) - PL ház"
    ),
    TYPE_01H(
        code = ".01H",
        alternateCode = ".162",
        housingType = "PX",
        displayName = ".01H (.162) - PX ház"
    ),
    TYPE_01P(
        code = ".01P",
        alternateCode = ".0EJ",
        housingType = "PX",
        displayName = ".01P (.0EJ) - PX ház"
    );
    
    /**
     * Egzotikus típus-e? (.01P és .013)
     */
    fun isExotic(): Boolean {
        return this == TYPE_01P || this == TYPE_013
    }
    
    companion object {
        fun fromCode(code: String): MotorType? {
            return values().find { 
                it.code == code || it.alternateCode == code 
            }
        }
    }
}
