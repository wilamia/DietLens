package com.example.dietlens.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dietlens.R
import com.example.dietlens.feature.profile.sheets.ChangeNameSheet
import com.example.dietlens.feature.profile.sheets.FavoritesSheet
import com.example.dietlens.feature.profile.sheets.HistorySheet
import com.example.dietlens.feature.profile.sheets.PreferencesSheet
import com.example.dietlens.theme.Buttons
import com.example.dietlens.theme.OnPrimary
import kotlinx.coroutines.launch


private sealed class SheetContent {
    data object None : SheetContent()
    data object Preferences : SheetContent()
    data object ChangeName : SheetContent()
    data object History : SheetContent()
    data object Favorites : SheetContent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onOpenProductDetails: (barcode: String) -> Unit,   // 👈 НОВОЕ
) {
    val state by viewModel.uiState.collectAsState()

    var sheet by remember { mutableStateOf<SheetContent>(SheetContent.None) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Никакой навигации на preferences — теперь это BottomSheet
    LaunchedEffect(state.navigationEvent) {
        when (state.navigationEvent) {
            is NavigationEvent.NavigateToLogin -> {
                viewModel.onNavigationEventHandled()
                onNavigateToLogin()
            }

            else -> Unit
        }
    }

    Surface(
        Modifier
            .fillMaxSize(),
        color = OnPrimary
    ) {
        ProfileScreenContent(
            name = state.userName,
            onPreferencesClick = { sheet = SheetContent.Preferences },
            onOpenFavorites = { sheet = SheetContent.Favorites },
            onOpenHistory = { sheet = SheetContent.History },
            onChangeNameClick = { sheet = SheetContent.ChangeName },
            onLogoutClick = { viewModel.onLogoutClick() },
            onDeleteAccountClick = { showDeleteDialog = true }
        )
    }
    if (showDeleteDialog) {
        ConfirmDeleteAccountDialog(
            isLoading = state.isLoading,
            onConfirm = {
                // жмём «Удалить» — шлём в VM
                viewModel.onDeleteAccountClick()
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
    if (sheet != SheetContent.None) {
        ModalBottomSheet(
            onDismissRequest = { sheet = SheetContent.None },
            sheetState = sheetState,
            containerColor = OnPrimary
        ) {
            when (sheet) {
                SheetContent.Preferences -> {
                    PreferencesSheet(
                        initialChecks = state.allergies,
                        isSaving = state.isSavingPreferences,
                        onSave = { viewModel.onSavePreferences(it) },
                        onClose = {
                            sheet = SheetContent.None
                            scope.launch { sheetState.hide() }
                        },
                        error = state.error
                    )
                }

                SheetContent.ChangeName -> {
                    ChangeNameSheet(
                        currentName = state.userName,
                        isLoading = state.isLoading,
                        onConfirm = { viewModel.onUpdateUserName(it) },
                        onClose = {
                            sheet = SheetContent.None
                            scope.launch { sheetState.hide() }
                        },
                        error = state.error
                    )
                }

                SheetContent.History -> {
                    HistorySheet(
                        items = state.history,
                        deletingIds = state.deletingHistoryIds,
                        onOpenDetails = { barcode ->
                            sheet = SheetContent.None
                            scope.launch { sheetState.hide() }
                            onOpenProductDetails(barcode)
                        },
                        onDelete = { docId -> viewModel.deleteHistoryItem(docId) },
                        onClose = {
                            sheet = SheetContent.None
                            scope.launch { sheetState.hide() }
                        }
                    )
                }

                SheetContent.Favorites -> {
                    FavoritesSheet(
                        items = state.favorites,
                        updatingIds = state.updatingFavoriteIds,
                        onToggle = { id, name, image -> viewModel.toggleFavorite(id, name, image) },
                        onOpenDetails = { id ->
                            sheet = SheetContent.None
                            scope.launch { sheetState.hide() }
                            onOpenProductDetails(id)
                        },
                        onClose = {
                            sheet = SheetContent.None
                            scope.launch { sheetState.hide() }
                        }
                    )
                }

                else -> Unit
            }
        }
    }
}


@Composable
fun ProfileScreenContent(
    name: String,
    onPreferencesClick: () -> Unit,
    onChangeNameClick: () -> Unit,
    onOpenFavorites: () -> Unit,
    onLogoutClick: () -> Unit,
    onOpenHistory: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)

            .background(OnPrimary),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.hi_profile, name),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Buttons,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        ProfileMenuItem(
            text = stringResource(R.string.preferences_profile),
            icon = Icons.Outlined.Settings,
            onClick = onPreferencesClick
        )

        ProfileMenuItem(
            text = stringResource(R.string.change_name_profile),
            icon = Icons.Outlined.Edit,
            onClick = onChangeNameClick
        )

        ProfileMenuItem(
            text = stringResource(R.string.liked_products),
            icon = Icons.Outlined.FavoriteBorder,
            onClick = onOpenFavorites
        )

        ProfileMenuItem(
            text = stringResource(R.string.profile_scan_history),
            icon = Icons.Outlined.History,
            onClick = onOpenHistory
        )

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        ProfileMenuItem(
            text = stringResource(R.string.logout),
            icon = Icons.Outlined.ExitToApp,
            onClick = onLogoutClick
        )

        ProfileMenuItem(
            text = stringResource(R.string.profile_delete_account),
            icon = Icons.Outlined.Delete,
            onClick = onDeleteAccountClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileMenuItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)

    Surface(
        shape = shape,
        color = Color.White,
        shadowElevation = 2.dp,
        tonalElevation = 0.dp,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        ListItem(
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            ),
            leadingContent = {
                Icon(imageVector = icon, contentDescription = null, tint = Buttons)
            },
            headlineContent = {
                Text(text = text, color = Color.Black, fontWeight = FontWeight.Medium)
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun ProfileScreenContentPreview() {
    Surface(modifier = Modifier.fillMaxSize()) {
        ProfileScreenContent(
            name = "Алексей (Preview)",
            onPreferencesClick = {},
            onChangeNameClick = {},
            onLogoutClick = {},
            onDeleteAccountClick = {},
            onOpenFavorites = {},
            onOpenHistory = {}
        )
    }
}
