package com.example.data

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest

enum class IconCategory(val displayName: String, val systemIcon: ImageVector) {
    SYSTEM("System", Icons.Default.Android),
    COMMUNICATION("Communication", Icons.Default.Chat),
    INTERNET("Internet", Icons.Default.Language),
    MEDIA("Media", Icons.Default.PlayCircle),
    GAMES("Games", Icons.Default.SportsEsports),
    PRODUCTIVITY("Productivity", Icons.Default.Assignment),
    UTILITY("Utility", Icons.Default.Build)
}

data class PapirusIcon(
    val id: String,
    val name: String,
    val category: IconCategory,
    val packageName: String,
    val componentName: String,
    val contentDescription: String,
    val svgFileName: String? = null,
    val githubSvgName: String? = null,
    val fallbackRender: (@Composable (modifier: Modifier) -> Unit)? = null
) {
    @Composable
    fun Render(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        val assetUrl = svgFileName?.let { "file:///android_asset/papirus/$it" }
        val githubUrl = githubSvgName?.let { "https://raw.githubusercontent.com/PapirusDevelopmentTeam/papirus-icon-theme/master/Papirus/64x64/apps/$it" }
        val primarySource = assetUrl ?: githubUrl

        if (primarySource != null) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(primarySource)
                    .decoderFactory(SvgDecoder.Factory())
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                modifier = modifier
            )
        } else if (fallbackRender != null) {
            fallbackRender.invoke(modifier)
        }
    }
}

fun Modifier.papirusShadow(): Modifier = this.drawBehind {
    val width = size.width
    val height = size.height
    drawRect(
        color = Color.Black.copy(alpha = 0.12f),
        topLeft = Offset(width * 0.06f, height * 0.06f),
        size = size
    )
}

val PapirusIconList = listOf(
    PapirusIcon(
        id = "firefox",
        name = "Firefox",
        category = IconCategory.INTERNET,
        packageName = "org.mozilla.firefox",
        componentName = "org.mozilla.firefox.App",
        contentDescription = "Official Linux Papirus Firefox Icon",
        svgFileName = "firefox.svg",
        githubSvgName = "firefox.svg"
    ),
    PapirusIcon(
        id = "google-chrome",
        name = "Chrome",
        category = IconCategory.INTERNET,
        packageName = "com.android.chrome",
        componentName = "com.google.android.apps.chrome.Main",
        contentDescription = "Official Linux Papirus Google Chrome Icon",
        svgFileName = "google-chrome.svg",
        githubSvgName = "google-chrome.svg"
    ),
    PapirusIcon(
        id = "telegram",
        name = "Telegram",
        category = IconCategory.COMMUNICATION,
        packageName = "org.telegram.messenger",
        componentName = "org.telegram.ui.LaunchActivity",
        contentDescription = "Official Linux Papirus Telegram Icon",
        svgFileName = "telegram.svg",
        githubSvgName = "telegram.svg"
    ),
    PapirusIcon(
        id = "discord",
        name = "Discord",
        category = IconCategory.COMMUNICATION,
        packageName = "com.discord",
        componentName = "com.discord.main.MainActivity",
        contentDescription = "Official Linux Papirus Discord Icon",
        svgFileName = "discord.svg",
        githubSvgName = "discord.svg"
    ),
    PapirusIcon(
        id = "spotify",
        name = "Spotify",
        category = IconCategory.MEDIA,
        packageName = "com.spotify.music",
        componentName = "com.spotify.music.MainActivity",
        contentDescription = "Official Linux Papirus Spotify Icon",
        svgFileName = "spotify.svg",
        githubSvgName = "spotify.svg"
    ),
    PapirusIcon(
        id = "vlc",
        name = "VLC Player",
        category = IconCategory.MEDIA,
        packageName = "org.videolan.vlc",
        componentName = "org.videolan.vlc.StartActivity",
        contentDescription = "Official Linux Papirus VLC Icon",
        svgFileName = "vlc.svg",
        githubSvgName = "vlc.svg"
    ),
    PapirusIcon(
        id = "gimp",
        name = "GIMP",
        category = IconCategory.UTILITY,
        packageName = "org.gimp.GIMP",
        componentName = "org.gimp.GIMP.MainActivity",
        contentDescription = "Official Linux Papirus GIMP Icon",
        svgFileName = "gimp.svg",
        githubSvgName = "gimp.svg"
    ),
    PapirusIcon(
        id = "inkscape",
        name = "Inkscape",
        category = IconCategory.UTILITY,
        packageName = "org.inkscape.Inkscape",
        componentName = "org.inkscape.Inkscape.MainActivity",
        contentDescription = "Official Linux Papirus Inkscape Icon",
        svgFileName = "inkscape.svg",
        githubSvgName = "inkscape.svg"
    ),
    PapirusIcon(
        id = "steam",
        name = "Steam",
        category = IconCategory.GAMES,
        packageName = "com.valvesoftware.android.steam.community",
        componentName = "com.valvesoftware.android.steam.community.MainActivity",
        contentDescription = "Official Linux Papirus Steam Icon",
        svgFileName = "steam.svg",
        githubSvgName = "steam.svg"
    ),
    PapirusIcon(
        id = "vscode",
        name = "VS Code",
        category = IconCategory.PRODUCTIVITY,
        packageName = "com.microsoft.vscode",
        componentName = "com.microsoft.vscode.MainActivity",
        contentDescription = "Official Linux Papirus VS Code Icon",
        svgFileName = "visual-studio-code.svg",
        githubSvgName = "visual-studio-code.svg"
    ),
    PapirusIcon(
        id = "terminal",
        name = "Terminal",
        category = IconCategory.UTILITY,
        packageName = "com.termux",
        componentName = "com.termux.app.TermuxActivity",
        contentDescription = "Official Linux Papirus Terminal Icon",
        svgFileName = "utilities-terminal.svg",
        githubSvgName = "utilities-terminal.svg"
    ),
    PapirusIcon(
        id = "files",
        name = "File Manager",
        category = IconCategory.SYSTEM,
        packageName = "com.google.android.apps.docs",
        componentName = "com.google.android.apps.docs.app.NewMainProxyActivity",
        contentDescription = "Official Linux Papirus File Manager Icon",
        svgFileName = "system-file-manager.svg",
        githubSvgName = "system-file-manager.svg"
    ),
    PapirusIcon(
        id = "libreoffice-writer",
        name = "Writer",
        category = IconCategory.PRODUCTIVITY,
        packageName = "org.libreoffice.androidapp",
        componentName = "org.libreoffice.androidapp.MainActivity",
        contentDescription = "Official Linux Papirus LibreOffice Writer Icon",
        svgFileName = "libreoffice-writer.svg",
        githubSvgName = "libreoffice-writer.svg"
    ),
    PapirusIcon(
        id = "libreoffice-calc",
        name = "Calc",
        category = IconCategory.PRODUCTIVITY,
        packageName = "org.libreoffice.androidapp.calc",
        componentName = "org.libreoffice.androidapp.calc.MainActivity",
        contentDescription = "Official Linux Papirus LibreOffice Calc Icon",
        svgFileName = "libreoffice-calc.svg",
        githubSvgName = "libreoffice-calc.svg"
    ),
    PapirusIcon(
        id = "blender",
        name = "Blender 3D",
        category = IconCategory.UTILITY,
        packageName = "org.blender.blender",
        componentName = "org.blender.blender.MainActivity",
        contentDescription = "Official Linux Papirus Blender Icon",
        svgFileName = "blender.svg",
        githubSvgName = "blender.svg"
    ),
    PapirusIcon(
        id = "obs",
        name = "OBS Studio",
        category = IconCategory.MEDIA,
        packageName = "com.obsproject.studio",
        componentName = "com.obsproject.studio.MainActivity",
        contentDescription = "Official Linux Papirus OBS Studio Icon",
        svgFileName = "obs.svg",
        githubSvgName = "obs.svg"
    ),
    PapirusIcon(
        id = "audacity",
        name = "Audacity",
        category = IconCategory.MEDIA,
        packageName = "org.audacityteam.audacity",
        componentName = "org.audacityteam.audacity.MainActivity",
        contentDescription = "Official Linux Papirus Audacity Icon",
        svgFileName = "audacity.svg",
        githubSvgName = "audacity.svg"
    ),
    PapirusIcon(
        id = "android-studio",
        name = "Android Studio",
        category = IconCategory.PRODUCTIVITY,
        packageName = "com.google.android.studio",
        componentName = "com.google.android.studio.MainActivity",
        contentDescription = "Official Linux Papirus Android Studio Icon",
        svgFileName = "android-studio.svg",
        githubSvgName = "android-studio.svg"
    ),
    PapirusIcon(
        id = "twitter",
        name = "X / Twitter",
        category = IconCategory.COMMUNICATION,
        packageName = "com.twitter.android",
        componentName = "com.twitter.android.StartActivity",
        contentDescription = "Official Linux Papirus Twitter Icon",
        svgFileName = "twitter.svg",
        githubSvgName = "twitter.svg"
    ),
    PapirusIcon(
        id = "settings",
        name = "Settings",
        category = IconCategory.SYSTEM,
        packageName = "com.android.settings",
        componentName = "com.android.settings.Settings",
        contentDescription = "Official Linux Papirus Settings Icon",
        svgFileName = "preferences-system.svg",
        githubSvgName = "preferences-system.svg"
    ),
    PapirusIcon(
        id = "calendar",
        name = "Calendar",
        category = IconCategory.PRODUCTIVITY,
        packageName = "com.google.android.calendar",
        componentName = "com.android.calendar.LaunchActivity",
        contentDescription = "Official Linux Papirus Calendar Icon",
        svgFileName = "office-calendar.svg",
        githubSvgName = "office-calendar.svg"
    ),
    PapirusIcon(
        id = "music",
        name = "Audio Player",
        category = IconCategory.MEDIA,
        packageName = "com.google.android.music",
        componentName = "com.google.android.music.AudioPreview",
        contentDescription = "Official Linux Papirus Audio Player Icon",
        svgFileName = "multimedia-audio-player.svg",
        githubSvgName = "multimedia-audio-player.svg"
    ),
    PapirusIcon(
        id = "email",
        name = "Mail",
        category = IconCategory.COMMUNICATION,
        packageName = "com.google.android.gm",
        componentName = "com.google.android.gm.ConversationListActivityGmail",
        contentDescription = "Official Linux Papirus Mail Icon",
        svgFileName = "internet-mail.svg",
        githubSvgName = "internet-mail.svg"
    ),
    PapirusIcon(
        id = "notes",
        name = "Keep Notes",
        category = IconCategory.PRODUCTIVITY,
        packageName = "com.google.android.keep",
        componentName = "com.google.android.keep.activities.BrowseActivity",
        contentDescription = "Official Linux Papirus Keep Notes Icon",
        svgFileName = "keep.svg",
        githubSvgName = "keep.svg"
    )
)
