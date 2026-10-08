package com.example.katalogresep.data.model


data class Recipe(
    val id: String,
    val name: String,
    val category: String,
    val area: String,
    val instructions: String,
    val thumbUrl: String,
    val youtubeUrl: String?,
    val sourceUrl: String?,
    val tags: List<String>,
    val ingredients: List<Ingredient>
)
