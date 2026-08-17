package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.ChatMessage
import com.example.ui.theme.KankaPrimary
import kotlinx.coroutines.launch

/**
 * MessageBubbleList is a dedicated, performant list component that renders chat messages
 * between the user and Kanka AI with auto-scrolling, thinking animation state, and
 * a scroll-to-bottom floating action indicator.
 */
@Composable
fun MessageBubbleList(
    messages: List<ChatMessage>,
    isGenerating: Boolean,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    onSaveToVault: ((language: String, code: String) -> Unit)? = null,
    emptyContent: @Composable () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll to the latest message whenever messages change or generation starts
    LaunchedEffect(messages.size, isGenerating) {
        if (messages.isNotEmpty()) {
            val targetIndex = if (isGenerating) messages.size else messages.size - 1
            if (targetIndex >= 0) {
                listState.animateScrollToItem(targetIndex)
            }
        }
    }

    // Determine if the user is scrolled away from the bottom
    val showScrollToBottom by remember {
        derivedStateOf {
            val totalItems = messages.size + if (isGenerating) 1 else 0
            if (totalItems <= 2) false
            else {
                val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                lastVisibleItemIndex < totalItems - 2
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("message_bubble_list")
    ) {
        if (messages.isEmpty()) {
            emptyContent()
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(
                    items = messages,
                    key = { it.messageId }
                ) { message ->
                    MessageBubble(
                        message = message,
                        onSaveToVault = onSaveToVault
                    )
                }

                if (isGenerating) {
                    item(key = "thinking_indicator") {
                        ThinkingIndicatorBubble()
                    }
                }
            }
        }

        // Floating Scroll-to-Bottom Button
        AnimatedVisibility(
            visible = showScrollToBottom,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        val totalItems = messages.size + if (isGenerating) 1 else 0
                        if (totalItems > 0) {
                            listState.animateScrollToItem(totalItems - 1)
                        }
                    }
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = KankaPrimary,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .size(40.dp)
                    .testTag("scroll_to_bottom_button")
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "En alta kaydır",
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
