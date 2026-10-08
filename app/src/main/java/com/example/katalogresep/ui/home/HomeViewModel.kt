package com.example.katalogresep.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.katalogresep.data.model.Recipe
import com.example.katalogresep.data.repository.RecipeRepository
import com.example.katalogresep.data.repository.RecipeRepositoryImpl
import com.example.katalogresep.ui.components.SimulationState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class HomeViewModel(
    private val repository: RecipeRepository = RecipeRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _isGridView = MutableStateFlow(true)
    val isGridView: StateFlow<Boolean> = _isGridView.asStateFlow()

    private val _simulatedState = MutableStateFlow(SimulationState.REAL_API)
    val simulatedState: StateFlow<SimulationState> = _simulatedState.asStateFlow()

    private val _recompositionCount = MutableStateFlow(1)
    val recompositionCount: StateFlow<Int> = _recompositionCount.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    val favoriteRecipeIds: StateFlow<Set<String>> = repository.favoriteRecipeIds
    val savedRecipeIds: StateFlow<Set<String>> = repository.savedRecipeIds
    val favoriteRecipes: StateFlow<List<Recipe>> = repository.favoriteRecipes
    val savedRecipes: StateFlow<List<Recipe>> = repository.savedRecipes

    private val _selectedTab = MutableStateFlow("eksplorasi")
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

    val categories = listOf(
        "Semua", "Beef", "Chicken", "Seafood", "Dessert", "Pasta", "Vegetarian", "Breakfast", "Side"
    )

    private var searchJob: Job? = null

    init {
        fetchRecipes("chicken")
    }


    fun selectSimulationState(state: SimulationState) {
        _simulatedState.value = state
        _recompositionCount.value += 1

        when (state) {
            SimulationState.LOADING -> showToast("Memanggil viewModel.fetchRecipes(retry = true)")
            SimulationState.EMPTY -> showToast("when(uiState) { is UiState.Empty -> EmptyQueryView() }")
            SimulationState.ERROR -> showToast("Pesan kesalahan: HTTP 503 SocketTimeoutException")
            SimulationState.REAL_API -> showToast("Menampilkan data katalog real dari TheMealDB API")
        }
    }

    fun selectTab(tabId: String) {
        _selectedTab.value = tabId
    }

    fun toggleFavorite(recipe: Recipe) {
        val isFav = repository.isFavorite(recipe.id)
        repository.toggleFavorite(recipe)
        if (isFav) {
            showToast("Dihapus dari Favorit")
        } else {
            showToast("Ditambahkan ke Favorit")
        }
    }

    fun toggleFavorite(recipeId: String) {
        val currentRecipes = (uiState.value as? HomeUiState.Success)?.recipes ?: emptyList()
        val recipe = currentRecipes.find { it.id == recipeId }
            ?: favoriteRecipes.value.find { it.id == recipeId }
            ?: savedRecipes.value.find { it.id == recipeId }
        if (recipe != null) {
            toggleFavorite(recipe)
        }
    }

    fun toggleSave(recipe: Recipe) {
        val isSav = repository.isSaved(recipe.id)
        repository.toggleSave(recipe)
        if (isSav) {
            showToast("Dihapus dari Simpan")
        } else {
            showToast("Disimpan ke Resep Simpan")
        }
    }

    fun toggleSave(recipeId: String) {
        val currentRecipes = (uiState.value as? HomeUiState.Success)?.recipes ?: emptyList()
        val recipe = currentRecipes.find { it.id == recipeId }
            ?: savedRecipes.value.find { it.id == recipeId }
            ?: favoriteRecipes.value.find { it.id == recipeId }
        if (recipe != null) {
            toggleSave(recipe)
        }
    }


    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        _simulatedState.value = SimulationState.REAL_API
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(500)
            if (newQuery.isBlank()) {
                fetchRecipes(if (_selectedCategory.value != "Semua") _selectedCategory.value else "chicken")
            } else {
                fetchRecipes(newQuery)
            }
        }
    }


    fun performSearch() {
        searchJob?.cancel()
        _simulatedState.value = SimulationState.REAL_API
        val query = _searchQuery.value
        fetchRecipes(query.ifBlank { "chicken" })
    }


    fun selectCategory(category: String) {
        _selectedCategory.value = category
        _searchQuery.value = ""
        _simulatedState.value = SimulationState.REAL_API
        fetchRecipes(if (category == "Semua") "chicken" else category)
    }


    fun toggleViewMode() {
        _isGridView.value = !_isGridView.value
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _simulatedState.value = SimulationState.REAL_API
        showToast("Menghapus query pencarian")
        fetchRecipes("chicken")
    }

    fun showOfflineCache() {
        showToast("Memuat 12 resep dari Room Database lokal")
        _simulatedState.value = SimulationState.REAL_API
        fetchRecipes("beef")
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }


    fun fetchRecipes(query: String) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            repository.searchRecipes(query)
                .onSuccess { recipes ->
                    if (recipes.isEmpty()) {
                        _uiState.value = HomeUiState.Empty(query)
                    } else {
                        _uiState.value = HomeUiState.Success(recipes)
                    }
                }
                .onFailure { throwable ->
                    _uiState.value = HomeUiState.Error(
                        throwable.localizedMessage ?: "Terjadi kesalahan saat memuat data resep."
                    )
                }
        }
    }
}


class HomeViewModelFactory(
    private val repository: RecipeRepository = RecipeRepositoryImpl()
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
