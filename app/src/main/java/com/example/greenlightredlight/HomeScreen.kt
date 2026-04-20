package com.example.greenlightredlight

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

val NavyBackground = Color(0xFF1A1A2E)
val DarkCard = Color(0xFF16213E)
val Teal = Color(0xFF4ECCA3)
val Red = Color(0xFFE94560)
val MutedText = Color(0xFF8888AA)

@Composable
fun HomeScreen(navController: NavController, viewModel: BudgetViewModel){
    val entries by viewModel.entries.collectAsState()
    val incomeEntries = entries.filter{it.isIncome}
    val expenseEntries = entries.filter{!it.isIncome}
    val net = viewModel.netBalance()
    val totalIncome = viewModel.totalIncome()
    val totalExpense = viewModel.totalExpenses()

    val progress = when{
        totalIncome == 0.0 && totalExpense == 0.0 -> 0.5f
        totalIncome == 0.0 ->0f
        else->(totalIncome/(totalIncome+totalExpense)).toFloat().coerceIn(0f,1f)

    }

    val isGreenLight = net>=0

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = NavyBackground

    ){
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ){
            item {
                Text(
                    text = "Green Light Red Light",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "NJ Tax Calculator * Weekly Budget",
                    color = MutedText,
                    fontSize = 11.sp
                )
            }
            item{
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(12.dp)
                ){
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Weekly net", color = MutedText, fontSize = 11.sp)
                        Text(
                            text = "$%.2f".format(net),
                            color = if(isGreenLight)Teal else Red,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            item{
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(8.dp)
                    ){
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Total income", color= MutedText, fontSize = 12.sp)
                            Text("$%.2f".format(totalIncome), color = Teal, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Total expenses", color = MutedText, fontSize = 10.sp)
                            Text(
                                "$%.2f".format(totalExpense),
                                color = Red,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("◀ Red Light", color = Red, fontSize = 10.sp)
                        Text("Green Light ▶", color = Teal, fontSize = 10.sp)
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth().height(10.dp)
                    ){
                        Row(modifier=Modifier.fillMaxSize()){
                            Box(
                                modifier = Modifier.weight(1f).fillMaxHeight().background(
                                        brush = Brush.horizontalGradient(colors = listOf(Red, DarkCard))
                                )
                            )
                            Box(
                                modifier = Modifier.weight(1f).fillMaxHeight().background(
                                    brush = Brush.horizontalGradient(colors = listOf(DarkCard, Teal))
                                )
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(4.dp)
                                .align(Alignment.CenterStart)
                                .offset(x = (progress*1f*360).dp - 2.dp)
                                .background(Color.White, shape= RoundedCornerShape(2.dp))
                        )
                    }
                }
            }
            item {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isGreenLight) Color(0xFF0D3B2E) else Color(0xFF3B0D1A)
                ) {
                    Text(
                        text = if (isGreenLight) "🟢 Green Light" else "🔴 Red Light",
                        color = if (isGreenLight) Teal else Red,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            item {
                Text("INCOME ENTRIES", color = MutedText, fontSize = 9.sp)
            }

            if (incomeEntries.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            "No entries yet",
                            color = MutedText,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            } else {
                items(incomeEntries) { entry ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "${entry.name} — ${if (entry.isRecurring) "Recurring" else "Incidental"} — ${entry.frequency}",
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                                Text("$%.2f/wk".format(entry.weeklyAmount),
                                    color = Teal,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedButton(
                                onClick = {
                                    navController.navigate("tax_breakdown/${entry.id}")
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Teal),
                                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(width = 1.dp)
                            ) {
                                Text("📄 View tax breakdown", fontSize = 9.sp)
                            }
                        }
                    }
                }
            }

            item {
                Text("EXPENSE ENTRIES", color = MutedText, fontSize = 9.sp)
            }

            if (expenseEntries.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            "No entries yet",
                            color = MutedText,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            } else {
                items(expenseEntries) { entry ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "${entry.name} — ${if (entry.isRecurring) "Recurring" else "Incidental"} — ${entry.frequency}",
                                color = Color.White,
                                fontSize = 11.sp
                            )
                            Text("$%.2f/wk".format(entry.weeklyAmount),
                                color = Red,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = { navController.navigate(NavRoutes.ADD_ENTRY) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Teal),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("+ Add entry", color = NavyBackground, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = { navController.navigate(NavRoutes.DELETE_ENTRY) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Red),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("- Delete entry", color = Color.White, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedButton(
                    onClick = { navController.navigate(NavRoutes.ALL_TAX_BREAKDOWN) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Teal),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("📊 View all tax breakdowns", fontSize = 13.sp)
                }
            }
        }
    }
}