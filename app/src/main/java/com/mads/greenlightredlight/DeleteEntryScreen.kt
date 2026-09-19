package com.mads.greenlightredlight

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.Alignment

@Composable
fun DeleteEntryScreen(navController: NavController, viewModel: BudgetViewModel) {
    var isIncome by remember { mutableStateOf(true) }
    var selectedEntryId by remember { mutableStateOf<Int?>(null) }
    var entryPendingDelete by remember {mutableStateOf<Entry?>(null)}

    val entries by viewModel.entries.collectAsState()
    val filteredEntries = entries.filter{it.isIncome == isIncome}

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = NavyBackground
    ){
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text("Delete Entry", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                IconButton(
                    onClick = rememberHapticClick{
                        navController.navigate(NavRoutes.HELP)
                    }
                ){
                    Text(
                        text = "❓",
                        fontSize = 18.sp
                    )
                }
            }

            Text("Type", color = MutedText, fontSize = 11.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                Button(
                    onClick = rememberHapticClick{
                        isIncome = true
                        selectedEntryId = null
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if(isIncome) Teal else DarkCard
                    ),
                    shape = RoundedCornerShape(8.dp)
                ){
                    Text("Income",color = if(!isIncome) Color.White else MutedText)
                }
                Button(
                    onClick = rememberHapticClick{
                        isIncome = false
                        selectedEntryId = null
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if(!isIncome) Red else DarkCard
                    ),
                    shape = RoundedCornerShape(8.dp)
                ){
                    Text("Expense", color = if(!isIncome) Color.White else MutedText)
                }
            }
            Text("Select entry to delete", color = MutedText, fontSize = 11.sp)

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                if(filteredEntries.isEmpty()){
                    item{
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkCard),
                            shape = RoundedCornerShape(6.dp)
                        ){
                            Text(
                                "No entries yet",
                                color = MutedText,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
                else{
                    items(filteredEntries) { entry ->
                        Card(
                            onClick = rememberHapticClick{ selectedEntryId = entry.id },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedEntryId == entry.id)
                                    if (isIncome) Color(0xFF0D3B2E) else Color(0xFF3B0D1A)
                                else {
                                    DarkCard
                                }
                            ),
                            shape = RoundedCornerShape(6.dp),
                            border = if (selectedEntryId == entry.id)
                                ButtonDefaults.outlinedButtonBorder(enabled = true)
                            else {
                                null
                            }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            )
                            {
                                Text(
                                    "${entry.name} - ${if (entry.isRecurring) "Recurring" else "Incidental"}",
                                    color = Color.White,
                                    fontSize = 11.sp
                                )

                                Text(
                                    "$%.2f".format(entry.amount),
                                    color = if (isIncome) Teal else Red,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
            Button(
                onClick = rememberHapticClick{
                    entryPendingDelete = selectedEntryId?.let{id->entries.find {it.id == id}}
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedEntryId != null,
                colors = ButtonDefaults.buttonColors(containerColor = Red),
                shape = RoundedCornerShape(8.dp)
            )
            {
                Text("Delete selected", color = Color.White, fontWeight = FontWeight.Medium)
            }

            OutlinedButton(
                onClick = rememberHapticClick{navController.popBackStack()},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MutedText),
                shape = RoundedCornerShape(8.dp)
            ){
                Text("Cancel")
            }
        }
        entryPendingDelete?.let {
            entry->
            AlertDialog(
                onDismissRequest = { entryPendingDelete = null },
                title = {Text(
                    text = "Delete entry",
                    fontWeight = FontWeight.Medium
                )},
                text = {Text("Are you sure you want to delete this entry?")},
                confirmButton = {
                    TextButton(
                        onClick = rememberHapticClick{
                            viewModel.deleteEntry(entry.id)
                            navController.popBackStack()
                            entryPendingDelete = null
                        }
                    ){
                        Text("Confirm", color = Red)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = rememberHapticClick { entryPendingDelete = null }
                    ){
                        Text("Cancel",color = MutedText)
                    }
                },
                containerColor = DarkCard,
                titleContentColor = Color.White,
                textContentColor = MutedText
            )
        }
    }
}