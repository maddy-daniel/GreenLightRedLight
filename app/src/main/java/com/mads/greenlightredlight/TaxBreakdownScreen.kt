package com.mads.greenlightredlight

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun TaxBreakdownScreen(navController: NavController, viewModel: BudgetViewModel, entryId: Int) {
    val entry = viewModel.getEntryById(entryId)
    val context= LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = NavyBackground
    ){
        Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
            .padding(start=16.dp, end=16.dp, top= 16.dp, bottom=32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Tax Breakdown",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            if(entry == null){
                Text("Entry not found", color = MutedText, fontSize = 13.sp)
            }
            else{
                val gross = entry.weeklyAmount
                val federalTax = TaxCalculator.calculateFederalTax(gross)
                val socialSecurity = TaxCalculator.calculateSocialSecurity(gross)
                val medicare = TaxCalculator.calculateMedicare(gross)
                val netTakeHome = TaxCalculator.calculateNetTakeHome(gross)

                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(12.dp),
                )
                {
                    Column(modifier = Modifier.padding(12.dp)){
                        Text("Entry", color = MutedText, fontSize = 11.sp)
                        Text(
                            entry.name,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Gross weekly amount", color = MutedText, fontSize = 11.sp)
                        Text(
                            text = "$${String.format("%.2f",gross)}/wk",
                            color = Teal,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                HorizontalDivider(color = Color(0xFF2A2A4A))
                Text("Federal Deductions", color = MutedText, fontSize = 11.sp)

                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(12.dp),
                )
                {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)){
                        TaxBreakdownRow(
                            label = "Federal Income Tax",
                            amount = federalTax,
                            isDeduction = true
                        )
                        HorizontalDivider(color = Color(0xFF2A2A4A))
                        TaxBreakdownRow(
                            label = "Social Security (6.2%)",
                            amount = socialSecurity,
                            isDeduction = true
                        )
                        HorizontalDivider(color = Color(0xFF2A2A4A))
                        TaxBreakdownRow(
                            label = "Medicare (1.45%)",
                            amount = medicare,
                            isDeduction = true
                        )
                    }
                }
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D3B2E)),
                    shape = RoundedCornerShape(12.dp),
                ){
                    Column(modifier = Modifier.padding(12.dp)){
                        Text("Net take-home", color = MutedText, fontSize = 11.sp)
                        Text(
                            text = "$${String.format("%.2f",netTakeHome)}/wk",
                            color = Teal,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Button(
                    onClick = {
                        CsvExporter.exportSingleEntry(context, entry)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(12.dp)
                ){
                    Text("⬇ Download as CSV",
                        color=Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            OutlinedButton(
                onClick = {navController.popBackStack() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MutedText),
                shape = RoundedCornerShape(12.dp)
            ){
                Text("← Back", modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

@Composable
fun TaxBreakdownRow(label: String, amount: Double, isDeduction: Boolean){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(label, color = MutedText, fontSize = 12.sp)
        Text(
            text = "${if(isDeduction)"-" else ""} $${String.format("%.2f", amount)}",
            color = if(isDeduction) Red else Teal,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}