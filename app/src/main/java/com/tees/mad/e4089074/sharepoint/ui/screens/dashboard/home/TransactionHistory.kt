package com.tees.mad.e4089074.sharepoint.ui.screens.dashboard.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.tees.mad.e4089074.sharepoint.ui.components.AuthScreenComponent
import com.tees.mad.e4089074.sharepoint.ui.components.TransactionList
import com.tees.mad.e4089074.sharepoint.viewmodels.TransactionViewModel

@Composable
fun TransactionHistoryScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    transactionViewModel: TransactionViewModel = viewModel()
) {
    val transactions by transactionViewModel.transactions.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        transactionViewModel.fetchTransactions()
        onDispose { }
    }

    AuthScreenComponent(
        onBackArrowClick = { navController.popBackStack() },
        title = "Transaction History",
        subtitle = "View your transaction history"
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        TransactionList(transactions = transactions)
    }

}
