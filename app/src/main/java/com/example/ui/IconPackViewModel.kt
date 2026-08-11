package com.example.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.IconCategory
import com.example.data.IconRepository
import com.example.data.PapirusIcon
import com.example.data.PapirusIconList
import com.example.data.RequestEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class InstalledAppInfo(
    val packageName: String,
    val appName: String,
    val isSelected: Boolean = false,
    val isAlreadyRequested: Boolean = false
)

class IconPackViewModel(private val repository: IconRepository) : ViewModel() {

    // Icon list search and filter states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<IconCategory?>(null)
    val selectedCategory: StateFlow<IconCategory?> = _selectedCategory.asStateFlow()

    // Room state flows
    val requestedPackages: StateFlow<List<RequestEntity>> = repository.allRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteIconIds: StateFlow<Set<String>> = repository.allFavorites
        .map { list -> list.map { it.iconId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // Filtered Icons list
    val filteredIcons: StateFlow<List<PapirusIcon>> = combine(
        _searchQuery,
        _selectedCategory
    ) { query, category ->
        PapirusIconList.filter { icon ->
            val matchesQuery = icon.name.contains(query, ignoreCase = true) ||
                    icon.packageName.contains(query, ignoreCase = true)
            val matchesCategory = category == null || icon.category == category
            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PapirusIconList)

    // Installed apps states
    private val _installedApps = MutableStateFlow<List<InstalledAppInfo>>(emptyList())
    val installedApps: StateFlow<List<InstalledAppInfo>> = _installedApps.asStateFlow()

    private val _isLoadingApps = MutableStateFlow(false)
    val isLoadingApps: StateFlow<Boolean> = _isLoadingApps.asStateFlow()

    private val _appsSearchQuery = MutableStateFlow("")
    val appsSearchQuery: StateFlow<String> = _appsSearchQuery.asStateFlow()

    // Map installed apps with search filter and request status
    val displayInstalledApps: StateFlow<List<InstalledAppInfo>> = combine(
        _installedApps,
        _appsSearchQuery,
        requestedPackages
    ) { apps, query, requests ->
        val requestedSet = requests.map { it.packageName }.toSet()
        apps.filter {
            it.appName.contains(query, ignoreCase = true) ||
                    it.packageName.contains(query, ignoreCase = true)
        }.map { app ->
            app.copy(
                isAlreadyRequested = requestedSet.contains(app.packageName)
            )
        }.sortedWith(compareBy<InstalledAppInfo> { it.isAlreadyRequested }
            .thenBy { it.appName })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: IconCategory?) {
        _selectedCategory.value = category
    }

    fun updateAppsSearchQuery(query: String) {
        _appsSearchQuery.value = query
    }

    // Toggle Favorite Action
    fun toggleFavorite(iconId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentFavs = favoriteIconIds.value
            if (currentFavs.contains(iconId)) {
                repository.removeFavorite(iconId)
            } else {
                repository.addFavorite(iconId)
            }
        }
    }

    // Load Installed Apps using PackageManager
    fun loadInstalledApps(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoadingApps.value = true
            try {
                val pm = context.packageManager
                val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                
                val filtered = packages.filter { app ->
                    val hasLaunchIntent = pm.getLaunchIntentForPackage(app.packageName) != null
                    val isNotSelf = app.packageName != context.packageName
                    hasLaunchIntent && isNotSelf
                }.map { app ->
                    InstalledAppInfo(
                        packageName = app.packageName,
                        appName = app.loadLabel(pm).toString()
                    )
                }

                if (filtered.isEmpty()) {
                    _installedApps.value = getFallbackApps()
                } else {
                    _installedApps.value = filtered
                }
            } catch (e: Exception) {
                _installedApps.value = getFallbackApps()
            } finally {
                _isLoadingApps.value = false
            }
        }
    }

    // Toggle app selection in request list
    fun toggleAppSelection(packageName: String) {
        val currentList = _installedApps.value
        _installedApps.value = currentList.map { app ->
            if (app.packageName == packageName) {
                app.copy(isSelected = !app.isSelected)
            } else {
                app
            }
        }
    }

    // Select or deselect all apps for request
    fun setAllAppsSelection(selected: Boolean) {
        val currentList = _installedApps.value
        val requestedSet = requestedPackages.value.map { it.packageName }.toSet()
        _installedApps.value = currentList.map { app ->
            if (requestedSet.contains(app.packageName)) {
                app.copy(isSelected = false) // Cannot select already requested
            } else {
                app.copy(isSelected = selected)
            }
        }
    }

    // Submit Selected Requests
    fun submitSelectedRequests(context: Context) {
        val selectedApps = _installedApps.value.filter { it.isSelected && !it.isAlreadyRequested }
        if (selectedApps.isEmpty()) {
            Toast.makeText(context, "No new apps selected to request", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val entities = selectedApps.map {
                RequestEntity(packageName = it.packageName, appName = it.appName)
            }
            repository.insertRequests(entities)

            // Reset local selection state
            _installedApps.value = _installedApps.value.map { it.copy(isSelected = false) }

            // Create share body
            val xmlBuilder = StringBuilder()
            xmlBuilder.append("<!-- Papirus Icon Pack Request list -->\n")
            selectedApps.forEach { app ->
                xmlBuilder.append("<item component=\"ComponentInfo{${app.packageName}/}\" name=\"${app.appName}\"/>\n")
            }

            // Share Intent
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Papirus Icon Request")
                putExtra(Intent.EXTRA_TEXT, "Hello Developer,\n\nI would love to request Papirus icons for the following apps:\n\n${xmlBuilder}\n\nThank you!")
            }

            viewModelScope.launch(Dispatchers.Main) {
                try {
                    val chooser = Intent.createChooser(shareIntent, "Send Icon Request Via")
                    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(chooser)
                    Toast.makeText(context, "Successfully requested ${selectedApps.size} icons!", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Icon requests recorded in local history", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Clear Request History
    fun clearRequestHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllRequests()
        }
    }

    // Standard fallback apps list in case packages can't be fetched or is empty (e.g. running inside sandbox)
    private fun getFallbackApps(): List<InstalledAppInfo> {
        return listOf(
            InstalledAppInfo("com.whatsapp", "WhatsApp"),
            InstalledAppInfo("com.instagram.android", "Instagram"),
            InstalledAppInfo("com.zhiliaoapp.musically", "TikTok"),
            InstalledAppInfo("com.netflix.mediaclient", "Netflix"),
            InstalledAppInfo("com.twitter.android", "X / Twitter"),
            InstalledAppInfo("com.snapchat.android", "Snapchat"),
            InstalledAppInfo("com.spotify.music", "Spotify"),
            InstalledAppInfo("com.pinterest", "Pinterest"),
            InstalledAppInfo("com.facebook.katana", "Facebook"),
            InstalledAppInfo("com.discord", "Discord"),
            InstalledAppInfo("com.slack", "Slack"),
            InstalledAppInfo("com.reddit.frontpage", "Reddit"),
            InstalledAppInfo("com.microsoft.teams", "Microsoft Teams"),
            InstalledAppInfo("com.linkedin.android", "LinkedIn"),
            InstalledAppInfo("com.amazon.mShop.android.shopping", "Amazon Shopping"),
            InstalledAppInfo("org.telegram.messenger", "Telegram"),
            InstalledAppInfo("com.valvesoftware.android.steam.community", "Steam"),
            InstalledAppInfo("com.supercell.clashofclans", "Clash of Clans")
        )
    }
}

class IconPackViewModelFactory(private val repository: IconRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IconPackViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IconPackViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
