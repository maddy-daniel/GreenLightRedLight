package com.mads.greenlightredlight

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

@Composable
fun rememberHapticClick(onClick: ()->Unit): ()->Unit {
    val haptic = LocalHapticFeedback.current
    return remember(onClick){
        {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        }
    }
}