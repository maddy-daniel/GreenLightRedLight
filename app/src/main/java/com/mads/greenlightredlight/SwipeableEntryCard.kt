package com.mads.greenlightredlight

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


private val ACTION_BUTTON_WIDTH = 76.dp
private const val ACTIONS_COUNT = 3

@Composable
fun SwipeableEntryCard(
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDeleteRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val actionsWidthPx = with(density) {
        (ACTION_BUTTON_WIDTH * ACTIONS_COUNT).toPx()
    }
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxWidth()) {
        //Cap font growth for these three buttons only, so the labels always fit
        CompositionLocalProvider(
            LocalDensity provides Density(
                density = density.density,
                fontScale = density.fontScale.coerceAtMost(1.3f)
            )
        )
        {
            //Background action buttons - only visible once the card is dragged left
            Row(
                modifier = Modifier.matchParentSize(),
                horizontalArrangement = Arrangement.End
            ) {
                SwipeAction("Edit", Teal, NavyBackground) {
                    onEdit()
                    scope.launch { offsetX.animateTo(0f) }
                }
                SwipeAction("Copy", Color(0xFF5B8DEF), Color.White) {
                    onDuplicate()
                    scope.launch { offsetX.animateTo(0f) }
                }

                SwipeAction("Delete", Red, Color.White) {
                    onDeleteRequest()
                    scope.launch {
                        offsetX.animateTo(0f)
                    }
                }
            }

            //Foreground content - draggable, slides left to reveal the actions behind it
            Box(
                modifier = Modifier.offset { IntOffset(offsetX.value.roundToInt(), 0) }.pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                val target = if (offsetX.value < -actionsWidthPx / 2) -actionsWidthPx else 0f
                                offsetX.animateTo(target)
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            val newValue = (offsetX.value + dragAmount).coerceIn(-actionsWidthPx, 0f)
                            scope.launch { offsetX.snapTo(newValue) }
                        }
                    )
                }
            ) {
                content()
                //While revealed, an invisible overlay swallows taps and swipes them back
                //Closed instead of letting the tap reach the content underneath.
                if (offsetX.value != 0f) {
                    Box(
                        modifier = Modifier.matchParentSize().clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            scope.launch { offsetX.animateTo(0f) }
                        }
                    )
                }
            }
        }
    }
}
    @Composable
    private fun SwipeAction(
        label: String,
        background: Color,
        textColor: Color,
        onClick: ()->Unit
    ){
        Box(
            modifier = Modifier.width(ACTION_BUTTON_WIDTH).fillMaxHeight()
                .background(background).clickable(onClick = rememberHapticClick(onClick)),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = label,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center
            )
        }
    }
