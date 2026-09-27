package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HapticPulseType
import com.example.ui.components.RetroBadge
import com.example.ui.components.RetroButton
import com.example.ui.components.RetroCard
import com.example.ui.theme.RetroCardBorder
import com.example.ui.theme.RetroDarkBg
import com.example.ui.theme.RetroGold
import com.example.ui.theme.RetroGoldDark
import com.example.ui.theme.RetroManaBlue
import com.example.ui.theme.RetroRuby
import com.example.ui.theme.RetroSurface
import com.example.ui.theme.RetroSurfaceVariant
import com.example.ui.viewmodel.GameViewModel

@Composable
fun SettingsScreen(
    viewModel: GameViewModel,
    onOpenUpgrade: () -> Unit
) {
    val context = LocalContext.current
    val profile by viewModel.profile.collectAsState()
    val isPaid = profile?.isPaidVersion == true

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(RetroDarkBg)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "GAME SETTINGS & HAPTICS",
                color = RetroGold,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Text(
                text = "Fine-tune vibration feedback, audio synthesis, and visual shaders.",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // Pro Edition / Ad-Free Status
        item {
            RetroCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = if (isPaid) Color(0xFF142426) else Color(0xFF241433),
                borderColor = if (isPaid) Color(0xFF10B981) else RetroRuby
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = RetroGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isPaid) "PRO EDITION: ACTIVE" else "EDITION: FREE TIER",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (isPaid) Color(0xFF34D399) else RetroGold
                            )
                        }
                        RetroBadge(if (isPaid) "AD-FREE" else "ADS ENABLED", backgroundColor = if (isPaid) Color(0xFF059669) else Color(0xFF334155), textColor = Color.White)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isPaid) {
                            "Enjoy 100% ad-free gameplay, +100% permanent gold bonus, cloud backup, exclusive skins, and expanded skill trees."
                        } else {
                            "Free tier features standard campaign and progression. Upgrade to Pro for ad-free experience and expanded content."
                        },
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    if (!isPaid) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RetroButton(
                                text = "UPGRADE TO PRO (₹99)",
                                onClick = onOpenUpgrade,
                                primaryColor = RetroRuby,
                                textColor = Color.White,
                                modifier = Modifier.weight(1f),
                                testTag = "settings_upgrade_pro_btn"
                            )
                            RetroButton(
                                text = "WATCH BONUS AD (+250G)",
                                onClick = {
                                    Toast.makeText(context, "Watched 8-bit retro ad! +250 Gold awarded.", Toast.LENGTH_SHORT).show()
                                },
                                primaryColor = Color(0xFF475569),
                                textColor = Color.White,
                                modifier = Modifier.weight(1f),
                                testTag = "watch_ad_btn"
                            )
                        }
                    }
                }
            }
        }

        // Haptic Feedback & Vibration Engine
        item {
            RetroCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = RetroSurface,
                borderColor = RetroCardBorder
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Vibration,
                                contentDescription = null,
                                tint = RetroGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "TACTILE HAPTIC FEEDBACK",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = RetroGold
                            )
                        }

                        Switch(
                            checked = profile?.hapticsEnabled ?: true,
                            onCheckedChange = { viewModel.toggleHaptics() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = RetroGold,
                                checkedTrackColor = RetroGoldDark
                            ),
                            modifier = Modifier.testTag("toggle_haptics_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Vibration shocks during attacks, critical strikes, spells, and damage taken.",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Intensity: ${profile?.hapticIntensity ?: "HEAVY"}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF38BDF8)
                        )
                        RetroButton(
                            text = "CYCLE INTENSITY",
                            onClick = { viewModel.cycleHapticIntensity() },
                            primaryColor = Color(0xFF334155),
                            textColor = Color.White,
                            testTag = "cycle_haptic_intensity_btn"
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "TEST HAPTIC SENSATIONS:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        RetroButton(
                            text = "HIT",
                            onClick = { viewModel.haptics.trigger(HapticPulseType.HERO_ATTACK) },
                            primaryColor = Color(0xFF1E293B),
                            textColor = RetroGold,
                            modifier = Modifier.weight(1f)
                        )
                        RetroButton(
                            text = "CRIT",
                            onClick = { viewModel.haptics.trigger(HapticPulseType.CRITICAL_HIT) },
                            primaryColor = Color(0xFF1E293B),
                            textColor = RetroRuby,
                            modifier = Modifier.weight(1f)
                        )
                        RetroButton(
                            text = "SPELL",
                            onClick = { viewModel.haptics.trigger(HapticPulseType.SPELL_CAST) },
                            primaryColor = Color(0xFF1E293B),
                            textColor = RetroManaBlue,
                            modifier = Modifier.weight(1f)
                        )
                        RetroButton(
                            text = "FANFARE",
                            onClick = { viewModel.haptics.trigger(HapticPulseType.VICTORY_FANFARE) },
                            primaryColor = Color(0xFF1E293B),
                            textColor = Color(0xFF34D399),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Sound Synthesis Setting
        item {
            RetroCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = RetroSurface,
                borderColor = RetroCardBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Hearing,
                            contentDescription = null,
                            tint = RetroManaBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "8-BIT RETRO CHIPTUNES",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFF1F5F9)
                            )
                            Text(
                                text = "Dynamic audio synthesized in real-time.",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Switch(
                        checked = profile?.soundEnabled ?: true,
                        onCheckedChange = { viewModel.toggleSound() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = RetroManaBlue,
                            checkedTrackColor = Color(0xFF0284C7)
                        ),
                        modifier = Modifier.testTag("toggle_sound_switch")
                    )
                }
            }
        }

        // Visuals: CRT Scanlines
        item {
            RetroCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = RetroSurface,
                borderColor = RetroCardBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = null,
                            tint = Color(0xFFA855F7),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "CRT SCANLINE SHADER",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFF1F5F9)
                            )
                            Text(
                                text = "Authentic 90s retro cathode-ray monitor lines.",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Switch(
                        checked = profile?.crtScanlines ?: true,
                        onCheckedChange = { viewModel.toggleScanlines() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFA855F7),
                            checkedTrackColor = Color(0xFF7E22CE)
                        ),
                        modifier = Modifier.testTag("toggle_crt_switch")
                    )
                }
            }
        }
    }
}
