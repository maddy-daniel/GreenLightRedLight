package com.mads.greenlightredlight

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


private val ACTION_BUTTON_WIDTH = 64.dp
private const val ACTIONS_COUNT = 3

@Composable
fun SwipeableEntryCard(
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDeleteRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
){
    val density = LocalDensity.current
    val actionsWidthPx = with(density){
        (ACTION_BUTTON_WIDTH * ACTIONS_COUNT).toPx()
    }
    val offsetX = remember { Animatable(0f)}
    val scope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize()) {
        //Background action buttons - only visible once the card is dragged left
        Row(
            modifier = Modifier.matchParentSize(),
            horizontalArrangement = Arrangement.End
        ){
            Button(
                onClick = rememberHapticClick{
                    onEdit()
                    scope.launch{offsetX.animateTo(0f)}
                },
                modifier = Modifier.width(ACTION_BUTTON_WIDTH).fillMaxHeight(),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Teal)
            )
            {
                Text(
                    text = "Edit",
                    color = NavyBackground,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Button(
                onClick = rememberHapticClick{
                    onDuplicate()
                    scope.launch{offsetX.animateTo(0f)}
                },
                modifier = Modifier.width(ACTION_BUTTON_WIDTH).fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B8DEF))
            )
            {
                Text(
                    text = "Copy",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Button(
                onClick = rememberHapticClick{
                    onDeleteRequest()
                    scope.launch{offsetX.animateTo(0f)}
                },
                modifier = Modifier.width(ACTION_BUTTON_WIDTH).fillMaxHeight(),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Red)
            )
            {
                Text(
                    text = "Delete",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        //Foreground content - draggable, slides left to reveal the actions behind it
        Box(
            modifier = Modifier.offset{ IntOffset(offsetX.value.roundToInt(), 0)}.pointerInput(Unit){
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch{
                                val target = if(offsetX.value < -actionsWidthPx / 2) -actionsWidthPx else 0f
                                offsetX.animateTo(target)
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            val newValue = (offsetX.value + dragAmount).coerceIn(-actionsWidthPx, 0f)
                            scope.launch{offsetX.snapTo(newValue)}
                        }
                    )
                }
        ){
            content()
            //While revealed, an invisible overlay swallows taps and swipes them back
            //Closed instead of letting the tap reach the content underneath.
            if(offsetX.value != 0f){
                Box(
                    modifier = Modifier.matchParentSize().clickable(
                        indication = null,
                        interactionSource = remember {MutableInteractionSource()}
                    ){
                        scope.launch{offsetX.animateTo(0f)}
                    }
                )
            }
        }
    }
}
