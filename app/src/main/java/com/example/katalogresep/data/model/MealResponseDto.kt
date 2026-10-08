package com.example.katalogresep.data.model

import com.google.gson.annotations.SerializedName


data class MealResponseDto(
    @SerializedName("meals")
    val meals: List<MealDto>? = null
)
