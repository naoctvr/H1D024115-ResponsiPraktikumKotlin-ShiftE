package com.example.katalogresep.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.katalogresep.ui.components.AppTopHeader
import com.example.katalogresep.ui.components.CategoryFilterRow
import com.example.katalogresep.ui.components.EmptyView
import com.example.katalogresep.ui.components.ErrorView
import com.example.katalogresep.ui.components.FeaturedHeroCard
import com.example.katalogresep.ui.components.HorizontalRecipeCard
import com.example.katalogresep.ui.components.LoadingView
import com.example.katalogresep.ui.components.ProfileDialog
import com.example.katalogresep.ui.components.RecipeCard
import com.example.katalogresep.ui.components.ResepKuBottomNavBar
import com.example.katalogresep.ui.components.SearchBarComponent
import kotlinx.coroutines.delay


@Composable
fun HomeScreen(
    onRecipeClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val isGridView by viewModel.isGridView.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteRecipeIds.collectAsStateWithLifecycle()
    val savedIds by viewModel.savedRecipeIds.collectAsStateWithLifecycle()
    val favoriteRecipes by viewModel.favoriteRecipes.collectAsStateWithLifecycle()
    val savedRecipes by viewModel.savedRecipes.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()

    var showProfileDialog by remember { mutableStateOf(false) }

    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            delay(2500)
            viewModel.clearToast()
        }
    }

    if (showProfileDialog) {
        ProfileDialog(onDismiss = { showProfileDialog = false })
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopHeader(
                title = "ResepKu",
                subtitle = when (selectedTab) {
                    "kategori" -> "Kategori Masakan"
                    "favorit" -> "Resep Favorit Saya"
                    "simpanan" -> "Resep Simpan"
                    else -> "Eksplorasi"
                },
                onProfileClick = { showProfileDialog = true }
            )
        },
        bottomBar = {
            ResepKuBottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = viewModel::selectTab
            )
        },
        floatingActionButton = {
            if (selectedTab == "eksplorasi" && uiState is HomeUiState.Success) {
                ExtendedFloatingActionButton(
                    onClick = {
                        val recipes = (uiState as? HomeUiState.Success)?.recipes
                        if (!recipes.isNullOrEmpty()) {
                            onRecipeClick(recipes.first().id)
                        } else {
                            viewModel.showToast("Mode Masak Diaktifkan")
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.SoupKitchen,
                            contentDescription = "Mode Masak",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    text = {
                        Text(
                            text = "Mode Masak",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(if (isGridView) 2 else 1),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (selectedTab == "eksplorasi") {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        SearchBarComponent(
                            query = searchQuery,
                            onQueryChange = viewModel::onSearchQueryChanged,
                            onSearchAction = viewModel::performSearch,
                            onFilterClick = viewModel::toggleViewMode
                        )
                    }
                }

                if (selectedTab == "kategori") {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        CategoryFilterRow(
                            categories = viewModel.categories,
                            selectedCategory = selectedCategory,
                            onCategorySelected = viewModel::selectCategory,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }

                when (uiState) {
                    is HomeUiState.Loading -> {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            LoadingView()
                        }
                    }

                    is HomeUiState.Empty -> {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            EmptyView(
                                query = searchQuery,
                                onSuggestedQueryClick = viewModel::onSearchQueryChanged,
                                onClearSearchClick = viewModel::clearSearch,
                                onExploreAllClick = viewModel::clearSearch
                            )
                        }
                    }

                    is HomeUiState.Error -> {
                        val errorState = uiState as HomeUiState.Error
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            ErrorView(
                                message = errorState.message,
                                onRetry = { viewModel.performSearch() },
                                onOfflineCacheClick = viewModel::showOfflineCache
                            )
                        }
                    }

                    is HomeUiState.Success -> {
                        val recipes = (uiState as HomeUiState.Success).recipes

                        when (selectedTab) {
                            "favorit" -> {
                                val favList = favoriteRecipes.ifEmpty { recipes.filter { favoriteIds.contains(it.id) } }
                                if (favList.isEmpty()) {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        EmptyView(
                                            query = "Resep Favorit",
                                            onExploreAllClick = { viewModel.selectTab("eksplorasi") }
                                        )
                                    }
                                } else {
                                    if (isGridView) {
                                        items(favList, key = { "fav_${it.id}" }) { recipe ->
                                            RecipeCard(
                                                recipe = recipe,
                                                onClick = { onRecipeClick(recipe.id) },
                                                isFavorite = true,
                                                onFavoriteToggle = { viewModel.toggleFavorite(recipe) },
                                                isSaved = savedIds.contains(recipe.id),
                                                onSaveToggle = { viewModel.toggleSave(recipe) },
                                                isGridView = true
                                            )
                                        }
                                    } else {
                                        items(favList, key = { "fav_${it.id}" }, span = { _ -> GridItemSpan(maxLineSpan) }) { recipe ->
                                            HorizontalRecipeCard(
                                                recipe = recipe,
                                                onClick = { onRecipeClick(recipe.id) },
                                                isFavorite = true,
                                                isSaved = savedIds.contains(recipe.id),
                                                onFavoriteToggle = { viewModel.toggleFavorite(recipe) },
                                                onSaveToggle = { viewModel.toggleSave(recipe) }
                                            )
                                        }
                                    }
                                }
                            }

                            "simpanan" -> {
                                val savedList = savedRecipes.ifEmpty { recipes.filter { savedIds.contains(it.id) } }
                                if (savedList.isEmpty()) {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        EmptyView(
                                            query = "Resep Simpan",
                                            onExploreAllClick = { viewModel.selectTab("eksplorasi") }
                                        )
                                    }
                                } else {
                                    if (isGridView) {
                                        items(savedList, key = { "saved_${it.id}" }) { recipe ->
                                            RecipeCard(
                                                recipe = recipe,
                                                onClick = { onRecipeClick(recipe.id) },
                                                isFavorite = favoriteIds.contains(recipe.id),
                                                onFavoriteToggle = { viewModel.toggleFavorite(recipe) },
                                                isSaved = true,
                                                onSaveToggle = { viewModel.toggleSave(recipe) },
                                                isGridView = true
                                            )
                                        }
                                    } else {
                                        items(savedList, key = { "saved_${it.id}" }, span = { _ -> GridItemSpan(maxLineSpan) }) { recipe ->
                                            HorizontalRecipeCard(
                                                recipe = recipe,
                                                onClick = { onRecipeClick(recipe.id) },
                                                isFavorite = favoriteIds.contains(recipe.id),
                                                isSaved = true,
                                                onFavoriteToggle = { viewModel.toggleFavorite(recipe) },
                                                onSaveToggle = { viewModel.toggleSave(recipe) }
                                            )
                                        }
                                    }
                                }
                            }

                            else -> {
                                if (recipes.isNotEmpty()) {
                                    if (selectedTab == "eksplorasi") {
                                        // Section 1: Hero Featured Meal Card
                                        val heroRecipe = recipes.first()
                                        item(span = { GridItemSpan(maxLineSpan) }) {
                                            Column {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(bottom = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.LocalFireDepartment,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(22.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "Rekomendasi Spesial Hari Ini",
                                                        style = MaterialTheme.typography.titleLarge,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }

                                                FeaturedHeroCard(
                                                    recipe = heroRecipe,
                                                    onClick = { onRecipeClick(heroRecipe.id) },
                                                    isFavorite = favoriteIds.contains(heroRecipe.id),
                                                    onFavoriteToggle = { viewModel.toggleFavorite(heroRecipe) },
                                                    isSaved = savedIds.contains(heroRecipe.id),
                                                    onSaveToggle = { viewModel.toggleSave(heroRecipe) }
                                                )
                                            }
                                        }

                                        item(span = { GridItemSpan(maxLineSpan) }) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 8.dp, bottom = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.RestaurantMenu,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.secondary,
                                                        modifier = Modifier.size(22.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "Eksplorasi Resep Populer",
                                                        style = MaterialTheme.typography.titleLarge,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }

                                                Text(
                                                    text = "${recipes.size} Resep",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        val popularRecipes = recipes.drop(1)
                                        items(popularRecipes, key = { it.id }) { recipe ->
                                            RecipeCard(
                                                recipe = recipe,
                                                onClick = { onRecipeClick(recipe.id) },
                                                isFavorite = favoriteIds.contains(recipe.id),
                                                onFavoriteToggle = { viewModel.toggleFavorite(recipe) },
                                                isSaved = savedIds.contains(recipe.id),
                                                onSaveToggle = { viewModel.toggleSave(recipe) },
                                                isGridView = isGridView
                                            )
                                        }
                                    } else {
                                        items(recipes, key = { "cat_${it.id}" }) { recipe ->
                                            RecipeCard(
                                                recipe = recipe,
                                                onClick = { onRecipeClick(recipe.id) },
                                                isFavorite = favoriteIds.contains(recipe.id),
                                                onFavoriteToggle = { viewModel.toggleFavorite(recipe) },
                                                isSaved = savedIds.contains(recipe.id),
                                                onSaveToggle = { viewModel.toggleSave(recipe) },
                                                isGridView = isGridView
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = toastMessage != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                toastMessage?.let { msg ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.inverseSurface,
                        contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                        shadowElevation = 6.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.inversePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
