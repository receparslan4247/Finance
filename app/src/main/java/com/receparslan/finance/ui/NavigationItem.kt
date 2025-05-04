package com.receparslan.finance.ui

import com.receparslan.finance.R

sealed class Screen(val rout: String) {
    object Home : Screen("home_screen")
    object Gainer : Screen("gainer_screen")
    object Loser : Screen("loser_screen")
    object Favourites : Screen("favourites_screen")
    object Search : Screen("search_screen")
}

data class NavigationItem(
    val title: String,
    val icon: Int,
    val filledIcon: Int,
    val route: String,
)

val navigationItems = listOf(
    NavigationItem(
        title = "Gainer",
        icon = R.drawable.gainer,
        filledIcon = R.drawable.gainer_filled,
        route = Screen.Gainer.rout
    ),
    NavigationItem(
        title = "Loser",
        icon = R.drawable.loser,
        filledIcon = R.drawable.loser_filled,
        route = Screen.Loser.rout
    ),
    NavigationItem(
        title = "Home",
        icon = R.drawable.home,
        filledIcon = R.drawable.home_filled,
        route = Screen.Home.rout
    ),
    NavigationItem(
        title = "Favourites",
        icon = R.drawable.favourites,
        filledIcon = R.drawable.favourites_filled,
        route = Screen.Favourites.rout
    ),
    NavigationItem(
        title = "Search",
        icon = R.drawable.search,
        filledIcon = R.drawable.search_filled,
        route = Screen.Search.rout
    )
)