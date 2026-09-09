package com.mads.greenlightredlight

import android.util.Log
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager

private const val TAG = "AddEntryDebug"

// Keeps only digits and a single decimal point so stray characters such as
// (spaces, commas, letters, pasted text, etc.) can never sneak into a numeric
// field regardless of how they were typed/entered.
private fun filterNumericInput(input: String): String{
    val digitsAndDot = input.filter {it.isDigit() || it == '.' }
    val firstDotIndex = digitsAndDot.indexOf('.')
    if(firstDotIndex == -1)
        return digitsAndDot
    val before = digitsAndDot.substring(0, firstDotIndex+1)
    val after = digitsAndDot.substring(firstDotIndex+1).replace(".", "")
    return before + after
}
@Composable
fun AddEntryScreen(navController: NavController, viewModel: BudgetViewModel, entryId: Int?= null) {
    val existingEntry = remember(entryId){
        entryId?.let{
            viewModel.getEntryById(it)
        }
    }
    val isEditing = existingEntry != null

    Log.d(TAG, "AddEntryScreen composed: entryId = $entryId, existingEntry = $existingEntry, isEditing = $isEditing")

    var isIncome by remember  {mutableStateOf(existingEntry?.isIncome ?: true)}
    var isRecurring by remember {mutableStateOf(existingEntry?.isRecurring ?: true)}
    var isHourly by remember { mutableStateOf(existingEntry?.isHourly ?: false) }
    var name by remember {mutableStateOf(existingEntry?.name ?: "")}
    var amount by remember { mutableStateOf(existingEntry?.amount?.toString() ?: "") }
    var frequency by remember{mutableStateOf(existingEntry?.frequency ?: "Weekly")}
    var isMonthlyExpense by remember {mutableStateOf(existingEntry?.let{!it.isIncome && it.frequency == "Monthly"}?:false)}
    var hourlyRate by remember { mutableStateOf("") }
    var hoursWorked by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }
    var hourlyRateError by remember { mutableStateOf<String?>(null) }
    var hoursWorkedError by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current
    val amountFocusRequester = remember{FocusRequester()}
    val hoursWorkedFocusRequester = remember{FocusRequester()}

    Surface(
        modifier = Modifier.fillMaxSize(),
        color= NavyBackground
    ){
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(start = 16.dp, end = 16.dp,top=16.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        if(isEditing)"Edit Entry" else "Add Entry",
                        color = if (isIncome)Teal else Red,
                        fontSize = 18.sp,
                        fontWeight= FontWeight.Medium
                    )
                    IconButton(
                        onClick = {
                            navController.navigate(NavRoutes.HELP)
                        }
                    ){
                        Text(
                            text = "❓",
                            fontSize = 18.sp
                        )
                    }
                }

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
                    onValueChange = {
                        name = it
                        nameError = null },
                    placeholder = {Text("e.g. Paycheck", color = MutedText)},
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            if (isHourly && isIncome) amountFocusRequester.requestFocus()
                            else amountFocusRequester.requestFocus()
                        }
                    ),
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
                nameError?.let{
                    Text(
                        text = it,
                        color = Red,
                        fontSize = 11.sp,
                        modifier = Modifier.offset(y= (-6).dp)
                    )
                }
                if(!isHourly || !isIncome){
                    Text("Amount ($)", color = MutedText, fontSize = 11.sp)
                    OutlinedTextField(
                        value = amount,
                        onValueChange = {input->
                            val filtered = filterNumericInput(input)
                            amount = filtered
                            amountError = null
                            Log.d(TAG, "amount input changed: raw='$input', filtered ='$filtered'")
                        },
                        placeholder = {Text("0.00", color = MutedText)},
                        modifier = Modifier.fillMaxWidth().focusRequester(amountFocusRequester),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()}),
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
                    amountError?.let{
                        Text(
                            text = it,
                            color = Red,
                            fontSize = 11.sp,
                            modifier = Modifier.offset(y = (-6).dp)
                        )
                    }
                }
                else{
                    Text("Pay rate ($/hr)", color = MutedText, fontSize = 11.sp)
                    OutlinedTextField(
                        value = hourlyRate,
                        onValueChange = {input->
                            val filtered = filterNumericInput(input)
                            hourlyRate = filtered
                            hourlyRateError = null
                            Log.d(TAG,"hourlyRate input changed: raw='$input', filtered ='$filtered'")

                            val rate = filtered.toDoubleOrNull() ?:0.0
                            val hours = hoursWorked.toDoubleOrNull() ?:0.0
                            amount = (rate*hours).toString()
                            amountError = null
                            Log.d(TAG, "Calculated amount = $amount")
                        },
                        placeholder = {Text("0.00", color = MutedText)},
                        modifier = Modifier.fillMaxWidth().focusRequester(amountFocusRequester),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = {hoursWorkedFocusRequester.requestFocus()}),
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
                    hourlyRateError?.let{
                        Text(
                            text = it,
                            color = Red,
                            fontSize = 11.sp,
                            modifier = Modifier.offset(y = (-6).dp)
                        )
                    }
                    Text("Hours worked", color = MutedText, fontSize = 11.sp)
                    OutlinedTextField(
                        value = hoursWorked,
                        onValueChange = { input->
                            val filtered = filterNumericInput(input)
                            hoursWorked = filtered
                            hoursWorkedError = null
                            Log.d(TAG, "hoursWorked input changed: raw='$input', filtered ='$filtered'")

                            val rate = hourlyRate.toDoubleOrNull() ?:0.0
                            val hours = filtered.toDoubleOrNull() ?:0.0
                            amount = (rate*hours).toString()
                            Log.d(TAG, "Calculated amount = $amount")
                        },
                        placeholder = {Text("0.00", color = MutedText)},
                        modifier = Modifier.fillMaxWidth().focusRequester(hoursWorkedFocusRequester),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()}),
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
                    hoursWorkedError?.let{
                        Text(
                            text = it,
                            color = Red,
                            fontSize = 11.sp,
                            modifier = Modifier.offset(y = (-6).dp)
                        )
                    }
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
                        Log.d(TAG, "Save tapped: name = '$name', amount = '$amount', parsedAmount = $parsedAmount',isHourly = $isHourly, hourlyRate = '$hourlyRate', hoursWorked='$hoursWorked'")

                        nameError =  if(name.isBlank()) "Please enter a name" else null
                        hourlyRateError = if(isHourly && isIncome && hourlyRate.isBlank()) "Please enter pay rate" else null
                        hoursWorkedError = if(isHourly && isIncome && hoursWorked.isBlank()) "Please enter hours worked" else null
                        amountError = if(parsedAmount<= 0.0 && hourlyRateError == null && hoursWorkedError == null) "Please enter a valid amount greater than 0.0" else null

                        val hasError = nameError != null || hourlyRateError != null || hoursWorkedError != null || amountError != null

                        if (!hasError) {
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
                            Log.d(TAG, "Validation passed. weeklyAmount=$weeklyAmount. Attempting insert...")

                            val entryToSave =
                                Entry(
                                    id = existingEntry?.id?:0,
                                    name = name,
                                    amount = parsedAmount,
                                    weeklyAmount = weeklyAmount,
                                    isIncome = isIncome,
                                    isRecurring = isRecurring,
                                    isHourly = isHourly,
                                    frequency = if(isIncome) frequency else if (isMonthlyExpense)"Monthly" else "Weekly",
                                    dateAdded = existingEntry?.dateAdded?:java.time.LocalDate.now().toString()
                                )
                            if(isEditing){
                                viewModel.updateEntry(entryToSave)
                                Log.d(TAG, "Entry updated successfully")
                            }
                            else{
                                viewModel.addEntry(entryToSave)
                                Log.d(TAG, "Entry submitted to ViewModel successfully")
                            }

                            navController.popBackStack()
                        }
                        else{
                            Log.d(TAG, "Validation FAILED: nameError = $nameError, hourlyRateError = $hourlyRateError, hoursWorkedError = $hoursWorkedError, amountError = $amountError")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor =  if(isIncome) Teal else Red),
                    shape = RoundedCornerShape(8.dp)
                ){
                    Text(if(isEditing) "Update Entry" else "Save Entry", color = NavyBackground, fontWeight = FontWeight.Medium)
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
}