package com.receparslan.finance.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.receparslan.finance.R
import com.receparslan.finance.viewmodel.CryptocurrencyViewModel

@Composable
fun FavouritesScreen(viewModel: CryptocurrencyViewModel, navController: NavController) {
    // This is the list of saved cryptocurrencies
    val savedCryptocurrencies by remember { viewModel.getCryptocurrenciesByIds(viewModel.savedCryptocurrencyList.map { it.id }) }

    // This is the state that indicates whether the app is currently loading data
    val isLoading by remember { viewModel.isLoading }

    // This is the title of the screen
    Text(
        text = "Favourites",
        style = TextStyle(
            shadow = Shadow(
                color = Color.White,
                offset = Offset(0f, 2f),
                blurRadius = 3f
            ),
            fontFamily = FontFamily(Font(R.font.poppins)),
            fontSize = 20.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )

    // This is the loading indicator that is displayed when the app is loading data
    if (viewModel.savedCryptocurrencyList.isEmpty())
        EmptyScreenHolder()
    else if (isLoading)
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(50.dp)
            )
        }

    // This is the grid that displays the saved cryptocurrencies
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        columns = GridCells.Fixed(2),
    ) {
        items(savedCryptocurrencies) { cryptocurrency ->
            Item(cryptocurrency, navController) {
                if (cryptocurrency.priceChangePercentage24h < 0)
                    Icon(
                        modifier = Modifier
                            .size(28.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.down_icon),
                        contentDescription = "Price Change",
                        tint = Color.Red
                    )
                else
                    Icon(
                        modifier = Modifier
                            .size(28.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.up_icon),
                        contentDescription = "Price Change",
                        tint = Color.Green
                    )
            }

            if (savedCryptocurrencies.indexOf(cryptocurrency) >= savedCryptocurrencies.size - 1)
                Spacer(Modifier.height(300.dp))
        }
    }
}

// This function is used to display a loading indicator while the cryptocurrency data is being loaded.
@Composable
fun EmptyScreenHolder() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Favourites List is Empty",
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.poppins)),
                fontSize = 20.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
        )
    }
}