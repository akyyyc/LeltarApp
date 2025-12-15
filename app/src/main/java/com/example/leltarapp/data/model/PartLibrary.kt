package com.inline.leltarapp.data.model

/**
 * Alkatrész könyvtár
 * Minden motor típushoz tartozó alkatrészlista
 */
object PartLibrary {
    
    /**
     * Motor típushoz tartozó alkatrészlista lekérése
     */
    fun getPartsForMotorType(motorType: MotorType): List<Part> {
        return when (motorType) {
            MotorType.TYPE_00L -> parts_00L
            MotorType.TYPE_013 -> parts_013
            MotorType.TYPE_01H -> parts_01H
            MotorType.TYPE_01P -> parts_01P
        }
    }
    
    // ========== .00L (.163) - PL HÁZ ==========
    private val parts_00L = listOf(
        // 1. STH PL (ház)
        Part(
            id = "EB11.200.134",
            name = "STH PL",
            category = PartCategory.HOUSING,
            quantityPerMotor = 1
        ),
        
        // 2. GOLYÓSCSAPÁGY
        Part(
            id = "EB11.200.0CN/0G7",
            name = "GOLYÓSCS",
            category = PartCategory.BEARING,
            quantityPerMotor = 1
        ),
        
        // 3. BearingB
        Part(
            id = "EB11.200.0D9",
            name = "BearingB",
            category = PartCategory.BEARING,
            quantityPerMotor = 1
        ),
        
        // 4. Rotor (Reich VAGY Serrano)
        Part(
            id = "ROTOR",
            name = "Rotor",
            category = PartCategory.ROTOR,
            quantityPerMotor = 1,
            hasVariants = true,
            variants = listOf(
                PartVariant(
                    id = "REICH",
                    name = "Reich",
                    partNumber = "EB11.200.0SK",
                    description = null
                ),
                PartVariant(
                    id = "SERRANO",
                    name = "Serrano",
                    partNumber = "EB11.200.0M3",
                    description = null
                ),
                PartVariant(
                    id = "UNKNOWN",
                    name = "NEM ELDÖNTHETŐ",
                    partNumber = "???",
                    description = "Fedeles motor, nem látható"
                )
            )
        ),
        
        // 5. PCB csavar (első rész: szenzor + bearing)
        Part(
            id = "3283.435.759_FIRST",
            name = "PCB csavar (első)",
            category = PartCategory.SCREW,
            quantityPerMotor = 5,
            isQuantityVariable = true,
            possibleQuantities = listOf(0, 2, 3, 5)
        ),
        
        // 6. Szenzor (Method VAGY PME-TS)
        Part(
            id = "SENSOR",
            name = "Szenzor",
            category = PartCategory.SENSOR,
            quantityPerMotor = 1,
            hasVariants = true,
            variants = listOf(
                PartVariant(
                    id = "METHOD",
                    name = "Method",
                    partNumber = "EB11.200.0DF",
                    description = "fekete szenzor"
                ),
                PartVariant(
                    id = "PME_TS",
                    name = "PME-TS",
                    partNumber = "EB11.200.0GR",
                    description = "fehér szenzor"
                ),
                PartVariant(
                    id = "UNKNOWN",
                    name = "NEM ELDÖNTHETŐ",
                    partNumber = "???",
                    description = "Fedeles motor, nem látható"
                )
            )
        ),
        
        // 7. Kábel (Method VAGY PME-TS)
        Part(
            id = "CABLE",
            name = "Kábel",
            category = PartCategory.CABLE,
            quantityPerMotor = 1,
            hasVariants = true,
            variants = listOf(
                PartVariant(
                    id = "METHOD",
                    name = "Method",
                    partNumber = "EB11.200.0YE",
                    description = "piros kábel"
                ),
                PartVariant(
                    id = "PME_TS",
                    name = "PME-TS",
                    partNumber = "EB11.200.0EV",
                    description = "kék kábel"
                ),
                PartVariant(
                    id = "UNKNOWN",
                    name = "NEM ELDÖNTHETŐ",
                    partNumber = "???",
                    description = "Fedeles motor, nem látható"
                )
            )
        ),
        
        // 8. PCB (Method VAGY PME-TS)
        Part(
            id = "PCB",
            name = "PCB",
            category = PartCategory.PCB,
            quantityPerMotor = 1,
            hasVariants = true,
            variants = listOf(
                PartVariant(
                    id = "METHOD",
                    name = "Method (STM/ME)",
                    partNumber = "EB11.200.0LY",
                    description = "piros csatlakozó"
                ),
                PartVariant(
                    id = "PME_TS",
                    name = "PME-TS (STM/PME)",
                    partNumber = "EB11.200.0ER",
                    description = "kék csatlakozó"
                ),
                PartVariant(
                    id = "UNKNOWN",
                    name = "NEM ELDÖNTHETŐ",
                    partNumber = "???",
                    description = "Fedeles motor, nem látható"
                )
            )
        ),
        
        // 9. PCB csavar (második rész: PCB csavarozás)
        Part(
            id = "3283.435.759_SECOND",
            name = "PCB csavar (második)",
            category = PartCategory.SCREW,
            quantityPerMotor = 4,
            isQuantityVariable = true,
            possibleQuantities = listOf(0, 4)
        ),
        
        // 10. Gearmodul (Reich VAGY Serrano)
        Part(
            id = "GEARMODUL",
            name = "Gearmodul",
            category = PartCategory.GEARMODULE,
            quantityPerMotor = 1,
            hasVariants = true,
            variants = listOf(
                PartVariant(
                    id = "REICH",
                    name = "Reich",
                    partNumber = "EB11.200.0R9",
                    description = null
                ),
                PartVariant(
                    id = "SERRANO",
                    name = "Serrano",
                    partNumber = "EB11.200.0SJ",
                    description = null
                ),
                PartVariant(
                    id = "UNKNOWN",
                    name = "NEM ELDÖNTHETŐ",
                    partNumber = "???",
                    description = "Fedeles motor, nem látható"
                )
            )
        ),
        
        // 11. Shaft (Method VAGY PME-TS)
        Part(
            id = "SHAFT",
            name = "shaft",
            category = PartCategory.SHAFT,
            quantityPerMotor = 1,
            hasVariants = true,
            variants = listOf(
                PartVariant(
                    id = "METHOD",
                    name = "Method",
                    partNumber = "EB11.200.0ET",
                    description = null
                ),
                PartVariant(
                    id = "PME_TS",
                    name = "PME-TS",
                    partNumber = "EB11.200.0R1",
                    description = null
                ),
                PartVariant(
                    id = "UNKNOWN",
                    name = "NEM ELDÖNTHETŐ",
                    partNumber = "???",
                    description = "Fedeles motor, nem látható"
                )
            )
        ),
        
        // 12. Hornyolt csapágy
        Part(
            id = "EB11.200.0JJ/0XB",
            name = "HORNYOLT GOLYÓSCS",
            category = PartCategory.BEARING,
            quantityPerMotor = 1
        ),
        
        // 13. Biztosítógyűrű
        Part(
            id = "1270.016.114",
            name = "BIZTOSÍTÓGYŰRŰ",
            category = PartCategory.SEAL,
            quantityPerMotor = 1
        ),
        
        // 14. O-gyűrű
        Part(
            id = "1270.016.106",
            name = "O-Gyűrű; 28X1",
            category = PartCategory.SEAL,
            quantityPerMotor = 1
        ),
        
        // 15. Házfedél
        Part(
            id = "EB11.200.0DA",
            name = "HÁZFEDÉL;",
            category = PartCategory.COVER,
            quantityPerMotor = 1
        ),
        
        // 16. Fedélcsavar
        Part(
            id = "1270.016.430",
            name = "fedél csavar",
            category = PartCategory.SCREW,
            quantityPerMotor = 5,
            isQuantityVariable = true,
            possibleQuantities = listOf(0, 5)
        ),
        
        // 17. Sleeve
        Part(
            id = "EB11.200.0CH/0XG",
            name = "sleeve",
            category = PartCategory.BUSHING,
            quantityPerMotor = 2
        ),
        
        // 18. Interface plain
        Part(
            id = "EB11.200.0UN/0XH",
            name = "Interface plain",
            category = PartCategory.BUSHING,
            quantityPerMotor = 2
        ),
        
        // 19. Vakdugó
        Part(
            id = "EB12.S00.08F",
            name = "Vakdugó;",
            category = PartCategory.PLUG,
            quantityPerMotor = 4
        ),
        
        // 20. DAE címke
        Part(
            id = "EB11.200.0E9",
            name = "DAE",
            category = PartCategory.LABEL,
            quantityPerMotor = 1
        )
    )
    
    // ========== .013 (.0LS) - PL HÁZ ==========
    // (Ugyanaz mint .00L, csak ház típus ugyanaz)
    private val parts_013 = parts_00L  // TODO: Finomhangolás ha van eltérés
    
    // ========== .01H (.162) - PX HÁZ ==========
    private val parts_01H = listOf(
        // 1. STH PX (ház)
        Part(
            id = "EB11.200.132",
            name = "STH PX",
            category = PartCategory.HOUSING,
            quantityPerMotor = 1
        ),
        
        // 2-3. Csapágyak (ugyanaz mint .00L)
        Part(
            id = "EB11.200.0CN/0G7",
            name = "GOLYÓSCS",
            category = PartCategory.BEARING,
            quantityPerMotor = 1
        ),
        Part(
            id = "EB11.200.0D9",
            name = "BearingB",
            category = PartCategory.BEARING,
            quantityPerMotor = 1
        ),
        
        // 4. Rotor (Reich VAGY Serrano - MÁSIK cikkszám!)
        Part(
            id = "ROTOR",
            name = "Rotor",
            category = PartCategory.ROTOR,
            quantityPerMotor = 1,
            hasVariants = true,
            variants = listOf(
                PartVariant(
                    id = "REICH",
                    name = "Reich",
                    partNumber = "EB11.200.0M2",  // PX házhoz!
                    description = null
                ),
                PartVariant(
                    id = "SERRANO",
                    name = "Serrano",
                    partNumber = "EB11.200.11E",  // PX házhoz!
                    description = null
                ),
                PartVariant(
                    id = "UNKNOWN",
                    name = "NEM ELDÖNTHETŐ",
                    partNumber = "???",
                    description = "Fedeles motor, nem látható"
                )
            )
        ),
        
        // 5-20. Többi alkatrész ugyanaz mint .00L
        // (Itt tömören, a teljes lista a parts_00L alapján)
    ) + parts_00L.drop(4)  // Az első 4 után ugyanazok az alkatrészek
    
    // ========== .01P (.0EJ) - PX HÁZ ==========
    // (Ugyanaz mint .01H)
    private val parts_01P = parts_01H  // TODO: Finomhangolás ha van eltérés
}
