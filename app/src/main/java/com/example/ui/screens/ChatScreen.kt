package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ChatMessage
import com.example.ui.components.KankaAvatar
import com.example.ui.components.MessageBubbleList
import com.example.ui.components.MessageInputBar
import com.example.ui.components.QuickPromptChips
import com.example.ui.theme.KankaAccentCyan
import com.example.ui.theme.KankaPrimary
import com.example.ui.theme.KankaPrimaryDark
import com.example.ui.viewmodel.KankaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: KankaViewModel,
    onOpenSessionsDrawer: () -> Unit,
    onNavigateToDevTools: () -> Unit,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.currentMessages.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val sessions by viewModel.sessions.collectAsStateWithLifecycle()
    val currentSessionId by viewModel.currentSessionId.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val activeSession = sessions.find { it.sessionId == currentSessionId }
    val sessionTitle = activeSession?.title ?: "Kanka AI Sohbeti"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        KankaAvatar(
                            avatarId = userProfile.avatarId,
                            size = 36.dp,
                            showOnlineBadge = true,
                            isThinking = isGenerating
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Kanka AI",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(KankaPrimary.copy(alpha = 0.12f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (userProfile.selectedModel.contains("pro")) "PRO 🧠" else "FLASH ⚡",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = KankaPrimary
                                    )
                                }
                            }
                            Text(
                                text = sessionTitle,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenSessionsDrawer) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Sohbet Geçmişi",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    // New Chat Action
                    IconButton(onClick = { viewModel.createNewSession("Yeni Sohbet") }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Yeni Sohbet",
                            tint = KankaPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            // Message Bubble List
            Box(modifier = Modifier.weight(1f)) {
                MessageBubbleList(
                    messages = messages,
                    isGenerating = isGenerating,
                    listState = listState,
                    onSaveToVault = { language, code ->
                        val title = "Kod - ${language.uppercase()}"
                        viewModel.saveSnippet(title, language, code)
                    },
                    emptyContent = {
                        EmptyChatGreeting(
                            userName = userProfile.name,
                            hitap = userProfile.hitapSekli,
                            onPromptSelected = { prompt ->
                                inputText = prompt
                            },
                            onNavigateToDevTools = onNavigateToDevTools
                        )
                    }
                )
            }

            // Quick Prompt Chips
            QuickPromptChips(
                onPromptClick = { prompt ->
                    viewModel.sendMessage(prompt)
                }
            )

            // Message Input Bar
            MessageInputBar(
                inputText = inputText,
                onInputTextChanged = { inputText = it },
                onSendMessage = { text ->
                    viewModel.sendMessage(text)
                },
                isGenerating = isGenerating
            )
        }
    }
}

@Composable
private fun EmptyChatGreeting(
    userName: String,
    hitap: String,
    onPromptSelected: (String) -> Unit,
    onNavigateToDevTools: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        KankaAvatar(
            avatarId = "avatar_robot",
            size = 72.dp,
            showOnlineBadge = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Selam $userName! Ben Kanka AI 🚀",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Seninle kod yazmak, zorlu bug'ları çözmek ve projelerini hızlandırmak için buradayım $hitap! Bir soru sor veya araçları dene:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            onClick = onNavigateToDevTools,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = KankaPrimary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "🛠️ Kanka Kod Çözücü Merkezi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Bug Fix, Algoritma Çözücü, Mimari & Test üreticisine göz at",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
