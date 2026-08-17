package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.CodeBlockBackground
import com.example.ui.theme.KankaAccentAmber
import com.example.ui.theme.KankaAccentCyan
import com.example.ui.theme.KankaAccentGreen
import com.example.ui.theme.KankaAccentPurple
import com.example.ui.theme.KankaPrimary
import com.example.ui.viewmodel.KankaViewModel

data class DevToolItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val defaultPlaceholder: String
)

val devToolsList = listOf(
    DevToolItem(
        id = "Bug Fix",
        title = "Hata & Bug Ayıklayıcı",
        description = "Hata veren kodu veya crash logunu gir, anında düzeltilmiş halini al.",
        icon = Icons.Default.BugReport,
        color = Color(0xFFEF4444),
        defaultPlaceholder = "// Hatalı kodu veya Stacktrace logunu buraya yapıştır..."
    ),
    DevToolItem(
        id = "Kod Üret",
        title = "Özellik & Kod Üretici",
        description = "İstediğin fonksiyon, sınıf, UI bileşeni veya servisi sıfırdan ürettir.",
        icon = Icons.Default.Code,
        color = KankaAccentCyan,
        defaultPlaceholder = "Örn: Room database ve ViewModel ile çalışan bir ürün listesi Repository'si yaz..."
    ),
    DevToolItem(
        id = "Algoritma Çöz",
        title = "Algoritma & LeetCode Çözücü",
        description = "Karmaşık veri yapıları ve algoritmaları en optimize Big-O ile çöz.",
        icon = Icons.Default.Psychology,
        color = KankaAccentPurple,
        defaultPlaceholder = "Örn: LRU Cache veri yapısını O(1) get ve put karmaşıklığı ile tasarla..."
    ),
    DevToolItem(
        id = "Mimari & SOLID",
        title = "Mimari & Clean Code",
        description = "MVVM, MVI, Clean Architecture ve SOLID prensiplerine uygun mimari danışmanlık.",
        icon = Icons.Default.Functions,
        color = KankaAccentAmber,
        defaultPlaceholder = "Örn: Çok katmanlı bir e-ticaret sepet yönetimi için Clean Architecture modüllerini çıkar..."
    ),
    DevToolItem(
        id = "Optimizasyon",
        title = "Performans & Refactor",
        description = "Kodun bellek, CPU ve asenkron çalışma performansını maksimize et.",
        icon = Icons.Default.Speed,
        color = KankaAccentGreen,
        defaultPlaceholder = "// Yavaş çalışan veya optimize etmek istediğin kod bloğunu yapıştır..."
    ),
    DevToolItem(
        id = "Birim Testi",
        title = "Birim Testi (Unit Test) Yazıcı",
        description = "Mocking, Robolectric ve JUnit ile %100 kapsayıcı testler hazırla.",
        icon = Icons.Default.Science,
        color = Color(0xFF38BDF8),
        defaultPlaceholder = "// Testini yazmak istediğin sınıf veya fonksiyonu buraya yapıştır..."
    )
)

val supportedLanguages = listOf(
    "Kotlin", "Jetpack Compose", "Python", "TypeScript", "JavaScript",
    "Java", "Go", "Rust", "SQL", "C++", "C#", "Flutter / Dart", "Swift", "HTML / CSS"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevToolsScreen(
    viewModel: KankaViewModel,
    onNavigateToChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    var selectedTool by remember { mutableStateOf(devToolsList[0]) }
    var selectedLanguage by remember { mutableStateOf(userProfile.favoriteLanguages.firstOrNull() ?: "Kotlin") }
    var inputCode by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = KankaPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kanka Kod Araçları",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Intro Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(KankaPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Geliştirici Problem Çözücü 🚀",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Aracını ve dilini seç, Kanka AI anında profesyonel çözümü üretsin!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Tool selection Chips
            Text(
                text = "1. Geliştirici Aracını Seç",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                devToolsList.forEach { tool ->
                    val isSelected = tool.id == selectedTool.id
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedTool = tool
                        },
                        label = { Text(tool.title, fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = tool.icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else tool.color,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = tool.color,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Language Selector
            Text(
                text = "2. Programlama Dili / Framework",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                supportedLanguages.forEach { lang ->
                    val isSelected = lang == selectedLanguage
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedLanguage = lang },
                        label = { Text(lang, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KankaPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Code & Prompt Input Area
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = selectedTool.icon,
                                contentDescription = null,
                                tint = selectedTool.color,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = selectedTool.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            text = selectedLanguage,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = KankaAccentCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedTool.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = inputCode,
                        onValueChange = { inputCode = it },
                        placeholder = {
                            Text(
                                text = selectedTool.defaultPlaceholder,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CodeBlockBackground,
                            unfocusedContainerColor = CodeBlockBackground,
                            focusedTextColor = Color(0xFFE2E8F0),
                            unfocusedTextColor = Color(0xFFE2E8F0)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val content = inputCode.ifBlank { selectedTool.defaultPlaceholder }
                            viewModel.sendDevToolPrompt(
                                actionTitle = selectedTool.id,
                                inputCodeOrQuery = content,
                                language = selectedLanguage
                            )
                            onNavigateToChat()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = selectedTool.color
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kanka AI ile Anında Çöz 🚀",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
