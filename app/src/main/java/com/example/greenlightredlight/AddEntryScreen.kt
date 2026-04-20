package com.example.greenlightredlight

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background

@Composable
fun AddEntryScreen(navController: NavController, viewModel: BudgetViewModel) {
    var isIncome by remember  {mutableStateOf(true)}
    var isRecurring by remember {mutableStateOf(true)}
    var isHourly by remember { mutableStateOf(false) }
    var name by remember {mutableStateOf("")}
    var amount by remember { mutableStateOf("") }
    var frequency by remember{mutableStateOf("Weekly")}
    var isMonthlyExpense by remember {mutableStateOf(false)}
    var hourlyRate by remember { mutableStateOf("") }
    var hoursWorked by remember { mutableStateOf("") }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color= NavyBackground
    ){
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ){
            Text("Add Entry", color = if (isIncome) Teal else Red, fontSize = 18.sp, fontWeight = FontWeight.Medium)

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
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCard),
                    border = if (isRecurring) ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        brush = androidx.compose.ui.graphics.SolidColor(if (isIncome) Teal else Red)
                    ) else null,
                    shape = RoundedCornerShape(8.dp)
                ){
                    Text("Recurring", color = if(isRecurring)(if(isIncome)Teal else Red) else MutedText)
                }
                Button(
                    onClick = {isRecurring = false},
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCard),
                    border = if (!isRecurring) ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        brush = androidx.compose.ui.graphics.SolidColor(if (isIncome) Teal else Red)
                    ) else null,
                    shape = RoundedCornerShape(8.dp)
                ){
                    Text("Incidental", color = if(!isRecurring) (if(isIncome)Teal else Red) else MutedText)
                }
            }
            if(isIncome){
                Text("Pay Frequency", color = MutedText, fontSize = 11.sp)
                var frequencyExpanded by remember {mutableStateOf(false)}
                val frequencies = listOf("Weekly", "Bi-weekly", "Semi-monthly","Monthly")

                Box(modifier = Modifier.fillMaxWidth()){
                    OutlinedTextField(
                        value = frequency,
                        onValueChange={},
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = {frequencyExpanded = !frequencyExpanded}){
                                Text("▼", color = MutedText, fontSize = 12.sp)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Teal,
                            unfocusedBorderColor = MutedText,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = DarkCard,
                            unfocusedContainerColor = DarkCard,
                            cursorColor = Teal
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    DropdownMenu(
                        expanded = frequencyExpanded,
                        onDismissRequest = {frequencyExpanded = false},
                        modifier = Modifier.fillMaxWidth().background(DarkCard)
                    ) {
                        frequencies.forEach{freq ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        freq,
                                        color = if(frequency == freq) Teal else Color.White,
                                        fontSize = 13.sp
                                    )
                                },
                                onClick = {
                                    frequency = freq
                                    frequencyExpanded = false
                                }
                            )
                        }
                    }
                }
                Text("Income type", color = MutedText, fontSize=11.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {isHourly = false},
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkCard),
                        border = if(!isHourly) ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                            brush = androidx.compose.ui.graphics.SolidColor(Teal)
                        ) else null,
                        shape= RoundedCornerShape(8.dp)
                    ){
                        Text("Flat", color = if (!isHourly) Teal else MutedText)
                    }
                    Button(
                        onClick = {isHourly = true},
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkCard),
                        border = if (isHourly) ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                            brush = androidx.compose.ui.graphics.SolidColor(Teal)
                        ) else null,
                        shape= RoundedCornerShape(8.dp)
                    ){
                        Text("Hourly", color = if(isHourly) Teal else MutedText)
                    }
                }
            }
            if(!isIncome){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text("Monthly Expense?", color = MutedText, fontSize = 11.sp)
                    Switch(
                        checked = isMonthlyExpense,
                        onCheckedChange = {isMonthlyExpense = it},
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Red,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = DarkCard
                        )
                    )
                }
            }
            Text("Name", color = MutedText, fontSize = 11.sp)
            OutlinedTextField(
                value = name,
                onValueChange = {name = it},
                placeholder = {Text("e.g. Paycheck", color = MutedText)},
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isIncome) Teal else Red,
                    unfocusedBorderColor = MutedText,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = DarkCard,
                    unfocusedContainerColor = DarkCard,
                    cursorColor = if (isIncome) Teal else Red
                ),
                shape = RoundedCornerShape(8.dp)
            )
            if(!isHourly || !isIncome){
                Text("Amount ($)", color = MutedText, fontSize = 11.sp)
                OutlinedTextField(
                    value = amount,
                    onValueChange = {amount = it},
                    placeholder = {Text("0.00", color = MutedText)},
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isIncome) Teal else Red,
                        unfocusedBorderColor = MutedText,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = DarkCard,
                        unfocusedContainerColor = DarkCard,
                        cursorColor = if (isIncome) Teal else Red
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
            else{
                Text("Pay rate ($/hr)", color = MutedText, fontSize = 11.sp)
                OutlinedTextField(
                    value = hourlyRate,
                    onValueChange = {
                        hourlyRate = it
                        val rate = it.toDoubleOrNull() ?: 0.0
                        val hours = hoursWorked.toDoubleOrNull() ?: 0.0
                        amount = (rate * hours).toString()
                    },
                    placeholder = {Text("0.00", color = MutedText)},
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Teal,
                        unfocusedBorderColor = MutedText,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = DarkCard,
                        unfocusedContainerColor = DarkCard,
                        cursorColor = Teal
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                Text("Hours worked", color = MutedText, fontSize = 11.sp)
                OutlinedTextField(
                    value = hoursWorked,
                    onValueChange = {
                        hoursWorked = it
                        val rate = hourlyRate.toDoubleOrNull() ?: 0.0
                        val hours = it.toDoubleOrNull() ?: 0.0
                        amount = (rate * hours).toString()
                    },
                    placeholder = {Text("0.00", color = MutedText)},
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Teal,
                        unfocusedBorderColor = MutedText,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = DarkCard,
                        unfocusedContainerColor = DarkCard,
                        cursorColor = Teal
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                if (amount.toDoubleOrNull() != null && amount.toDouble() > 0.0) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D3B2E)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Calculated Amount",
                                color = MutedText,
                                fontSize = 11.sp,
                            )
                            Text("${"$"}${"%.2f".format(amount.toDouble())}", color = Teal, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
            Button(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && parsedAmount > 0) {
                        val weeklyAmount = when {
                            !isIncome && isMonthlyExpense -> parsedAmount / 4.2
                            isIncome -> when (frequency) {
                                "Weekly" -> parsedAmount
                                "Bi-weekly" -> parsedAmount / 2.0
                                "Semi-monthly" -> parsedAmount * 24 / 52.0
                                "Monthly" -> parsedAmount * 12 / 52.0
                                else -> parsedAmount
                            }
                            else -> parsedAmount
                        }
                        viewModel.addEntry(
                            Entry(
                                id = 0,
                                name = name,
                                amount = parsedAmount,
                                weeklyAmount = weeklyAmount,
                                isIncome = isIncome,
                                isRecurring = isRecurring,
                                isHourly = isHourly,
                                frequency = if(isIncome) frequency else if (isMonthlyExpense)"Monthly" else "Weekly"
                            )
                        )
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor =  if(isIncome) Teal else Red),
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