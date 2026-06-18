package com.mads.greenlightredlight

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

@Composable
fun CalendarScreen(navController: NavController, viewModel: BudgetViewModel) {
    val entries by viewModel.entries.collectAsState()
    var currentMonth by remember{
        mutableStateOf(YearMonth.now())
    }

    var selectedDate by remember{
        mutableStateOf<LocalDate?>(null)
    }

    var selectedWeekStart by remember{
        mutableStateOf<LocalDate?>(null)
    }

    val today = LocalDate.now()
    val datesWithEntries = entries.map{
        it.dateAdded
    }.toSet()


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = NavyBackground
    )
    {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp, top= 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ){
            item{
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Text(
                        text = "◀",
                        color = Teal,
                        fontSize = 18.sp,
                        modifier = Modifier.clickable { currentMonth = currentMonth.minusMonths(1) }
                    )
                    Text(
                        currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "▶",
                        color = Teal,
                        fontSize = 18.sp,
                        modifier = Modifier.clickable { currentMonth = currentMonth.plusMonths(1) }
                    )
                }
            }
            item{
                Row(modifier = Modifier.fillMaxWidth()){
                    Spacer(modifier = Modifier.width(28.dp))
                    listOf("Sun", "Mon","Tue", "Wed", "Thu", "Fri","Sat").forEach {
                        day->
                        Text(
                            day,
                            color = MutedText,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            val firstDayOfMonth = currentMonth.atDay(1)
            val lastDayOfMonth = currentMonth.atEndOfMonth()
            val firstSunday = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))

            val weeks = mutableListOf<List<LocalDate>>()
            var current = firstSunday
            while(!current.isAfter(lastDayOfMonth)){
                val week = (0..6).map{
                    current.plusDays(it.toLong())
                }
                weeks.add(week)
                current = current.plusWeeks(1)
            }

            items(weeks){
                week->
                val weekStart = week.first()
                val weekEnd = week.last()
                val weekNumber = weekStart.format(DateTimeFormatter.ofPattern("w"))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = weekNumber,
                        color = Teal,
                        fontSize = 10.sp,
                        modifier = Modifier.width(28.dp).clickable {
                            selectedWeekStart = weekStart
                            selectedDate = null
                        },
                        textAlign = TextAlign.Center
                    )

                    week.forEach{
                        date->
                        val isToday = date == today
                        val isSelected = date == selectedDate
                        val isCurrentMonth = date.month == currentMonth.month
                        val hasEntries = datesWithEntries.contains(date.toString())

                        Column(
                            modifier = Modifier.weight(1f).clickable {
                                selectedDate = date
                                selectedWeekStart = null
                            },
                            horizontalAlignment = Alignment.CenterHorizontally
                        )
                        {
                            Box(
                                modifier = Modifier.size(30.dp).clip(CircleShape).background(
                                    when{
                                        isToday -> Teal
                                        isSelected -> Color(0xFF2A2A4A)
                                        else -> Color.Transparent
                                    }
                                ).then(
                                if(isSelected && !isToday)
                                    Modifier.border(1.dp, MutedText, CircleShape)
                                else Modifier
                            ),
                            contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    date.dayOfMonth.toString(),
                                    color = when {
                                        isToday -> NavyBackground
                                        isCurrentMonth -> Color.White
                                        else -> Color(0xFF444466)
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            if(hasEntries){
                                Box(
                                    modifier = Modifier.size(5.dp).clip(CircleShape).background(Teal)
                                )
                            }
                            else{
                                Spacer(modifier = Modifier.height(5.dp))
                            }
                        }
                    }
                }
            }
            selectedDate?.let{
                date->
                val dateEntries = viewModel.getEntriesForDate(date.toString())
                item{
                    HorizontalDivider(color = Color(0xFF2A2A4A))
                    Text(
                        date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d yyyy")),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                if(dateEntries.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkCard),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "No entries on this date",
                                color = MutedText,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
                else{
                    items(dateEntries){
                        entry->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkCard),
                            shape = RoundedCornerShape(8.dp)
                        )
                        {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            )
                            {
                                Text(
                                    text = "${entry.name} - ${if(entry.isRecurring)"Recurring" else "Incidental"}",
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                                Text(
                                    "$${String.format("%.2f", entry.weeklyAmount)}/wk",
                                    color = if(entry.isIncome) Teal else Red,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            selectedWeekStart?.let{
                weekStart->
                val weekEnd = weekStart.plusDays(6)
                val weekEntries = viewModel.getEntriesForWeek(
                    weekStart.toString(),
                    weekEnd.toString()
                )
                val weekIncome = weekEntries.filter {it.isIncome}.sumOf {
                    if(it.isHourly)
                        TaxCalculator.calculateNetTakeHome(it.weeklyAmount)
                    else
                        it.weeklyAmount
                }

                val weekExpenses = weekEntries.filter{!it.isIncome}.sumOf{it.weeklyAmount}
                val weekNet = weekIncome - weekExpenses
                val isGreen = weekNet >= 0

                item{
                    HorizontalDivider(color = Color(0xFF2A2A4A))
                    Text(
                        text = "Week of ${weekStart.format(DateTimeFormatter.ofPattern("MMM d"))} - ${weekEnd.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(12.dp)
                    ){
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        )
                        {
                            Text("Total income", color = MutedText, fontSize = 12.sp)
                            Text(
                                text = "$${String.format("%.2f", weekIncome)}",
                                color = Teal,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text("Total expenses", color = MutedText, fontSize = 12.sp)
                            Text(
                                text = "$${String.format("%.2f", weekExpenses)}",
                                color = Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        HorizontalDivider(color = Color(0xFF2A2A4A))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        )
                            {
                                Text("Net balance", color = MutedText, fontSize = 12.sp)
                                Text(
                                    text = "$${String.format("%.2f", weekNet)}",
                                    color = if(isGreen) Teal else Red,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if(isGreen) Color(0xFF0D3B2E) else Color(0xFF2B0D1A)
                            ) {
                            Text(
                                if (isGreen) "Green Light" else "Red Light",
                                color = if (isGreen) Teal else Red,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}