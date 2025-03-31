package com.tees.mad.e4089074.sharepoint.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tees.mad.e4089074.sharepoint.ui.components.dashboard.TransactionListItem
import com.tees.mad.e4089074.sharepoint.util.TransactionData

@Composable
fun TransactionList(
    transactions: List<TransactionData>
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        if (transactions.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillParentMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No transactions yet",
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(transactions) { transaction ->
                TransactionListItem(transaction)
            }
        }
    }
}
