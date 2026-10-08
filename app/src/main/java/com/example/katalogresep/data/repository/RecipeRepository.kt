package com.example.katalogresep.data.repository

import com.example.katalogresep.data.model.Recipe
import com.example.katalogresep.data.model.toRecipe
import com.example.katalogresep.data.remote.ApiService
import com.example.katalogresep.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext


interface RecipeRepository {
    suspend fun searchRecipes(query: String): Result<List<Recipe>>
    suspend fun getRecipeDetail(id: String): Result<Recipe>

    val favoriteRecipeIds: StateFlow<Set<String>>
    val savedRecipeIds: StateFlow<Set<String>>
    val favoriteRecipes: StateFlow<List<Recipe>>
    val savedRecipes: StateFlow<List<Recipe>>

    fun toggleFavorite(recipe: Recipe)
    fun toggleSave(recipe: Recipe)
    fun isFavorite(recipeId: String): Boolean
    fun isSaved(recipeId: String): Boolean
}


class RecipeRepositoryImpl(
    private val apiService: ApiService = RetrofitClient.apiService
) : RecipeRepository {

    companion object {
        private val _favoriteRecipeIds = MutableStateFlow<Set<String>>(emptySet())
        private val _savedRecipeIds = MutableStateFlow<Set<String>>(emptySet())
        private val _recipeCache = mutableMapOf<String, Recipe>()

        private val _favoriteRecipes = MutableStateFlow<List<Recipe>>(emptyList())
        private val _savedRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    }

    override val favoriteRecipeIds: StateFlow<Set<String>> = _favoriteRecipeIds.asStateFlow()
    override val savedRecipeIds: StateFlow<Set<String>> = _savedRecipeIds.asStateFlow()
    override val favoriteRecipes: StateFlow<List<Recipe>> = _favoriteRecipes.asStateFlow()
    override val savedRecipes: StateFlow<List<Recipe>> = _savedRecipes.asStateFlow()

    override fun isFavorite(recipeId: String): Boolean = _favoriteRecipeIds.value.contains(recipeId)
    override fun isSaved(recipeId: String): Boolean = _savedRecipeIds.value.contains(recipeId)

    override fun toggleFavorite(recipe: Recipe) {
        _recipeCache[recipe.id] = recipe
        val current = _favoriteRecipeIds.value.toMutableSet()
        if (current.contains(recipe.id)) {
            current.remove(recipe.id)
        } else {
            current.add(recipe.id)
        }
        _favoriteRecipeIds.value = current
        _favoriteRecipes.value = current.mapNotNull { _recipeCache[it] }
    }

    override fun toggleSave(recipe: Recipe) {
        _recipeCache[recipe.id] = recipe
        val current = _savedRecipeIds.value.toMutableSet()
        if (current.contains(recipe.id)) {
            current.remove(recipe.id)
        } else {
            current.add(recipe.id)
        }
        _savedRecipeIds.value = current
        _savedRecipes.value = current.mapNotNull { _recipeCache[it] }
    }

    override suspend fun searchRecipes(query: String): Result<List<Recipe>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchRecipes(query)
                if (response.isSuccessful) {
                    val meals = response.body()?.meals
                    if (meals != null) {
                        val recipeList = meals.map { it.toRecipe() }
                        recipeList.forEach { _recipeCache[it.id] = it }
                        _favoriteRecipes.value = _favoriteRecipeIds.value.mapNotNull { _recipeCache[it] }
                        _savedRecipes.value = _savedRecipeIds.value.mapNotNull { _recipeCache[it] }
                        Result.success(recipeList)
                    } else {
                        Result.success(emptyList())
                    }
                } else {
                    Result.failure(Exception("Gagal mengambil data resep: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getRecipeDetail(id: String): Result<Recipe> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRecipeDetail(id)
                if (response.isSuccessful) {
                    val mealDto = response.body()?.meals?.firstOrNull()
                    if (mealDto != null) {
                        val recipe = mealDto.toRecipe()
                        _recipeCache[recipe.id] = recipe
                        _favoriteRecipes.value = _favoriteRecipeIds.value.mapNotNull { _recipeCache[it] }
                        _savedRecipes.value = _savedRecipeIds.value.mapNotNull { _recipeCache[it] }
                        Result.success(recipe)
                    } else {
                        Result.failure(Exception("Detail resep tidak ditemukan"))
                    }
                } else {
                    Result.failure(Exception("Gagal mengambil detail resep: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

