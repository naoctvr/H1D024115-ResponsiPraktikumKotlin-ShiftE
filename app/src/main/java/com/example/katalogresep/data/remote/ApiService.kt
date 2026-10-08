package com.example.katalogresep.data.remote

import com.example.katalogresep.data.model.MealResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    @GET("search.php")
    suspend fun searchRecipes(
        @Query("s") query: String
    ): Response<MealResponseDto>

    @GET("lookup.php")
    suspend fun getRecipeDetail(
        @Query("i") id: String
    ): Response<MealResponseDto>
}
