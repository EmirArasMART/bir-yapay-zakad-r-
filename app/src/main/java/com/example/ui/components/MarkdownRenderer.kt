package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KankaAccentCyan

sealed class ContentBlock {
    data class TextBlock(val text: String) : ContentBlock()
    data class CodeBlock(val language: String, val code: String) : ContentBlock()
}

@Composable
fun MarkdownRenderer(
    content: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onSaveToVault: ((language: String, code: String) -> Unit)? = null
) {
    val blocks = parseContentBlocks(content)

    Column(modifier = modifier.fillMaxWidth()) {
        blocks.forEach { block ->
            when (block) {
                is ContentBlock.TextBlock -> {
                    RenderFormattedText(block.text, textColor)
                }
                is ContentBlock.CodeBlock -> {
                    CodeBlockView(
                        language = block.language,
                        code = block.code,
                        onSaveToVault = onSaveToVault
                    )
                }
            }
        }
    }
}

@Composable
private fun RenderFormattedText(rawText: String, defaultColor: Color) {
    val lines = rawText.lines()
    Column {
        lines.forEach { line ->
            val trimmed = line.trim()
            when {
                trimmed.startsWith("### ") -> {
                    Text(
                        text = buildInlineMarkdown(trimmed.removePrefix("### "), defaultColor),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                trimmed.startsWith("## ") -> {
                    Text(
                        text = buildInlineMarkdown(trimmed.removePrefix("## "), defaultColor),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                trimmed.startsWith("# ") -> {
                    Text(
                        text = buildInlineMarkdown(trimmed.removePrefix("# "), defaultColor),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
                trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "•",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                        Text(
                            text = buildInlineMarkdown(trimmed.substring(2), defaultColor),
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp
                        )
                    }
                }
                trimmed.isNotEmpty() -> {
                    Text(
                        text = buildInlineMarkdown(trimmed, defaultColor),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
                else -> {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

private fun buildInlineMarkdown(text: String, defaultColor: Color): AnnotatedString {
    return buildAnnotatedString {
        var cursor = 0
        val length = text.length

        while (cursor < length) {
            // Check for bold **text**
            if (cursor + 1 < length && text[cursor] == '*' && text[cursor + 1] == '*') {
                val end = text.indexOf("**", cursor + 2)
                if (end != -1) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = defaultColor)) {
                        append(text.substring(cursor + 2, end))
                    }
                    cursor = end + 2
                    continue
                }
            }

            // Check for inline code `code`
            if (text[cursor] == '`') {
                val end = text.indexOf('`', cursor + 1)
                if (end != -1) {
                    withStyle(
                        SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6750A4),
                            background = Color(0xFFEADDFF)
                        )
                    ) {
                        append(" ${text.substring(cursor + 1, end)} ")
                    }
                    cursor = end + 1
                    continue
                }
            }

            // Default character
            withStyle(SpanStyle(color = defaultColor)) {
                append(text[cursor])
            }
            cursor++
        }
    }
}

private fun parseContentBlocks(content: String): List<ContentBlock> {
    val blocks = mutableListOf<ContentBlock>()
    val codeRegex = Regex("```([a-zA-Z0-9_-]*)\\s*\\n([\\s\\S]*?)```")
    var lastIndex = 0

    val matches = codeRegex.findAll(content)
    for (match in matches) {
        val matchStart = match.range.first
        val matchEnd = match.range.last + 1

        if (matchStart > lastIndex) {
            val textPart = content.substring(lastIndex, matchStart).trim()
            if (textPart.isNotEmpty()) {
                blocks.add(ContentBlock.TextBlock(textPart))
            }
        }

        val language = match.groupValues[1].ifBlank { "code" }
        val code = match.groupValues[2]
        blocks.add(ContentBlock.CodeBlock(language, code))

        lastIndex = matchEnd
    }

    if (lastIndex < content.length) {
        val remainingText = content.substring(lastIndex).trim()
        if (remainingText.isNotEmpty()) {
            blocks.add(ContentBlock.TextBlock(remainingText))
        }
    }

    if (blocks.isEmpty() && content.isNotEmpty()) {
        blocks.add(ContentBlock.TextBlock(content))
    }

    return blocks
}
