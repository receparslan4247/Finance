package com.receparslan.finance.ui.screens

import android.icu.text.DecimalFormat
import android.icu.text.DecimalFormatSymbols
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.gson.Gson
import com.receparslan.finance.R
import com.receparslan.finance.model.Cryptocurrency
import com.receparslan.finance.viewmodel.CryptocurrencyViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.util.Locale
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GainerScreen(viewModel: CryptocurrencyViewModel, navController: NavController) {
    val cryptocurrencyGainers = remember { viewModel.cryptocurrencyGainerList }

    val isLoading by remember { viewModel.isLoading }

    // State to manage the refreshing state
    var isRefreshing by rememberSaveable { mutableStateOf(false) }
    val refreshState = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()

    // Header for the screen
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .size(28.dp),
            imageVector = ImageVector.vectorResource(R.drawable.up_icon),
            contentDescription = "Price Change",
            tint = Color.Green
        )

        Text(
            text = "Top Gainers",
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
            textAlign = TextAlign.Center
        )

        Icon(
            modifier = Modifier
                .size(28.dp),
            imageVector = ImageVector.vectorResource(R.drawable.up_icon),
            contentDescription = "Price Change",
            tint = Color.Green
        )
    }

    // Show a loading indicator if the cryptocurrency list is empty and data is being loaded
    if (cryptocurrencyGainers.isEmpty() || isLoading)
        ScreenHolder()
    else {
        // PullToRefreshBox is used to implement the pull-to-refresh functionality
        PullToRefreshBox(
            state = refreshState,
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                coroutineScope.launch {
                    viewModel.refreshGainerLoserScreen()
                    delay(1500)
                    isRefreshing = false
                }
            }
        ) {
            // Grid layout for displaying the top gainers
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                contentPadding = PaddingValues(16.dp),
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(cryptocurrencyGainers) { cryptocurrency ->
                    Item(cryptocurrency, navController) {
                        Icon(
                            modifier = Modifier.size(15.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.up_icon),
                            contentDescription = "Price Change",
                            tint = Color.Green
                        )
                    }

                    if (cryptocurrencyGainers.indexOf(cryptocurrency) >= cryptocurrencyGainers.size - 2)
                        Spacer(Modifier.height(300.dp))
                }
            }
        }
    }
}

@Composable
fun Item(cryptocurrency: Cryptocurrency, navController: NavController, arrow: @Composable () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(elevation = 3.dp, spotColor = Color.White, ambientColor = Color.White, shape = RoundedCornerShape(size = 15.dp))
            .background(color = Color(0xFF211E41), shape = RoundedCornerShape(size = 15.dp))
            .clickable {
                // URLEncoder is used to encode the cryptocurrency object as a JSON string
                val encodedString = URLEncoder.encode(Gson().toJson(cryptocurrency), "utf-8")

                // Navigate to the detail screen with the encoded cryptocurrency object as an argument
                navController.navigate("detail_screen/$encodedString")
            },
        contentAlignment = Alignment.Center
    ) {
        Column {
            // Display the cryptocurrency icon
            Image(
                painter = rememberAsyncImagePainter(cryptocurrency.image),
                contentDescription = cryptocurrency.name,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(75.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally)
            )

            // Display the cryptocurrency name
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = cryptocurrency.name,
                style = TextStyle(
                    color = Color(0xFFA7A7A7),
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.poppins)),
                    fontWeight = FontWeight(500),
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(0f, 1f),
                        blurRadius = 2f
                    )
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Display the cryptocurrency price
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "$" + DecimalFormat("#,###.####", DecimalFormatSymbols(Locale.US)).format(cryptocurrency.currentPrice),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.poppins)),
                    fontWeight = FontWeight(600),
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(0f, 1f),
                        blurRadius = 2f
                    )
                ),
                maxLines = 1,
            )

            // Display the price change percentage
            Row(
                modifier = Modifier
                    .border(0.5.dp, Color(0xFF211E41), RoundedCornerShape(16.dp))
                    .background(Color(0xFF1D1B32), RoundedCornerShape(16.dp))
                    .wrapContentSize()
                    .align(Alignment.CenterHorizontally)
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                arrow() // Display the arrow icon based on the price change percentage

                Text(
                    text = (cryptocurrency.priceChangePercentage24h.absoluteValue.toString() + "%"),
                    style = TextStyle(
                        color = Color(0xFFA7A7A7),
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.poppins)),
                        fontWeight = FontWeight(500),
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(0f, 1f),
                            blurRadius = 2f
                        )
                    )
                )
            }
        }
    }
}