package com.example.katalogresep.ui.detail

import com.example.katalogresep.data.model.Recipe


sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val recipe: Recipe) : DetailUiState
    data class Error(val message: String) : DetailUiState
}
