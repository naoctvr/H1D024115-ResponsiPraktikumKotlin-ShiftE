package com.example.katalogresep.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.katalogresep.data.repository.RecipeRepository
import com.example.katalogresep.data.repository.RecipeRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class DetailViewModel(
    private val recipeId: String,
    private val repository: RecipeRepository = RecipeRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    val isFavorite: StateFlow<Boolean> = repository.favoriteRecipeIds.map { ids ->
        ids.contains(recipeId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.isFavorite(recipeId))

    val isSaved: StateFlow<Boolean> = repository.savedRecipeIds.map { ids ->
        ids.contains(recipeId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.isSaved(recipeId))

    init {
        fetchRecipeDetail()
    }

    fun toggleFavorite() {
        val currentRecipe = (_uiState.value as? DetailUiState.Success)?.recipe
        if (currentRecipe != null) {
            repository.toggleFavorite(currentRecipe)
        }
    }

    fun toggleSave() {
        val currentRecipe = (_uiState.value as? DetailUiState.Success)?.recipe
        if (currentRecipe != null) {
            repository.toggleSave(currentRecipe)
        }
    }


    fun fetchRecipeDetail() {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading

            repository.getRecipeDetail(recipeId)
                .onSuccess { recipe ->
                    _uiState.value = DetailUiState.Success(recipe)
                }
                .onFailure { throwable ->
                    _uiState.value = DetailUiState.Error(
                        throwable.localizedMessage ?: "Gagal memuat detail resep."
                    )
                }
        }
    }
}


class DetailViewModelFactory(
    private val recipeId: String,
    private val repository: RecipeRepository = RecipeRepositoryImpl()
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(recipeId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
