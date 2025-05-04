package com.receparslan.finance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.receparslan.finance.ui.screens.MainScreen
import com.receparslan.finance.ui.theme.FinanceTheme
import com.receparslan.finance.viewmodel.CryptocurrencyViewModel

@Suppress("unused", "RedundantSuppression")
class MainActivity : ComponentActivity() {
    // ViewModel instance for managing cryptocurrency data
    private val viewModel: CryptocurrencyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinanceTheme {
                MainScreen(viewModel)
            }
        }
    }
}