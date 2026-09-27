package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Stage
import com.example.ui.components.CrtScanlinesOverlay
import com.example.ui.components.RetroBadge
import com.example.ui.screens.BattleScreen
import com.example.ui.screens.CloudSaveScreen
import com.example.ui.screens.DungeonMapScreen
import com.example.ui.screens.HeroRosterScreen
import com.example.ui.screens.PaidUpgradeDialog
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.screens.SkillTreeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RetroCardBorder
import com.example.ui.theme.RetroDarkBg
import com.example.ui.theme.RetroGold
import com.example.ui.theme.RetroManaBlue
import com.example.ui.theme.RetroRuby
import com.example.ui.theme.RetroSurface
import com.example.ui.theme.RetroSurfaceVariant
import com.example.ui.viewmodel.GameViewModel

enum class MainNavScreen(val title: String, val icon: ImageVector) {
    DUNGEON("Dungeon", Icons.Default.Map),
    HEROES("Heroes", Icons.Default.Group),
    SKILLS("Skills", Icons.Default.AutoAwesome),
    SHOP("Shop", Icons.Default.ShoppingBag),
    CLOUD("Cloud", Icons.Default.CloudSync),
    SETTINGS("Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                IntrastApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntrastApp(viewModel: GameViewModel) {
    val context = LocalContext.current
    val profile by viewModel.profile.collectAsState()
    val combatState by viewModel.combatState.collectAsState()
    val isUpgradeDialogOpen by viewModel.isUpgradeDialogOpen.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    var currentScreen by remember { mutableStateOf(MainNavScreen.DUNGEON) }
    val isPaid = profile?.isPaidVersion == true
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.statusMessage.value = null
        }
    }

    BackHandler(enabled = currentScreen != MainNavScreen.DUNGEON && combatState == null) {
        currentScreen = MainNavScreen.DUNGEON
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                if (combatState == null) {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth().padding(end = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "INTRAST",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 17.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = RetroGold,
                                        letterSpacing = 2.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    if (isPaid) {
                                        RetroBadge("PRO", backgroundColor = RetroRuby, textColor = Color.White)
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "🪙 ${profile?.gold ?: 0}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = RetroGold
                                    )
                                    Text(
                                        text = "💎 ${profile?.gems ?: 0}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = RetroManaBlue
                                    )

                                    if (!isPaid) {
                                        Box(
                                            modifier = Modifier
                                                .testTag("topbar_upgrade_badge")
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(RetroRuby)
                                                .clickable { viewModel.isUpgradeDialogOpen.value = true }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "PRO ₹99",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = RetroDarkBg
                        )
                    )
                }
            },
            bottomBar = {
                if (combatState == null) {
                    NavigationBar(
                        containerColor = RetroSurface,
                        tonalElevation = 8.dp
                    ) {
                        MainNavScreen.entries.forEach { screen ->
                            val isSelected = currentScreen == screen
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { currentScreen = screen },
                                icon = {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = screen.title,
                                        tint = if (isSelected) RetroGold else Color(0xFF94A3B8),
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = screen.title,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) RetroGold else Color(0xFF94A3B8)
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = RetroSurfaceVariant
                                ),
                                modifier = Modifier.testTag("nav_${screen.name.lowercase()}")
                            )
                        }
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = RetroDarkBg
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (combatState != null) {
                    BattleScreen(
                        viewModel = viewModel,
                        onBattleExit = {
                            currentScreen = MainNavScreen.DUNGEON
                        }
                    )
                } else {
                    when (currentScreen) {
                        MainNavScreen.DUNGEON -> DungeonMapScreen(
                            viewModel = viewModel,
                            onStartStage = { stage ->
                                viewModel.startBattle(stage)
                            },
                            onOpenUpgrade = { viewModel.isUpgradeDialogOpen.value = true }
                        )

                        MainNavScreen.HEROES -> HeroRosterScreen(
                            viewModel = viewModel,
                            onOpenUpgrade = { viewModel.isUpgradeDialogOpen.value = true }
                        )

                        MainNavScreen.SKILLS -> SkillTreeScreen(
                            viewModel = viewModel,
                            onOpenUpgrade = { viewModel.isUpgradeDialogOpen.value = true }
                        )

                        MainNavScreen.SHOP -> ShopScreen(viewModel = viewModel)

                        MainNavScreen.CLOUD -> CloudSaveScreen(
                            viewModel = viewModel,
                            onOpenUpgrade = { viewModel.isUpgradeDialogOpen.value = true }
                        )

                        MainNavScreen.SETTINGS -> SettingsScreen(
                            viewModel = viewModel,
                            onOpenUpgrade = { viewModel.isUpgradeDialogOpen.value = true }
                        )
                    }
                }
            }
        }

        // CRT Scanline Shader Overlay (if enabled)
        if (profile?.crtScanlines == true) {
            CrtScanlinesOverlay(modifier = Modifier.matchParentSize())
        }

        // Paid / Pro Edition Upgrade Dialog
        if (isUpgradeDialogOpen) {
            PaidUpgradeDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.isUpgradeDialogOpen.value = false }
            )
        }
    }
}
