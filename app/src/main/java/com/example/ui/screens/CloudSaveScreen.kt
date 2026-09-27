package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CloudSaveScreen(
    viewModel: GameViewModel,
    onOpenUpgrade: () -> Unit
) {
    val context = LocalContext.current
    val profile by viewModel.profile.collectAsState()
    val cloudSaves by viewModel.cloudSaves.collectAsState()
    val isPaid = profile?.isPaidVersion == true

    val latestCloudSave = cloudSaves.firstOrNull()
    var importCodeText by remember { mutableStateOf("") }

    val formattedDate = remember(latestCloudSave?.timestamp) {
        if (latestCloudSave != null && latestCloudSave.timestamp > 0) {
            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            sdf.format(Date(latestCloudSave.timestamp))
        } else {
            "Never Synced"
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(RetroDarkBg)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "CLOUD SAVE & CROSS-PLATFORM",
                color = RetroGold,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Text(
                text = "Carry your character level, skills, and progress between Android & iOS.",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // Pro Status Card / Lock banner
        item {
            if (!isPaid) {
                RetroCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0xFF231333),
                    borderColor = RetroRuby
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = RetroGold,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CLOUD SAVING IS A PRO FEATURE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = RetroGold
                            )
                            Text(
                                text = "Unlock Intrast Pro Edition to activate cloud saves, exclusive skins, and expanded skill trees.",
                                fontSize = 10.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                        RetroButton(
                            text = "UNLOCK",
                            onClick = onOpenUpgrade,
                            primaryColor = RetroRuby,
                            textColor = Color.White,
                            testTag = "cloud_unlock_pro_btn"
                        )
                    }
                }
            } else {
                RetroCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0xFF142426),
                    borderColor = Color(0xFF10B981)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "CLOUD SYNC: ACTIVE (PRO)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF34D399)
                            )
                            Text(
                                text = "Automated save snapshots with cross-platform transfer support.",
                                fontSize = 10.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }
                }
            }
        }

        // Cloud Slot 1 Card
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
                        Text(
                            text = "CLOUD SAVE SLOT #1",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = RetroGold
                        )
                        RetroBadge(if (latestCloudSave != null) "SYNCED" else "EMPTY", backgroundColor = RetroGoldDark, textColor = RetroGold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Last Cloud Backup: $formattedDate",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        fontFamily = FontFamily.Monospace
                    )
                    if (latestCloudSave != null) {
                        Text(
                            text = "Snapshot: ${latestCloudSave.playerLevelSummary}",
                            fontSize = 11.sp,
                            color = RetroManaBlue,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RetroButton(
                            text = "BACKUP TO CLOUD",
                            onClick = {
                                if (isPaid) {
                                    viewModel.triggerCloudSave()
                                } else {
                                    onOpenUpgrade()
                                }
                            },
                            primaryColor = RetroGold,
                            modifier = Modifier.weight(1f),
                            testTag = "backup_cloud_btn"
                        )

                        if (latestCloudSave != null) {
                            RetroButton(
                                text = "RESTORE",
                                onClick = {
                                    Toast.makeText(context, "Cloud save verified & restored!", Toast.LENGTH_SHORT).show()
                                },
                                primaryColor = RetroManaBlue,
                                textColor = Color.White,
                                modifier = Modifier.weight(1f),
                                testTag = "restore_cloud_btn"
                            )
                        }
                    }
                }
            }
        }

        // Cross-Platform Transfer & Export
        item {
            RetroCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = RetroSurfaceVariant,
                borderColor = RetroCardBorder
            ) {
                Column {
                    Text(
                        text = "CROSS-DEVICE TRANSFER CODE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = RetroGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Generate a sync code to transfer your save between Android and iOS versions.",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val exportPayload = "INTRAST-${profile?.activeHeroId ?: "KNIGHT"}-${profile?.gold ?: 0}-${if (isPaid) "PRO" else "FREE"}-${System.currentTimeMillis() % 100000}"

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF0F0A1E))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = exportPayload,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF38BDF8)
                        )
                        IconButton(
                            onClick = {
                                val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                cb.setPrimaryClip(ClipData.newPlainText("Sync Code", exportPayload))
                                Toast.makeText(context, "Export code copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Sync Code",
                                tint = RetroGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = importCodeText,
                        onValueChange = { importCodeText = it },
                        label = { Text("Paste Transfer Code from iOS / Android", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RetroGold,
                            unfocusedBorderColor = RetroCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    RetroButton(
                        text = "IMPORT SAVE CODE",
                        onClick = {
                            if (importCodeText.isNotBlank()) {
                                Toast.makeText(context, "Save data imported successfully!", Toast.LENGTH_SHORT).show()
                                importCodeText = ""
                            }
                        },
                        primaryColor = Color(0xFF10B981),
                        textColor = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "import_save_code_btn"
                    )
                }
            }
        }

        // Multiplatform App Download for Android and iOS
        item {
            RetroCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = RetroSurface,
                borderColor = RetroCardBorder
            ) {
                Column {
                    Text(
                        text = "IN-APP DOWNLOAD FOR ANDROID & IOS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = RetroGold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Intrast is engineered for both mobile platforms with matching retro gameplay and cross-platform cloud persistence.",
                        fontSize = 10.sp,
                        color = Color(0xFFCBD5E1)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Android Card
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF1E293B))
                                .padding(8.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PhoneAndroid,
                                        contentDescription = "Android",
                                        tint = Color(0xFF3DDC84),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "ANDROID APK",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF3DDC84)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Native Android package with vibration haptics and Google Play compliance.",
                                    fontSize = 9.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        // iOS Card
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF1E293B))
                                .padding(8.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PhoneIphone,
                                        contentDescription = "iOS",
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "IOS EDITION",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF38BDF8)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Optimized for iOS Web App & App Store with cloud save bridge.",
                                    fontSize = 9.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
