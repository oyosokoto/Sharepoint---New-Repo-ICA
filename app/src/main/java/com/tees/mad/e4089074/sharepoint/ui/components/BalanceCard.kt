package com.tees.mad.e4089074.sharepoint.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple80
import com.tees.mad.e4089074.sharepoint.util.formatNumber
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
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Purple80
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GBP",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                IconButton(
                    onClick = {
                        isBalanceVisible = !isBalanceVisible
                        // Save the new visibility state to DataStore
                        kotlinx.coroutines.MainScope().launch {
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
                        tint = Color.Black.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Total Transaction Summary",
                fontSize = 12.sp,
                color = Color.Black.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isBalanceVisible) "£${formatNumber(amountSaved)}" else "********",
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Total Amount Spent : " + if (isBalanceVisible) "£${
                    formatNumber(
                        totalAmountSpent
                    )
                }" else "********",
                fontSize = 12.sp,
                color = Color.Black
            )
        }
    }
}

