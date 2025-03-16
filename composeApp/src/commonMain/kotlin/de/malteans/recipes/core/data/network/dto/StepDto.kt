package de.malteans.recipes.core.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class StepDto(
    val id: Long,
    val stepNumber: Int,
    val description: String,
    val duration: Int?,
)
