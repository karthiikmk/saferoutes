package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.CarDetails

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey val registrationNumber: String,
    val makeModel: String,
    val variant: String,
    val tyreSpecification: String,
    val recommendedPsi: String,
    val hasConnectedCarBluelink: Boolean,
    val fastagProvider: String,
    val fuelType: String,
    val oemRsaPhone: String,
    val sosButtonInstruction: String,
    val userEmail: String,
    val isDefault: Boolean = false
) {
    fun toCarDetails(): CarDetails {
        return CarDetails(
            makeModel = makeModel,
            variant = variant,
            registrationNumber = registrationNumber,
            tyreSpecification = tyreSpecification,
            recommendedPsi = recommendedPsi,
            hasConnectedCarBluelink = hasConnectedCarBluelink,
            fastagProvider = fastagProvider,
            fuelType = fuelType,
            oemRsaPhone = oemRsaPhone,
            sosButtonInstruction = sosButtonInstruction
        )
    }

    companion object {
        fun fromCarDetails(details: CarDetails, email: String, isDefault: Boolean = false): VehicleEntity {
            return VehicleEntity(
                registrationNumber = details.registrationNumber.ifBlank { "REG-${System.currentTimeMillis()}" },
                makeModel = details.makeModel,
                variant = details.variant,
                tyreSpecification = details.tyreSpecification,
                recommendedPsi = details.recommendedPsi,
                hasConnectedCarBluelink = details.hasConnectedCarBluelink,
                fastagProvider = details.fastagProvider,
                fuelType = details.fuelType,
                oemRsaPhone = details.oemRsaPhone,
                sosButtonInstruction = details.sosButtonInstruction,
                userEmail = email,
                isDefault = isDefault
            )
        }
    }
}
