package com.mads.greenlightredlight

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SettingsScreen(navController: NavController){
    val context = LocalContext.current
    var isLockEnabled by remember { mutableStateOf(SecurePrefs.isLockEnabled(context)) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = NavyBackground
    ){
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(start = 16.dp, end=16.dp, top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ){
            Text(
                text = "Settings",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(color = Color(0xFF2A2A4A))

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(12.dp)
            )
            {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "App Lock",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Required biometric authentication to open the app",
                            color = MutedText,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = isLockEnabled,
                        onCheckedChange = { enabled->
                            isLockEnabled = enabled
                            SecurePrefs.setLockEnabled(context, enabled)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Teal,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = DarkCard
                        )
                    )
                }
            }

            OutlinedButton(
                onClick = {navController.popBackStack()},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MutedText),
                shape = RoundedCornerShape(12.dp)
            )
            {
                Text(
                    text = "<- Back",
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}