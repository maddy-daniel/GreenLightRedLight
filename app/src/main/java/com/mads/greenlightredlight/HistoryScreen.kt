package com.mads.greenlightredlight

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HistoryScreen(navController: NavController, viewModel: BudgetViewModel) {
    val entries  by viewModel.entries.collectAsState()
    var selectedFilter by remember{
        mutableStateOf("All")
    }
    val filters = listOf("All", "Last 4 Weeks","Last 3 Months")
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    val allHistory = viewModel.getWeeklyHistory()

    val filteredHistory = when (selectedFilter) {
        "Last 4 Weeks" -> {
            val cutoff = LocalDate.now().minusWeeks(4).toString()
            allHistory.filter{
                it.weekStart >= cutoff
            }
        }
        "Last 3 Months" -> {
            val cutoff = LocalDate.now().minusMonths(3).toString()
            allHistory.filter{
                it.weekStart>=cutoff
            }
        }
        else-> allHistory
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = NavyBackground
    )
    {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom =32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        )
        {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Column{
                        Text(
                            text = "History",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Past weekly budgets",
                            color = MutedText,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(
                        onClick = {
                            navController.navigate(NavRoutes.HELP)
                        }
                    ) {
                        Text(
                            text = "❓",
                            fontSize = 18.sp
                        )
                    }

                }
            }

            item{
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    filters.forEach{
                        filter->
                        OutlinedButton(
                            onClick = {
                                selectedFilter = filter
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if(selectedFilter==filter) Teal else MutedText
                            ),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                                brush = androidx.compose.ui.graphics.SolidColor(
                                    if(selectedFilter == filter) Teal else Color(0xFF2A2A4A)
                                )
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical= 6.dp),
                            modifier = Modifier.height(36.dp)
                        ){
                            Text(filter, fontSize = 11.sp)
                        }
                    }
                }
            }
            if(filteredHistory.isEmpty()){
                item{
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(12.dp)
                    )
                    {
                        Text(
                            text = "No History yet. Complete a week to see your history here.",
                            color = MutedText,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
            else{
                items(filteredHistory){
                    week->
                    var expanded by remember{
                        mutableStateOf(false)
                    }
                    val isGreen = week.netBalance>= 0
                    val weekStartDate = LocalDate.parse(week.weekStart)
                    val weekEndDate = LocalDate.parse(week.weekEnd)

                    Card(
                        onClick = {
                            expanded = !expanded
                        },
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(12.dp)
                    )
                    {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        )
                        {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            )
                            {
                                Text(
                                    text = "${weekStartDate.format(DateTimeFormatter.ofPattern("MMM d"))} - ${weekEndDate.format(formatter)}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    if(expanded)"▲" else "▼",
                                    color = MutedText,
                                    fontSize = 11.sp
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            )
                            {
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = "Income",
                                            color = MutedText,
                                            fontSize = 10.sp
                                        )
                                        Text(
                                            text = "$${String.format("%.2f", week.totalIncome)}",
                                            color = Teal,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = NavyBackground),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = "Expenses",
                                            color = MutedText,
                                            fontSize = 10.sp
                                        )
                                        Text(
                                            text = "$${String.format("%.2f", week.totalExpenses)}",
                                            color = Red,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = NavyBackground),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = "Net",
                                            color = MutedText,
                                            fontSize = 10.sp
                                        )
                                        Text(
                                            text = "$${String.format("%.2f", week.netBalance)}",
                                            color = if(isGreen) Teal else Red,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if(isGreen) Color(0xFF0D3B2E) else Color(0xFF3B0D1A)
                            ){
                                Text(
                                    if(isGreen) "Green Light" else "Red Light",
                                    color = if(isGreen) Teal else Red,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            AnimatedVisibility(visible = expanded) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)){
                                    HorizontalDivider(color = Color(0xFF2A2A4A))
                                    Text(
                                        text = "Entries",
                                        color = MutedText,
                                        fontSize = 10.sp
                                    )
                                    week.entries.forEach{
                                        entry->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        )
                                        {
                                            Text(
                                                text = "${entry.name} - ${if (entry.isRecurring) "Recurring" else "Incidental"}",
                                                color = Color.White,
                                                fontSize = 11.sp
                                            )
                                            Text(
                                                text = "$${String.format("%.2f", entry.weeklyAmount)}/wk",
                                                color = if(entry.isIncome) Teal else Red,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}
