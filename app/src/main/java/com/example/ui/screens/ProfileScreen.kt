package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UserProfile
import com.example.ui.components.KankaAvatar
import com.example.ui.theme.KankaAccentCyan
import com.example.ui.theme.KankaAccentGreen
import com.example.ui.theme.KankaPrimary
import com.example.ui.viewmodel.KankaViewModel

val availableAvatars = listOf(
    "avatar_robot" to "🤖 Robot",
    "avatar_dev" to "🧑‍💻 Developer",
    "avatar_ninja" to "🥷 Ninja",
    "avatar_wizard" to "🧙‍♂️ Büyücü",
    "avatar_rocket" to "🚀 Roket",
    "avatar_cat" to "🐱 Kedi",
    "avatar_fox" to "🦊 Tilki",
    "avatar_alien" to "👾 Alien"
)

val availableHitaplar = listOf("Kankam", "Reis", "Hocam", "Dostum", "Üstat", "Kardeşim", "Dev", "Müdür")
val availableLevels = listOf("Junior", "Mid-Level", "Senior", "Öğrenci", "Hobist")
val allTechStacks = listOf(
    "Kotlin", "Jetpack Compose", "Python", "TypeScript", "React",
    "Java", "Go", "Rust", "C++", "C#", "Flutter", "Swift", "SQL", "Docker", "Node.js"
)

val availablePersonas = listOf(
    "SAMIMI" to ("🤝 Samimi Kanka" to "Dostça, esprili, samimi ve teknik olarak güçlü"),
    "KIDEMLI" to ("🏛️ Kıdemli Yazılım Mimarı" to "SOLID, Clean Code ve yüksek mimari standartlar"),
    "HIZLI" to ("⚡ Hızlı & Pratik" to "Lafı uzatmadan doğrudan temiz kod ve çözüm"),
    "EGITICI" to ("🎓 Öğretici Mentor" to "Adım adım mantığını anlatan eğitici dost")
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    viewModel: KankaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    var name by remember(currentProfile) { mutableStateOf(currentProfile.name) }
    var hitapSekli by remember(currentProfile) { mutableStateOf(currentProfile.hitapSekli) }
    var avatarId by remember(currentProfile) { mutableStateOf(currentProfile.avatarId) }
    var experienceLevel by remember(currentProfile) { mutableStateOf(currentProfile.experienceLevel) }
    var selectedLanguages by remember(currentProfile) { mutableStateOf(currentProfile.favoriteLanguages.toSet()) }
    var personalityTone by remember(currentProfile) { mutableStateOf(currentProfile.personalityTone) }
    var selectedModel by remember(currentProfile) { mutableStateOf(currentProfile.selectedModel) }
    var customNotes by remember(currentProfile) { mutableStateOf(currentProfile.customNotes) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = KankaPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kullanıcı Profili & Kanka AI",
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
            // Live Preview Greeting Card
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
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        KankaAvatar(
                            avatarId = avatarId,
                            size = 52.dp,
                            showOnlineBadge = true
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Kanka AI Canlı Önizleme",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = KankaPrimary
                            )
                            Text(
                                text = "“Eyvallah $hitapSekli $name! Kodları ateşlemeye hazırım! 🚀”",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // 1. Avatar Selector
            Text(
                text = "Profil Avatarı",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                availableAvatars.forEach { (id, label) ->
                    val isSelected = id == avatarId
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) KankaPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) KankaPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { avatarId = id }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            KankaAvatar(avatarId = id, size = 40.dp, showOnlineBadge = false)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }

            // 2. Name Input & Hitap Şekli
            Text(
                text = "İsim ve Kanka AI Hitap Şekli",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Görünen İsminiz") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_name_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Text(
                text = "Kanka AI sana nasıl seslensin?",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableHitaplar.forEach { item ->
                    FilterChip(
                        selected = item == hitapSekli,
                        onClick = { hitapSekli = item },
                        label = { Text(item) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KankaPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // 3. Experience Level
            Text(
                text = "Yazılım Deneyim Seviyesi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableLevels.forEach { level ->
                    FilterChip(
                        selected = level == experienceLevel,
                        onClick = { experienceLevel = level },
                        label = { Text(level) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KankaAccentCyan,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            // 4. Tech Stack Selector
            Text(
                text = "Kullandığın Teknolojiler & Diller",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                allTechStacks.forEach { tech ->
                    val isSelected = selectedLanguages.contains(tech)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedLanguages = if (isSelected) {
                                selectedLanguages - tech
                            } else {
                                selectedLanguages + tech
                            }
                        },
                        label = { Text(tech, fontSize = 12.sp) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KankaPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // 5. Kanka AI Kişilik Modu
            Text(
                text = "Kanka AI Kişilik & Üslup Tarzı",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                availablePersonas.forEach { (key, pair) ->
                    val (title, desc) = pair
                    val isSelected = key == personalityTone
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) KankaPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) KankaPrimary else Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { personalityTone = key }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isSelected) KankaPrimary else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = desc,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 6. AI Engine Selector
            Text(
                text = "AI Modeli",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    selected = selectedModel == "gemini-3.5-flash",
                    onClick = { selectedModel = "gemini-3.5-flash" },
                    label = { Text("⚡ Gemini 3.5 Flash (Hızlı)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = KankaAccentGreen,
                        selectedLabelColor = Color.Black
                    )
                )
                FilterChip(
                    selected = selectedModel == "gemini-3.1-pro-preview",
                    onClick = { selectedModel = "gemini-3.1-pro-preview" },
                    label = { Text("🧠 Gemini 3.1 Pro (Kod Ustası)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = KankaAccentCyan,
                        selectedLabelColor = Color.Black
                    )
                )
            }

            // 7. Custom Notes
            Text(
                text = "Özel Kanka AI Talimatların",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = customNotes,
                onValueChange = { customNotes = it },
                label = { Text("Kanka AI'ya özel notların (örn: Kodları her zaman Kotlin coroutines ile yaz)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // Save Button
            Button(
                onClick = {
                    val updated = currentProfile.copy(
                        name = name.ifBlank { "Yazılımcı" },
                        hitapSekli = hitapSekli,
                        avatarId = avatarId,
                        experienceLevel = experienceLevel,
                        favoriteLanguages = selectedLanguages.toList(),
                        personalityTone = personalityTone,
                        selectedModel = selectedModel,
                        customNotes = customNotes
                    )
                    viewModel.updateUserProfile(updated)
                    Toast.makeText(context, "Profilin başarıyla güncellendi $hitapSekli! 🚀", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_profile_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KankaPrimary)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Profili Kaydet & Uygula",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
