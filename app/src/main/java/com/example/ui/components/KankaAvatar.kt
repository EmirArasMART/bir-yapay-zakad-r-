package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KankaAccentCyan
import com.example.ui.theme.KankaAccentGreen
import com.example.ui.theme.KankaPrimary

@Composable
fun KankaAvatar(
    avatarId: String = "avatar_robot",
    size: Dp = 40.dp,
    showOnlineBadge: Boolean = true,
    isThinking: Boolean = false,
    modifier: Modifier = Modifier
) {
    val emoji = when (avatarId) {
        "avatar_robot" -> "🤖"
        "avatar_dev" -> "🧑‍💻"
        "avatar_ninja" -> "🥷"
        "avatar_wizard" -> "🧙‍♂️"
        "avatar_cat" -> "🐱"
        "avatar_rocket" -> "🚀"
        "avatar_fox" -> "🦊"
        "avatar_alien" -> "👾"
        else -> "🤖"
    }

    Box(modifier = modifier.size(size)) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(KankaPrimary, Color(0xFF9A82DB))
                    )
                )
                .border(1.5.dp, Color(0xFFEADDFF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = (size.value * 0.52f).sp
            )
        }

        if (showOnlineBadge) {
            val badgeColor = if (isThinking) Color(0xFF7E5700) else Color(0xFF386A20)
            Box(
                modifier = Modifier
                    .size(size * 0.28f)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(badgeColor)
                    .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
            )
        }
    }
}
