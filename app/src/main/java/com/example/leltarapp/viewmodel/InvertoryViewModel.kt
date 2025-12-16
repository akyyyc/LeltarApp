package com.inline.leltarapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inline.leltarapp.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

/**
 * Leltár ViewModel
 * Kezeli a leltározási folyamatot és az üzleti logikát
 */
class InventoryViewModel : ViewModel() {

    // Aktuális leltár állapot
    private val _inventory = MutableStateFlow<Inventory?>(null)
    val inventory: StateFlow<Inventory?> = _inventory.asStateFlow()

    // Aktuálisan szerkesztett motor (pontos mód)
    private val _currentMotor = MutableStateFlow<Motor?>(null)
    val currentMotor: StateFlow<Motor?> = _currentMotor.asStateFlow()

    // Motor típushoz tartozó alkatrészek
    private val _partsList = MutableStateFlow<List<Part>>(emptyList())
    val partsList: StateFlow<List<Part>> = _partsList.asStateFlow()

    /**
     * Új leltár kezdése
     */
    fun startNewInventory(
        motorType: MotorType,
        paletteId: String = "",
        serialNumber: String = "",
        signature: String = ""
    ) {
        val newInventory = Inventory(
            id = UUID.randomUUID().toString(),
            motorType = motorType,
            paletteId = paletteId,
            serialNumber = serialNumber,
            signature = signature,
            date = Date()
        )

        _inventory.value = newInventory
        _partsList.value = PartLibrary.getPartsForMotorType(motorType)
    }

    /**
     * Leltározási mód váltása (Gyors ↔ Pontos)
     */
    fun switchMode(newMode: InventoryMode) {
        _inventory.value = _inventory.value?.copy(mode = newMode)
    }

    // ========== GYORS MÓD FUNKCIÓK ==========

    /**
     * Alkatrész mennyiség növelése (gyors mód)
     */
    fun incrementPart(partId: String, variantId: String? = null, amount: Int = 1) {
        viewModelScope.launch {
            _inventory.value?.let { inv ->
                // Alap mennyiség növelése
                val currentTotal = inv.totalParts[partId] ?: 0
                inv.totalParts[partId] = currentTotal + amount

                // Variáns mennyiség növelése (ha van)
                if (variantId != null) {
                    val variantMap = inv.totalPartVariants.getOrPut(partId) { mutableMapOf() }
                    val currentVariantCount = variantMap[variantId] ?: 0
                    variantMap[variantId] = currentVariantCount + amount
                }

                // Trigger StateFlow update
                _inventory.value = inv.copy()
            }
        }
    }

    /**
     * Alkatrész mennyiség csökkentése (gyors mód)
     */
    fun decrementPart(partId: String, variantId: String? = null, amount: Int = 1) {
        viewModelScope.launch {
            _inventory.value?.let { inv ->
                // Alap mennyiség csökkentése (nem mehet negatívba)
                val currentTotal = inv.totalParts[partId] ?: 0
                inv.totalParts[partId] = maxOf(0, currentTotal - amount)

                // Variáns mennyiség csökkentése (ha van)
                if (variantId != null) {
                    val variantMap = inv.totalPartVariants[partId]
                    if (variantMap != null) {
                        val currentVariantCount = variantMap[variantId] ?: 0
                        variantMap[variantId] = maxOf(0, currentVariantCount - amount)
                    }
                }

                // Trigger StateFlow update
                _inventory.value = inv.copy()
            }
        }
    }

    // ========== PONTOS MÓD FUNKCIÓK ==========

    /**
     * Új motor kezdése (pontos mód)
     */
    fun startNewMotor() {
        _inventory.value?.let { inv ->
            val motorNumber = inv.motors.size + 1
            val newMotor = Motor(
                id = UUID.randomUUID().toString(),
                motorNumber = motorNumber,
                motorType = inv.motorType,
                parts = mutableMapOf(),
                partVariants = mutableMapOf()
            )
            _currentMotor.value = newMotor
        }
    }

    /**
     * Alkatrész kiválasztása/kipipálása (pontos mód)
     */
    fun togglePartInMotor(partId: String, isChecked: Boolean) {
        _currentMotor.value?.let { motor ->
            val updated = motor.copy()

            if (isChecked) {
                // Alkatrész hozzáadása
                val part = _partsList.value.find { it.id == partId }
                updated.parts[partId] = part?.quantityPerMotor ?: 1

                // Intelligens függőségek alkalmazása
                applyDependencies(updated, partId, DependencyDirection.FORWARD)
            } else {
                // Alkatrész eltávolítása
                updated.parts.remove(partId)
                updated.partVariants.remove(partId)
            }

            _currentMotor.value = updated
        }
    }

    /**
     * Alkatrész mennyiség beállítása (pontos mód - csavarok)
     */
    fun setPartQuantity(partId: String, quantity: Int) {
        _currentMotor.value?.let { motor ->
            val updated = motor.copy()
            updated.parts[partId] = quantity
            _currentMotor.value = updated
        }
    }

    /**
     * Variáns kiválasztása (pontos mód)
     */
    fun selectVariant(partId: String, variantId: String) {
        _currentMotor.value?.let { motor ->
            val updated = motor.copy()
            updated.partVariants[partId] = variantId

            // Intelligens függőségek alkalmazása
            applyDependencies(updated, partId, DependencyDirection.BACKWARD)

            _currentMotor.value = updated
        }
    }

    /**
     * Motor mentése (pontos mód)
     */
    fun saveCurrentMotor() {
        viewModelScope.launch {
            _currentMotor.value?.let { motor ->
                _inventory.value?.let { inv ->
                    val updated = inv.copy()
                    updated.motors.add(motor)

                    // Összesítés frissítése
                    motor.parts.forEach { (partId, quantity) ->
                        val currentTotal = updated.totalParts[partId] ?: 0
                        updated.totalParts[partId] = currentTotal + quantity
                    }

                    // Variánsok összesítése
                    motor.partVariants.forEach { (partId, variantId) ->
                        val variantMap = updated.totalPartVariants.getOrPut(partId) { mutableMapOf() }
                        val currentCount = variantMap[variantId] ?: 0
                        variantMap[variantId] = currentCount + (motor.parts[partId] ?: 1)
                    }

                    _inventory.value = updated
                    _currentMotor.value = null
                }
            }
        }
    }

    /**
     * Fedeles motor hozzáadása (NEM ELDÖNTHETŐ variánsok)
     */
    fun addCoveredMotor() {
        viewModelScope.launch {
            _inventory.value?.let { inv ->
                val updated = inv.copy(coveredMotorCount = inv.coveredMotorCount + 1)

                // Minden variánsos alkatrészhez hozzáadunk 1-1 db-ot
                _partsList.value.forEach { part ->
                    if (part.hasVariants) {
                        val currentTotal = updated.totalParts[part.id] ?: 0
                        updated.totalParts[part.id] = currentTotal + 1

                        // "NEM ELDÖNTHETŐ" variáns hozzáadása
                        val variantMap = updated.totalPartVariants.getOrPut(part.id) { mutableMapOf() }
                        val unknownCount = variantMap["UNKNOWN"] ?: 0
                        variantMap["UNKNOWN"] = unknownCount + 1
                    }
                }

                _inventory.value = updated
            }
        }
    }

    // ========== INTELLIGENS FÜGGŐSÉGI LOGIKA ==========

    private enum class DependencyDirection {
        FORWARD,   // Előre (pl. Shaft választás → Szenzor/Kábel/PCB auto)
        BACKWARD   // Hátra (pl. Fogaskerék választás → Rotor auto)
    }

    /**
     * Intelligens függőségek alkalmazása
     */
    private fun applyDependencies(motor: Motor, partId: String, direction: DependencyDirection) {
        when (partId) {
            // ===== SHAFT (TENGELY) =====
            "SHAFT" -> {
                if (direction == DependencyDirection.FORWARD) {
                    // Ha shaft-et választunk, alatta minden kell
                    motor.partVariants["SHAFT"]?.let { shaftVariant ->
                        // Szenzor, Kábel, PCB ugyanaz a típus
                        motor.partVariants["SENSOR"] = shaftVariant
                        motor.partVariants["CABLE"] = shaftVariant
                        motor.partVariants["PCB"] = shaftVariant

                        // Alkatrészek bejelölése
                        motor.parts["SENSOR"] = 1
                        motor.parts["CABLE"] = 1
                        motor.parts["PCB"] = 1

                        // Alatta lévők
                        motor.parts["ROTOR"] = 1
                        motor.parts["EB11.200.0D9"] = 1  // BearingB
                        motor.parts["EB11.200.0CN/0G7"] = 1  // GOLYÓSCS
                    }
                }
            }

            // ===== PCB =====
            "PCB" -> {
                if (direction == DependencyDirection.FORWARD) {
                    // Ha PCB-t választunk, szenzor + kábel kell
                    motor.partVariants["PCB"]?.let { pcbVariant ->
                        motor.partVariants["SENSOR"] = pcbVariant
                        motor.partVariants["CABLE"] = pcbVariant

                        motor.parts["SENSOR"] = 1
                        motor.parts["CABLE"] = 1

                        // Alatta lévők
                        motor.parts["ROTOR"] = 1
                        motor.parts["EB11.200.0D9"] = 1
                        motor.parts["EB11.200.0CN/0G7"] = 1
                    }
                }
            }

            // ===== GEARMODUL (FOGASKERÉK) =====
            "GEARMODUL" -> {
                if (direction == DependencyDirection.BACKWARD) {
                    // Ha fogaskereket választunk → rotor automatikusan
                    motor.partVariants["GEARMODUL"]?.let { gearVariant ->
                        motor.partVariants["ROTOR"] = gearVariant  // Reich → Reich, Serrano → Serrano
                        motor.parts["ROTOR"] = 1
                    }
                }
            }

            // ===== SZENZOR =====
            "SENSOR" -> {
                // Ha szenzort választunk, ajánljuk a kábelt, PCB-t, shaft-et
                motor.partVariants["SENSOR"]?.let { sensorVariant ->
                    if (motor.partVariants["CABLE"] == null) {
                        motor.partVariants["CABLE"] = sensorVariant
                    }
                    if (motor.partVariants["PCB"] == null) {
                        motor.partVariants["PCB"] = sensorVariant
                    }
                    if (motor.partVariants["SHAFT"] == null) {
                        motor.partVariants["SHAFT"] = sensorVariant
                    }
                }
            }
        }
    }

    /**
     * Összesítés lekérése (becsült értékekkel)
     */
    fun getSummary(): Map<String, Map<String, EstimatedValue>> {
        return _inventory.value?.calculateEstimatedTotals() ?: emptyMap()
    }
}