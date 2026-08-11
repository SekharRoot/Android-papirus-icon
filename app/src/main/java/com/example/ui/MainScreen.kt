package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.IconCategory
import com.example.data.PapirusIcon
import com.example.data.PapirusIconList
import com.example.data.papirusShadow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class Screen(val title: String, val icon: ImageVector, val tag: String) {
    ICONS("Icons", Icons.Default.GridOn, "nav_icons_tab"),
    APPLY("Apply", Icons.Default.CheckCircle, "nav_apply_tab"),
    REQUEST("Requests", Icons.Default.Send, "nav_requests_tab")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: IconPackViewModel) {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(Screen.ICONS) }
    var selectedIconForDetail by remember { mutableStateOf<PapirusIcon?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Load installed apps when transitioning to requests screen
    LaunchedEffect(currentScreen) {
        if (currentScreen == Screen.REQUEST) {
            viewModel.loadInstalledApps(context)
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                Screen.values().forEach { screen ->
                    val selected = currentScreen == screen
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentScreen = screen },
                        label = {
                            Text(
                                text = screen.title,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = "${screen.title} Screen"
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag(screen.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.ICONS -> IconsScreen(
                    viewModel = viewModel,
                    onIconClick = { selectedIconForDetail = it }
                )
                Screen.APPLY -> ApplyScreen()
                Screen.REQUEST -> RequestScreen(viewModel = viewModel)
            }
        }

        // Beautiful bottom sheet for displaying Icon details
        if (selectedIconForDetail != null) {
            val icon = selectedIconForDetail!!
            val favoriteIds by viewModel.favoriteIconIds.collectAsState()
            val isFavorite = favoriteIds.contains(icon.id)

            ModalBottomSheet(
                onDismissRequest = { selectedIconForDetail = null },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .size(width = 40.dp, height = 4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                    )
                }
            ) {
                IconDetailSheetContent(
                    icon = icon,
                    isFavorite = isFavorite,
                    onToggleFavorite = { viewModel.toggleFavorite(icon.id) },
                    onClose = { selectedIconForDetail = null }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IconsScreen(
    viewModel: IconPackViewModel,
    onIconClick: (PapirusIcon) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val filteredIcons by viewModel.filteredIcons.collectAsState()
    val favorites by viewModel.favoriteIconIds.collectAsState()

    var showOnlyFavorites by remember { mutableStateOf(false) }

    val displayIcons = if (showOnlyFavorites) {
        filteredIcons.filter { favorites.contains(it.id) }
    } else {
        filteredIcons
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // App header and branding
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Papirus Icons",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Vibrant open-source flat design theme",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }

                // Favorites Filter Switch
                IconButton(
                    onClick = { showOnlyFavorites = !showOnlyFavorites },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (showOnlyFavorites) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                        .testTag("filter_favorites_button")
                ) {
                    Icon(
                        imageVector = if (showOnlyFavorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Show Favorites",
                        tint = if (showOnlyFavorites) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search 2,500+ Papirus icons...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear Search")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("icon_search_input"),
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                )
            )
        }

        // Horizontal Category Filter Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // "All" filter chip
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { viewModel.selectCategory(null) },
                            label = { Text("All") },
                            leadingIcon = if (selectedCategory == null) {
                                { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.testTag("category_all_chip")
                        )

                        IconCategory.values().forEach { category ->
                            val isSelected = selectedCategory == category
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectCategory(category) },
                                label = { Text(category.displayName) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (isSelected) Icons.Default.Check else category.systemIcon,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.testTag("category_${category.name.lowercase()}_chip")
                            )
                        }
                    }
                }
            }
        }

        // Grid listing icons
        if (displayIcons.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (showOnlyFavorites) Icons.Default.FavoriteBorder else Icons.Default.Search,
                        contentDescription = "Empty list",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (showOnlyFavorites) "No Favorite Icons Yet" else "No matching icons found",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (showOnlyFavorites) "Browse the collection and tap the heart icon on any design to add it here!"
                        else "Try querying something else or check your category filters.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 4.dp)
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 74.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp)
                    .testTag("icons_grid"),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayIcons, key = { it.id }) { icon ->
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .combinedClickable(
                                onClick = { onIconClick(icon) },
                                onLongClick = { onIconClick(icon) }
                            )
                            .padding(vertical = 8.dp)
                            .testTag("icon_item_${icon.id}"),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Render dynamic Composable Papirus Icon
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .papirusShadow(),
                            contentAlignment = Alignment.Center
                        ) {
                            icon.Render(Modifier.fillMaxSize())
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = icon.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IconDetailSheetContent(
    icon: PapirusIcon,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    val copyToClipboard = { label: String, text: String ->
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied $label to clipboard", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp, top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Heading details
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Icon Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close Sheet")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Hero Render
        Box(
            modifier = Modifier
                .size(112.dp)
                .papirusShadow(),
            contentAlignment = Alignment.Center
        ) {
            icon.Render(Modifier.fillMaxSize())
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = icon.name,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = icon.category.displayName,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Technical properties (Package info, Activity info)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Package name field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Target Package",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = icon.packageName,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(onClick = { copyToClipboard("Package Name", icon.packageName) }) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy Package", modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Component / launcher activity field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Launcher Activity Component",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = icon.componentName,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(onClick = { copyToClipboard("Launcher Component", icon.componentName) }) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy Component", modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Actions: Toggle Favorite, Copy XML line
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("sheet_favorite_button"),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isFavorite) "Favorited" else "Favorite")
            }

            Button(
                onClick = {
                    val xmlSnippet = "<item component=\"ComponentInfo{${icon.packageName}/${icon.componentName}}\" drawable=\"${icon.id}\"/>"
                    copyToClipboard("XML mapping config", xmlSnippet)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("sheet_copy_xml_button"),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Copy XML")
            }
        }
    }
}

@Composable
fun ApplyScreen() {
    val context = LocalContext.current

    val launchers = listOf(
        LauncherInfo("Nova Launcher", "com.teslacoilsw.launcher", "com.novalauncher.THEME_APPLY"),
        LauncherInfo("Lawnchair", "ch.deletescape.lawnchair.plah", "ch.deletescape.lawnchair.APPLY_THEME"),
        LauncherInfo("Smart Launcher", "com.smartlauncher.recents", "com.smartlauncher.action.APPLY_THEME"),
        LauncherInfo("Action Launcher", "com.actionlauncher.playstore", "com.actionlauncher.THEME_APPLY"),
        LauncherInfo("Niagara Launcher", "bitpit.launcher", "bitpit.launcher.APPLY_THEME"),
        LauncherInfo("OnePlus Launcher", "com.oneplus.launcher", "com.oneplus.launcher.THEME_APPLY"),
        LauncherInfo("Apex Launcher", "com.anddoes.launcher", "com.anddoes.launcher.THEME_APPLY"),
        LauncherInfo("Go Launcher", "com.jiubang.go.launcher", "com.jiubang.go.launcher.THEME_APPLY")
    )

    val triggerApply = { launcher: LauncherInfo ->
        val intent = Intent(launcher.action).apply {
            // Include package references that typical launchers expect to apply the pack
            putExtra("com.novalauncher.theme.extra.ICON_THEME_PACKAGE", context.packageName)
            putExtra("org.adw.launcher.theme.ICON_THEME_PACKAGE", context.packageName)
            putExtra("icon_pack_package", context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
            Toast.makeText(context, "Directing to ${launcher.name} settings...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Launcher is likely not installed, show fallback instructions
            val pm = context.packageManager
            val isInstalled = try {
                pm.getPackageInfo(launcher.packageName, 0)
                true
            } catch (ne: PackageManager.NameNotFoundException) {
                false
            }

            if (isInstalled) {
                Toast.makeText(context, "Direct application not supported. Please open ${launcher.name} settings to select Papirus Icons.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "${launcher.name} is not installed on this device.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("apply_screen"),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Apply Icon Pack",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Select your current launcher to easily apply the Papirus icon theme",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }

        // Instructions Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Instructions",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Manual Installation Guide",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "If your launcher is not listed or direct apply fails: Go to your home screen settings -> Icon options -> Choose 'Papirus Icons'.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Supported Launchers",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Display list of Launchers
        items(launchers) { launcher ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { triggerApply(launcher) }
                    .testTag("launcher_item_${launcher.name.lowercase().replace(" ", "_")}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Android,
                                contentDescription = launcher.name,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = launcher.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = launcher.packageName,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Apply",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

data class LauncherInfo(val name: String, val packageName: String, val action: String)

@Composable
fun RequestScreen(viewModel: IconPackViewModel) {
    val displayApps by viewModel.displayInstalledApps.collectAsState()
    val isLoading by viewModel.isLoadingApps.collectAsState()
    val searchAppsQuery by viewModel.appsSearchQuery.collectAsState()
    val requestHistory by viewModel.requestedPackages.collectAsState()

    var showHistorySection by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val selectedCount = displayApps.count { it.isSelected }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Icon Requests",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Select apps on your device to request Papirus styled icons",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }

                // History toggle button
                IconButton(
                    onClick = { showHistorySection = !showHistorySection },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (showHistorySection) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                        .testTag("toggle_history_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Request History",
                        tint = if (showHistorySection) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Apps Search bar
            if (!showHistorySection) {
                OutlinedTextField(
                    value = searchAppsQuery,
                    onValueChange = { viewModel.updateAppsSearchQuery(it) },
                    placeholder = { Text("Search installed applications...") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchAppsQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateAppsSearchQuery("") }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear Search")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("app_search_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Selection helpers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$selectedCount selected to request",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Select All",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable { viewModel.setAllAppsSelection(true) }
                                .padding(vertical = 4.dp, horizontal = 8.dp)
                                .testTag("select_all_apps_button")
                        )
                        Text(
                            text = "Clear All",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .clickable { viewModel.setAllAppsSelection(false) }
                                .padding(vertical = 4.dp, horizontal = 8.dp)
                                .testTag("deselect_all_apps_button")
                        )
                    }
                }
            }
        }

        if (showHistorySection) {
            // Request History screen list
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "History (${requestHistory.size} requested)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (requestHistory.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.clearRequestHistory() },
                            modifier = Modifier.testTag("clear_history_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear History",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (requestHistory.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Empty History",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Request history is empty",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(requestHistory, key = { it.packageName }) { request ->
                            val formatter = remember { SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault()) }
                            val dateString = formatter.format(Date(request.timestamp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = request.appName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = request.packageName,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = dateString,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Requested",
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Main App selection List
            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Scanning device applications...", fontSize = 14.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("apps_list"),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(displayApps, key = { it.packageName }) { app ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                                    .clickable(enabled = !app.isAlreadyRequested) {
                                        viewModel.toggleAppSelection(app.packageName)
                                    }
                                    .testTag("app_item_${app.packageName}"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (app.isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                                    else if (app.isAlreadyRequested) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                                ),
                                border = if (app.isSelected) CardDefaults.outlinedCardBorder() else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (app.isAlreadyRequested) MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                                                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Android,
                                                contentDescription = app.appName,
                                                tint = if (app.isAlreadyRequested) MaterialTheme.colorScheme.outline
                                                else MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(14.dp))
                                        Column {
                                            Text(
                                                text = app.appName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = if (app.isAlreadyRequested) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                                else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = app.packageName,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    if (app.isAlreadyRequested) {
                                        Text(
                                            text = "Requested",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF4CAF50),
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFE8F5E9))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    } else {
                                        Checkbox(
                                            checked = app.isSelected,
                                            onCheckedChange = { viewModel.toggleAppSelection(app.packageName) },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = MaterialTheme.colorScheme.primary,
                                                uncheckedColor = MaterialTheme.colorScheme.outline
                                            ),
                                            modifier = Modifier.testTag("app_checkbox_${app.packageName}")
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Floating Request button at the bottom
                    androidx.compose.animation.AnimatedVisibility(
                        visible = selectedCount > 0,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                    ) {
                        FloatingActionButton(
                            onClick = { viewModel.submitSelectedRequests(context) },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .widthIn(min = 200.dp)
                                .height(56.dp)
                                .testTag("submit_request_fab")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(imageVector = Icons.Default.Send, contentDescription = "Send Icon Requests")
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Send Request ($selectedCount apps)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
