package com.mads.greenlightredlight

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun AllTaxBreakdownScreen(navController: NavController, viewModel:BudgetViewModel) {
    val entries by viewModel.entries.collectAsState()
    val incomeEntries = entries.filter{it.isIncome && it.isHourly}
    val context = LocalContext.current

    val totalNetTakeHome = incomeEntries.sumOf{
        TaxCalculator.calculateNetTakeHome(it.weeklyAmount)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = NavyBackground
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "All Tax Breakdowns",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Federal Deductions for All Income Entries",
                    color = MutedText,
                    fontSize = 12.sp
                )
            }
            if (incomeEntries.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "No income entries yet. Add an income entry to see your tax breakdown.",
                            color = MutedText,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(incomeEntries) { entry ->
                    val gross = entry.weeklyAmount
                    val federalTax = TaxCalculator.calculateFederalTax(gross)
                    val socialSecurity = TaxCalculator.calculateSocialSecurity(gross)
                    val medicare = TaxCalculator.calculateMedicare(gross)
                    val netTakeHome = TaxCalculator.calculateNetTakeHome(gross)

                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(entry.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text(
                                    text = "$${String.format("%.2f", gross)}/wk",
                                    color = Teal,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            HorizontalDivider(color = Color(0xFF2A2A4A))
                            TaxBreakdownRow(
                                label = "Federal Income Tax",
                                amount = federalTax,
                                isDeduction = true
                            )

                            TaxBreakdownRow(
                                label = "Social Security (6.2%)",
                                amount = socialSecurity,
                                isDeduction = true
                            )

                            TaxBreakdownRow(
                                label = "Medicare (1.45%)",
                                amount = medicare,
                                isDeduction = true
                            )

                            HorizontalDivider(color = Color(0xFF2A2A4A))
                            TaxBreakdownRow(
                                label = "Net take-home",
                                amount = netTakeHome,
                                isDeduction = false
                            )
                        }
                    }
                }
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D3E)),
                        shape = RoundedCornerShape(12.dp)
                    )
                    {
                        Column(
                            modifier = Modifier.padding(12.dp)){
                            Text(
                                text = "Total net take-home",
                                color = MutedText,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "$${String.format("%.2f", totalNetTakeHome)}/wk",
                                color = Teal,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                item{
                    Button(
                        onClick = {
                            CsvExporter.exportAllEntries(context, incomeEntries)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(12.dp)
                    )
                    {
                        Text(
                            text = "⬇ Download all as CSV",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                item{
                    OutlinedButton(
                        onClick = {navController.popBackStack()},
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MutedText),
                        shape = RoundedCornerShape(12.dp)
                    ){
                        Text("← Back", modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }
}