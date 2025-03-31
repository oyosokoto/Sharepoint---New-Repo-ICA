package com.tees.mad.e4089074.sharepoint.ui.components.dashboard

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleSoft
import com.tees.mad.e4089074.sharepoint.ui.theme.White
import com.tees.mad.e4089074.sharepoint.util.formatNumber
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val BALANCE_VISIBILITY_KEY = booleanPreferencesKey("balance_visibility")

@Composable
fun BalanceCard(
    modifier: Modifier = Modifier,
    amountSaved: Float = 0f,
    totalAmountSpent: Float = 0f
) {
    val context = LocalContext.current

    var isBalanceVisible by remember { mutableStateOf(true) }

    // Load saved preference when the composable is first composed
    LaunchedEffect(Unit) {
        val balanceVisibilityFlow: Flow<Boolean> = context.dataStore.data
            .map { preferences ->
                preferences[BALANCE_VISIBILITY_KEY] != false
            }
        isBalanceVisible = balanceVisibilityFlow.first()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp),
        colors = CardDefaults.cardColors(
            containerColor = PurpleSoft
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top row with currency and visibility toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GBP",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = White.copy(alpha = 0.9f)
                    )
                )

                IconButton(
                    onClick = {
                        isBalanceVisible = !isBalanceVisible
                        // Save the new visibility state to DataStore
                        MainScope().launch {
                            context.dataStore.edit { preferences ->
                                preferences[BALANCE_VISIBILITY_KEY] = isBalanceVisible
                            }
                        }
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isBalanceVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (isBalanceVisible) "Hide Balance" else "Show Balance",
                        tint = Color.White
                    )
                }
            }

            // Balance info
            Text(
                text = "Total Transaction Summary",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = White.copy(alpha = 0.8f)
                )
            )

            Text(
                text = if (isBalanceVisible) "£${formatNumber(amountSaved)}" else "********",
                style = TextStyle(
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            )

            Text(
                text = "Total Amount Spent : " + if (isBalanceVisible) "£${
                    formatNumber(
                        totalAmountSpent
                    )
                }" else "********",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = White.copy(alpha = 0.8f)
                )
            )
        }
    }
}

