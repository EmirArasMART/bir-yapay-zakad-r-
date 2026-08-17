package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KankaPrimary

/**
 * MessageInputBar is a dedicated chat input bar component for Kanka AI.
 * It features multiline expanding text entry, a code snippet helper menu,
 * dynamic send actions, clear-text shortcuts, and active state management.
 */
@Composable
fun MessageInputBar(
    inputText: String,
    onInputTextChanged: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    isGenerating: Boolean,
    modifier: Modifier = Modifier,
    placeholder: String = "Kanka'ya sor veya kod yapıştır...",
    onInsertCodeTemplate: ((String) -> Unit)? = null
) {
    var showCodeMenu by remember { mutableStateOf(false) }

    val canSend = inputText.isNotBlank() && !isGenerating

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("message_input_bar")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                // Code block insertion shortcut button with dropdown template menu
                Box {
                    IconButton(
                        onClick = { showCodeMenu = true },
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("code_snippet_helper_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = "Kod Bloğu Ekle",
                            tint = KankaPrimary
                        )
                    }

                    DropdownMenu(
                        expanded = showCodeMenu,
                        onDismissRequest = { showCodeMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Kotlin Bloğu (```kotlin)") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DataObject,
                                    contentDescription = null,
                                    tint = KankaPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                showCodeMenu = false
                                val codeTemplate = "\n```kotlin\n// Kotlin kodunu buraya yapıştır\n\n```"
                                val newText = if (inputText.isBlank()) "```kotlin\n// Kotlin kodunu buraya yapıştır\n\n```" else "$inputText$codeTemplate"
                                onInputTextChanged(newText)
                                onInsertCodeTemplate?.invoke("kotlin")
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("JavaScript/TS Bloğu (```ts)") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DataObject,
                                    contentDescription = null,
                                    tint = KankaPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                showCodeMenu = false
                                val codeTemplate = "\n```typescript\n// TypeScript kodunu buraya yapıştır\n\n```"
                                val newText = if (inputText.isBlank()) "```typescript\n// TypeScript kodunu buraya yapıştır\n\n```" else "$inputText$codeTemplate"
                                onInputTextChanged(newText)
                                onInsertCodeTemplate?.invoke("typescript")
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Python Bloğu (```python)") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DataObject,
                                    contentDescription = null,
                                    tint = KankaPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                showCodeMenu = false
                                val codeTemplate = "\n```python\n# Python kodunu buraya yapıştır\n\n```"
                                val newText = if (inputText.isBlank()) "```python\n# Python kodunu buraya yapıştır\n\n```" else "$inputText$codeTemplate"
                                onInputTextChanged(newText)
                                onInsertCodeTemplate?.invoke("python")
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Genel Kod Bloğu (```)") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = null,
                                    tint = KankaPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                showCodeMenu = false
                                val codeTemplate = "\n```\n\n```"
                                val newText = if (inputText.isBlank()) "```\n\n```" else "$inputText$codeTemplate"
                                onInputTextChanged(newText)
                                onInsertCodeTemplate?.invoke("code")
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Text input field
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputTextChanged,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    placeholder = {
                        Text(
                            text = placeholder,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    },
                    trailingIcon = {
                        if (inputText.isNotEmpty()) {
                            IconButton(
                                onClick = { onInputTextChanged("") },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Metni Temizle",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    maxLines = 5,
                    shape = RoundedCornerShape(24.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Default
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KankaPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Send Button with animated appearance
                IconButton(
                    onClick = {
                        if (canSend) {
                            val textToSend = inputText.trim()
                            onSendMessage(textToSend)
                            onInputTextChanged("")
                        }
                    },
                    enabled = canSend,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (canSend) KankaPrimary
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                        .testTag("send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Gönder",
                        tint = if (canSend) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Optional subtle helper text if input contains code blocks
            if (inputText.contains("```")) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 48.dp, bottom = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        tint = KankaPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Kod bloğu algılandı — Kanka AI otomatik syntax ve debug analizine hazır",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = KankaPrimary
                    )
                }
            }
        }
    }
}
