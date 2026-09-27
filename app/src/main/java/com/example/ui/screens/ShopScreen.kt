package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ItemTemplates
import com.example.ui.components.RetroBadge
import com.example.ui.components.RetroButton
import com.example.ui.components.RetroCard
import com.example.ui.theme.RetroCardBorder
import com.example.ui.theme.RetroDarkBg
import com.example.ui.theme.RetroGold
import com.example.ui.theme.RetroManaBlue
import com.example.ui.theme.RetroSurface
import com.example.ui.viewmodel.GameViewModel

@Composable
fun ShopScreen(viewModel: GameViewModel) {
    val profile by viewModel.profile.collectAsState()
    val inventoryEntities by viewModel.inventory.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(RetroDarkBg)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DUNGEON ALCHEMIST & SHOP",
                        color = RetroGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Prepare consumables and tactical bombs for dangerous bosses.",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                RetroBadge("🪙 ${profile?.gold ?: 0} GOLD", backgroundColor = RetroGold, textColor = RetroDarkBg)
            }
        }

        items(ItemTemplates.allShopItems) { item ->
            val ownedCount = inventoryEntities.find { it.itemId == item.id }?.quantity ?: 0
            val canAfford = (profile?.gold ?: 0) >= item.goldPrice

            RetroCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("shop_item_${item.id}"),
                backgroundColor = RetroSurface,
                borderColor = RetroCardBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                color = RetroGold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            RetroBadge("Owned: $ownedCount", backgroundColor = Color(0xFF1E293B), textColor = RetroManaBlue)
                        }
                        Text(
                            text = item.description,
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Cost: ${item.goldPrice} Gold",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (canAfford) Color(0xFFFBBF24) else Color(0xFFEF4444),
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    RetroButton(
                        text = "BUY",
                        onClick = { viewModel.buyShopItem(item, 1) },
                        enabled = canAfford,
                        primaryColor = RetroGold,
                        testTag = "buy_${item.id}_btn"
                    )
                }
            }
        }
    }
}
