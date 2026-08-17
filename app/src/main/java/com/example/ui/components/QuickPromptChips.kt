package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KankaAccentCyan
import com.example.ui.theme.KankaPrimaryDark

data class QuickPrompt(
    val title: String,
    val prompt: String
)

val defaultQuickPrompts = listOf(
    QuickPrompt("🐛 Bug Ayıkla", "Aşağıdaki kodda veya hata çıktısında bir problem var. Hatanın nedenini tespit et ve bana çalışan, düzeltilmiş kodu ver:\n\n```\n// Kodunu veya stacktrace'ini buraya yapıştır\n```"),
    QuickPrompt("⚡ Compose UI Yaz", "Bana Jetpack Compose ile modern, animasyonlu ve Material 3 uyumlu bir arayüz bileşeni yaz."),
    QuickPrompt("🧠 Algoritma Çöz", "Karmaşık bir algoritma problemi için en optimize (zaman ve bellek açısından) çözümü Big-O analiziyle açıkla."),
    QuickPrompt("🏗️ Mimari & MVVM", "Clean Architecture ve MVVM prensiplerine uygun ViewModel, StateFlow ve Repository katmanlarını hazırla."),
    QuickPrompt("🚀 Kod Optimizasyonu", "Aşağıdaki kodu performans, bellek tüketimi ve okunabilirlik açısından optimize et:\n\n```\n// Kodunu buraya yapıştır\n```"),
    QuickPrompt("🧪 Birim Testi Yaz", "Verilen sınıf ve fonksiyonlar için tüm edge-case'leri kapsayan Robolectric / JUnit birim testleri yaz.")
)

@Composable
fun QuickPromptChips(
    onPromptClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    prompts: List<QuickPrompt> = defaultQuickPrompts
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        prompts.forEach { item ->
            AssistChip(
                onClick = { onPromptClick(item.prompt) },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                shape = RoundedCornerShape(20.dp),
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = AssistChipDefaults.assistChipBorder(
                    borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    borderWidth = 1.dp,
                    enabled = true
                )
            )
        }
    }
}
