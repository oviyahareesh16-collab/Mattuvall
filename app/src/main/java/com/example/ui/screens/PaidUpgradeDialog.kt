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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
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

private const val UPI_ID = "oviyahareesh16-1@okicici"
private const val PRO_PRICE_INR = "99"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaidUpgradeDialog(
    viewModel: GameViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var upiRefInput by remember { mutableStateOf("") }

    fun openUpiPayment() {
        try {
            val uri = Uri.parse("upi://pay?pa=$UPI_ID&pn=Intrast%20Game&am=$PRO_PRICE_INR&cu=INR&tn=Intrast%20Pro%20Edition%20Lifetime%20Unlock")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "No UPI app found. Please copy the UPI ID below.", Toast.LENGTH_LONG).show()
        }
    }

    fun copyUpiId() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("UPI ID", UPI_ID)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "UPI ID copied: $UPI_ID", Toast.LENGTH_SHORT).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(vertical = 24.dp)
            .testTag("paid_upgrade_dialog"),
        content = {
            RetroCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                backgroundColor = RetroDarkBg,
                borderColor = RetroGold
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = "Pro Edition",
                                tint = RetroGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "INTRAST PRO EDITION",
                                color = RetroGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Text(
                        text = "Lifetime Unlock • Credit to $UPI_ID",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Features unlocked list
                    RetroCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = RetroSurface,
                        borderColor = RetroCardBorder
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            FeatureRow(
                                icon = Icons.Default.CloudDone,
                                title = "Cloud Saving & Backup",
                                subtitle = "Sync and restore progress seamlessly between Android & iOS"
                            )
                            FeatureRow(
                                icon = Icons.Default.Palette,
                                title = "Exclusive Character Skins",
                                subtitle = "Unlock Cyber Ronin Alric, Starlight Lyra, Solar Seraph, and more"
                            )
                            FeatureRow(
                                icon = Icons.Default.FlashOn,
                                title = "Expanded Character Skill Trees",
                                subtitle = "Unlock Tier 4 Mastery & Tier 5 Mythic combat abilities for all 6 heroes"
                            )
                            FeatureRow(
                                icon = Icons.Default.CheckCircle,
                                title = "100% Ad-Free Experience",
                                subtitle = "No advertisements + permanent 2x Gold and XP multiplier!"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // UPI Payment Section
                    RetroCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color(0xFF1E1738),
                        borderColor = RetroGoldDark
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "OFFICIAL UPI PAYMENT",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = RetroGold
                                )
                                RetroBadge("₹$PRO_PRICE_INR ONLY", backgroundColor = RetroGold, textColor = RetroDarkBg)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Recipient UPI ID:",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8),
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF0F0A1E))
                                    .border(1.dp, RetroGoldDark, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = UPI_ID,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF38BDF8)
                                )
                                IconButton(
                                    onClick = { copyUpiId() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy UPI ID",
                                        tint = RetroGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            RetroButton(
                                text = "PAY ₹$PRO_PRICE_INR VIA GOOGLE PAY / PHONEPE",
                                onClick = { openUpiPayment() },
                                primaryColor = RetroGold,
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "pay_via_upi_btn"
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Transaction Reference Entry
                            OutlinedTextField(
                                value = upiRefInput,
                                onValueChange = { upiRefInput = it },
                                label = { Text("UPI Ref / UTR No. (Optional)", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = RetroGold,
                                    unfocusedBorderColor = RetroCardBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            RetroButton(
                                text = "VERIFY & ACTIVATE PRO",
                                onClick = { viewModel.activatePaidVersion(upiRefInput.ifBlank { "MANUAL-UPI-$UPI_ID" }) },
                                primaryColor = Color(0xFF10B981),
                                textColor = Color.White,
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "verify_activate_pro_btn"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Instant Test / Developer Unlock
                    RetroButton(
                        text = "TEST / DEMO INSTANT UNLOCK",
                        onClick = { viewModel.activatePaidVersion("DEV-TEST-UNLOCK") },
                        primaryColor = Color(0xFF3B82F6),
                        textColor = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "test_instant_unlock_btn"
                    )
                }
            }
        }
    )
}

@Composable
private fun FeatureRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = RetroGold,
            modifier = Modifier
                .padding(top = 2.dp, end = 10.dp)
                .size(18.dp)
        )
        Column {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFFF1F5F9)
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}
