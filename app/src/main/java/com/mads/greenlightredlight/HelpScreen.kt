package com.mads.greenlightredlight

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HelpScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = NavyBackground
    ){
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ){
            Text(
                text = "How to use",
                color= Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Green Light Red Light",
                color = MutedText,
                fontSize=12.sp
            )

            HorizontalDivider(color = Color(0xFF2A2A4A))

            HelpSection(title = "What is this app?"){
                Text(
                    text = "Green Light Red Light helps you instantly know if you have enough money to cover your expenses this week. Green Light means that you're covered. Red Light means you're short. ",
                    color = Color(0xFFCCCCDD),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }

            HorizontalDivider(color = Color(0xFF2A2A4A))

            HelpSection(title = "Category"){
                HelpStep(label = "Recurring", description = "An entry that happens every week like a paycheck or rent. Kept after New Week Rollover.")
                HelpStep(label = "Incidental", description = "A one-time or irregular entry like a bonus or unexpected expense. Cleared after New Week Rollover.")
            }

            HorizontalDivider(color = Color(0xFF2A2a4a))

            HelpSection(title = "Income Type"){
                HelpStep(label = "Flat", description = "A fixed salary or set amount per  (e.g. \$500 every week)." )
                HelpStep(label = "Hourly", description = "Enter your pay rate and hours worked - the app calculates your total automatically.")
            }
            HorizontalDivider(color = Color(0xFF2A2A4A))

            HelpSection(title = "Pay frequencies"){
                Text(
                    text = "All pay frequencies are automatically converted to a weekly amount so your balance is always calculated on a weekly basis.",
                    color = Color(0xFFCCCCDD),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                HelpStep(label = "Weekly", description = "52 paychecks/year - no conversion needed.")
                HelpStep(label = "Bi-weekly", description = "26 paychecks/year - amount divided by 2.")
                HelpStep(label = "Semi-monthly", description = "24 paychecks/year - amount multiplied by 24 then divided by 52.")
                HelpStep(label = "Monthly", description = "12 paychecks/year - amount multiplied by 12 then divided by 52.")
                HelpStep(label = "Yearly expense", description = "Annual expense divided by 52 to get weekly amount.")
            }
            HorizontalDivider(color = Color(0xFF2A2A4A))

            HelpSection(title = "Federal Tax breakdown"){
                Text(
                    text = "The app calculates three basic federal deductions on your income entries: ",
                    color = Color(0xFFCCCCDD),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                HelpStep(label = "Federal Income Tax:", description = "Based on your 2026 federal income tax bracket.")
                HelpStep(label = "Social Security:", description = "6.2% of your gross income.")
                HelpStep(label = "Medicare:", description = "1.45% of your gross income.")
            }

            HorizontalDivider(color = Color(0xFF2A2A4A))

            HelpSection(title = "How to get started"){
                HelpStep(label = "1.", description = "Tap Start on the Welcome screen to open the app.")
                HelpStep(label = "2.", description = "Tap + Add Entry to add your income or expenses.")
                HelpStep(label = "3.", description = "Select Income or Expense and fill in the details.")
                HelpStep(label = "4.", description = "View your weekly balance on the home screen.")
                HelpStep(label = "5.", description = "Tap an income entry to view its federal tax breakdown.")
                HelpStep(label = "6.", description = "Tap New Week Rollover at the start of each week to reset.")
            }

            OutlinedButton(
                onClick = {navController.popBackStack()},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MutedText),
                shape = RoundedCornerShape(12.dp)
            ){
                Text(
                    text = "<- Back",
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun HelpSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column (verticalArrangement = Arrangement.spacedBy(8.dp)){
        Text(title, color = Teal, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        content()
    }
}

@Composable
fun HelpStep(label: String, description: String){
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(6.dp)
    ){
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            Text(label, color = Teal, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Text(description, color = Color(0xFFCCCCDD), fontSize = 11.sp, lineHeight = 16.sp)
        }
    }
}