package com.receparslan.finance.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.receparslan.finance.R
import com.receparslan.finance.viewmodel.CryptocurrencyViewModel

@Composable
fun SearchScreen(viewModel: CryptocurrencyViewModel, navController: NavController) {
    // This is the search query entered by the user
    var searchQuery by rememberSaveable { mutableStateOf("") }

    // This is the list of cryptocurrencies that match the search query
    val cryptocurrencySearchList = remember { viewModel.cryptocurrencySearchList }

    // This is the focus manager used to clear the focus from the search field
    val focusManager = LocalFocusManager.current

    // This is the state that indicates whether the app is currently loading data
    val isLoading by remember { viewModel.isLoading }

    // Search field for entering the cryptocurrency name
    TextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            focusManager.clearFocus()
            viewModel.searchCryptocurrencies(searchQuery)
        }),
        placeholder = {
            Text(
                text = "Search Cryptocurrency",
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.poppins)),
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                )
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Outlined.Clear,
                contentDescription = "Clear",
                modifier = Modifier.clickable {
                    searchQuery = ""
                }
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search",
                modifier = Modifier.padding(start = 10.dp)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp, 9.dp, 20.dp, 0.dp)
            .border(BorderStroke(2.dp, Color(0xFF211E41)), CircleShape),
        textStyle = TextStyle(
            fontFamily = FontFamily(Font(R.font.poppins)),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        ),
        singleLine = true,
        shape = CircleShape,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = Color(0xFF211E41),
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            cursorColor = Color(0xFF000000),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color(0xFFA7A7A7),
        )
    )

    // Show a placeholder while loading data
    if (isLoading)
        CryptocurrencyPlaceholder()
    else
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp, 70.dp, 20.dp, 16.dp),
            contentPadding = PaddingValues(top = 7.dp),
        ) {
            items(cryptocurrencySearchList) {
                CryptocurrencyRow(it, navController)

                // Show a spacer at the end of the list to show cryptocurrency on the bottom bar
                if (cryptocurrencySearchList.lastOrNull() == it)
                    Spacer(Modifier.height(100.dp))
            }
        }
}

@Composable
private fun CryptocurrencyPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp, 70.dp, 20.dp, 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Loading...",
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