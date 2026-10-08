package com.example.katalogresep.ui.home

import com.example.katalogresep.data.model.Recipe


sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val recipes: List<Recipe>) : HomeUiState
    data class Error(val message: String) : HomeUiState
    data class Empty(val query: String) : HomeUiState
}
