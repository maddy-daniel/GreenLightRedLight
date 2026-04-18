package com.example.greenlightredlight

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable

fun AddEntryScreen(navController: NavController, viewModel: BudgetViewModel) {
    var isIncome by remember  {mutableStateOf(true)}
    var isRecurring by remember {mutableStateOf(true)}
    var isHourly by remember { mutableStateOf(false) }
    var name by remember {mutableStateOf("")}
    var amount by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color= NavyBackground
    ){
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ){
            Text("Add Entry", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)

            Text("Type", color = MutedText, fontSize= 11.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {isIncome = true},
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if(isIncome) Teal else DarkCard
                    ),
                    shape = RoundedCornerShape(8.dp)
                ){
                    Text("Income", color = if(isIncome) NavyBackground else MutedText)
                }
                Button(
                    onClick = {isIncome = false},
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if(!isIncome) Red else DarkCard
                    ),
                    shape = RoundedCornerShape(8.dp)
                ){
                    Text("Expense", color = if(!isIncome) Color.White else MutedText)
                }
            }
            Text("Category", color=MutedText, fontSize = 11.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {isRecurring = true},
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if(isRecurring) DarkCard else DarkCard,
                    ),
                    border = if(isRecurring) ButtonDefaults.outlinedButtonBorder(enabled = true) else null,
                    shape = RoundedCornerShape(8.dp)
                ){
                    Text("Recurring", color = if(isRecurring) Teal else MutedText)
                }
                Button(
                    onClick = {isRecurring = false},
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkCard
                    ),
                    border = if(!isRecurring) ButtonDefaults.outlinedButtonBorder(enabled = true) else null,
                    shape = RoundedCornerShape(8.dp)
                ){
                    Text("Incidental", color = if(!isRecurring) Teal else MutedText)
                }
            }
            if(isIncome){
                Text("Income type", color = MutedText, fontSize=11.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {isHourly = false},
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkCard),
                        border = if(!isHourly) ButtonDefaults.outlinedButtonBorder(enabled = true) else null,
                        shape= RoundedCornerShape(8.dp)
                    ){
                        Text("Flat", color = if (!isHourly) Teal else MutedText)
                    }
                    Button(
                        onClick = {isHourly = true},
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkCard),
                        border = if (isHourly) ButtonDefaults.outlinedButtonBorder(enabled = true) else null,
                        shape= RoundedCornerShape(8.dp)
                    ){
                        Text("Hourly", color = if(isHourly) Teal else MutedText)
                    }
                }
            }
            Text("Name", color = MutedText, fontSize = 11.sp)
            OutlinedTextField(
                value = name,
                onValueChange = {name = it},
                placeholder = {Text("e.g. Paycheck", color = MutedText)},
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Teal,
                    unfocusedBorderColor = MutedText,
                    focusedTextColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Teal
                ),
                shape = RoundedCornerShape(8.dp)
            )
            Text("Amount ($)", color = MutedText, fontSize = 11.sp)
            OutlinedTextField(
                value = amount,
                onValueChange = {amount = it},
                placeholder = {Text("0.00", color = MutedText)},
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Teal,
                    unfocusedBorderColor = MutedText,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Teal
                ),
                shape = RoundedCornerShape(8.dp)
            )
            Button(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull() ?: 0.0
                    if(name.isNotBlank() && parsedAmount>0){
                        viewModel.addEntry(
                            Entry(
                                id = 0,
                                name = name,
                                amount = parsedAmount,
                                isIncome = isIncome,
                                isRecurring = isRecurring,
                                isHourly = isHourly,
                            )
                        )
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Teal),
                shape = RoundedCornerShape(8.dp)
            ){
                Text("Save Entry", color = NavyBackground, fontWeight = FontWeight.Medium)
            }
            OutlinedButton(
                onClick = {navController.popBackStack() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(contentColor = MutedText),
                shape = RoundedCornerShape(8.dp)
            ){
                Text("Cancel")
            }
        }
    }
}