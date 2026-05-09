package com.mads.greenlightredlight

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun PlaceholderScreen(navController: NavController, title: String) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = NavyBackground
    ){
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = {navController.popBackStack()},
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MutedText),
                shape = RoundedCornerShape(8.dp)
            ){
                Text("<- Back")
            }
        }
    }
}