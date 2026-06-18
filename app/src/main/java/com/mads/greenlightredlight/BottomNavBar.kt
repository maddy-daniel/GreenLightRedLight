package com.mads.greenlightredlight

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onHomeClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onCalendarClick: () -> Unit
) {
    NavigationBar(
        containerColor = NavyBackground,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == NavRoutes.HOME,
            onClick = onHomeClick,
            icon = {
                Text (
                    text = "🏠",
                    fontSize = 20.sp
                )
            },
            label = {
                Text(
                    text = "Home",
                    color = if(currentRoute == NavRoutes.HOME) Teal else MutedText,
                    fontSize = 11.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Teal,
                unselectedIconColor = MutedText,
                selectedTextColor = Teal,
                unselectedTextColor = MutedText,
                indicatorColor = Color(0xFF2A2A4A)
            )
        )
        NavigationBarItem(
            selected = currentRoute == NavRoutes.HISTORY,
            onClick = onHistoryClick,
            icon = {
                Text(
                    text = "📋",
                    fontSize = 20.sp
                )
            },
            label = {
                Text(
                    text = "History",
                    color = if(currentRoute == NavRoutes.HISTORY) Teal else MutedText,
                    fontSize = 11.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Teal,
                unselectedIconColor = MutedText,
                selectedTextColor = Teal,
                unselectedTextColor = MutedText,
                indicatorColor = Color(0xFF2A2A4A)
            )
        )
        NavigationBarItem(
            selected = currentRoute == NavRoutes.CALENDAR,
            onClick = onCalendarClick,
            icon = {
                Text(
                    text = "📅",
                    fontSize = 20.sp
                )
            },
            label = {
                Text(
                    text = "Calendar",
                    color = if (currentRoute == NavRoutes.CALENDAR) Teal else MutedText,
                    fontSize = 11.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Teal,
                unselectedIconColor = MutedText,
                selectedTextColor = Teal,
                unselectedTextColor = MutedText,
                indicatorColor = Color(0xFF2A2A4A)
            )
        )
    }
}