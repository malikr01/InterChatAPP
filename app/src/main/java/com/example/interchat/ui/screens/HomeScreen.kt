package com.example.interchat.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.interchat.R
import com.example.interchat.data.CredentialsStore
import com.example.interchat.data.MockAuthRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

/* ------------------------------------------------------------- */
/*                        HOME SCREEN (FINAL)                    */
/* ------------------------------------------------------------- */

@Composable
fun HomeScreen(
    userName: String = "Ahmet",
    onOpenChat: () -> Unit = {},
    onOpenTransactions: () -> Unit = {},
    onLoggedOut: () -> Unit = {}, // ✅ Logout bittikten sonra çalıştırılacak callback
    // MOCK – API gelince dışarıdan ver
    totalBalanceText: String = "₺45,230",
    incomeThisMonthText: String = "₺12,500",
    expenseThisMonthText: String = "₺7,340",
    savingThisMonthText: String = "₺5,160",
    transactionsMock: List<Tx> = demoTransactions()
) {
    val headerBrush = Brush.verticalGradient(listOf(Color(0xFF318CFF), Color(0xFF7C4DFF)))
    val transactions = remember(transactionsMock) { transactionsMock }

    // ✅ Logout altyapısı (repo + coroutine)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val store = remember { CredentialsStore(context) }
    val authRepo = remember { MockAuthRepository(store) }

    val doLogout: () -> Unit = remember{
        {
            scope.launch {
                // kayıtlı oturum bilgilerini kapat
                authRepo.logout()
                // (opsiyonel) kalıcı kimlik bilgilerini de silmek istersen:
                // store.clear()
                onLoggedOut()
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        // Üst degrade + dekoratif daireler
        Box(Modifier.fillMaxWidth().height(180.dp).background(headerBrush))
        Box(
            Modifier.size(160.dp).align(Alignment.TopEnd)
                .offset(x = 36.dp, y = (-42).dp)
                .clip(CircleShape).background(Color.White.copy(0.06f))
        )
        Box(
            Modifier.size(120.dp).align(Alignment.TopStart)
                .offset(x = (-28).dp, y = 34.dp)
                .clip(CircleShape).background(Color.White.copy(0.06f))
        )

        Column(Modifier.fillMaxSize()) {
            TopHeaderBar(userName = userName, onLogout = doLogout)

            // İçerik: üst köşeleri yuvarlatılmış beyaz panel
            Surface(
                shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 10.dp
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        SummaryGrid(
                            total = totalBalanceText,
                            income = incomeThisMonthText,
                            expense = expenseThisMonthText,
                            saving = savingThisMonthText
                        )
                    }
                    item { InterChatBanner(onClick = onOpenChat) }

                    item {
                        Row(
                            Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Son İşlemler", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Tümü",
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onOpenTransactions() }
                                    .padding(4.dp)
                            )
                        }
                    }
                    items(transactions) { tx -> TransactionRow(tx) }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

/* -------------------------- HEADER --------------------------- */

@Composable
private fun TopHeaderBar(
    userName: String,
    onLogout: () -> Unit
) {
    // weight KULLANMADAN genişlikleri hesapla
    val cfg = LocalConfiguration.current
    val screenW = cfg.screenWidthDp.dp
    val H_PAD = 16.dp
    val GAP = 10.dp

    // Satır 1: profil(36) + GAP + search(?) + GAP + bildirim(40)
    val searchW = screenW - (H_PAD * 2 + 36.dp + GAP + 40.dp + GAP)

    // Satır 2: sol yazılar(?) + GAP + chip(112) + GAP + çıkış(40)
    val leftW = screenW - (H_PAD * 2 + 112.dp + GAP + 40.dp + GAP)

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = H_PAD, vertical = 12.dp)
    ) {
        // Satır 1: Profil • Arama • Bildirim
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(GAP)
        ) {
            // Profil rozeti (36dp)
            Box(
                Modifier.size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Person, null, tint = Color.White)
            }

            // Arama (hesaplanan genişlik)
            Box(Modifier.width(searchW.coerceAtLeast(80.dp))) {
                SearchBarGlass()
            }

            // Bildirim cam buton (40dp)
            GlassIconButton(
                icon = Icons.Outlined.Notifications,
                contentDesc = "Bildirimler",
                onClick = { /* TODO */ },
                size = 40.dp
            )
        }

        Spacer(Modifier.height(12.dp))

        // Satır 2: Selamlama (sol) • Intertech chip + Çıkış (sağ)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(GAP)
        ) {
            Column(Modifier.width(leftW.coerceAtLeast(120.dp))) {
                Text(
                    "İyi günler, $userName",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                )
                Text(
                    "InterChat Finans Asistanına Hoşgeldiniz",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(0.85f)
                    )
                )
            }

            IntertechLogoChip() // 112 x 40
            GlassIconButton(
                icon = Icons.Outlined.Logout,   // kapı + sağa ok
                contentDesc = "Çıkış",
                onClick = onLogout,              // ✅ gerçek logout
                size = 40.dp
            )
        }
    }
}

@Composable
private fun GlassIconButton(
    icon: ImageVector,
    contentDesc: String?,
    onClick: () -> Unit,
    size: Dp = 40.dp
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(0.16f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, Color.White.copy(0.28f)),
        modifier = Modifier.size(size)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = contentDesc, tint = Color.White)
        }
    }
}

@Composable
private fun SearchBarGlass() {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(0.12f),
        border = BorderStroke(1.dp, Color.White.copy(0.28f)),
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
    ) {
        Row(Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Search, null, tint = Color.White.copy(0.75f))
            Spacer(Modifier.width(8.dp))
            Text("Ara...", color = Color.White.copy(0.9f), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun IntertechLogoChip() {
    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE7EBF0)),
        modifier = Modifier
            .width(112.dp)
            .height(40.dp)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(R.drawable.intertech),
                contentDescription = "Intertech",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}

/* ----------------------- 2x2 KART GRID ---------------------- */

@Composable
private fun SummaryGrid(
    total: String,
    income: String,
    expense: String,
    saving: String
) {
    val cfg = LocalConfiguration.current
    val screenW = cfg.screenWidthDp.dp
    val outer = 16.dp
    val gap = 12.dp
    val cardW = (screenW - outer * 2 - gap) / 2

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
            StatCard(
                "Toplam Bakiye", total,
                Color(0xFF30B3FF), Color(0xFF2D6BFF),
                { Icon(Icons.Outlined.Savings, null, tint = Color.White) },
                cardW
            )
            StatCard(
                "Bu Ay Gelir", income,
                Color(0xFF6FD6FF), Color(0xFF7B66FF),
                { Icon(Icons.Outlined.TrendingUp, null, tint = Color.White) },
                cardW
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
            StatCard(
                "Bu Ay Gider", expense,
                Color(0xFF4EC3FF), Color(0xFF6B4CFF),
                { Icon(Icons.Outlined.TrendingDown, null, tint = Color.White) },
                cardW
            )
            StatCard(
                "Tasarruf", saving,
                Color(0xFF7C5CFF), Color(0xFF7C2BFF),
                { Icon(Icons.Outlined.Savings, null, tint = Color.White) },
                cardW
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    startColor: Color,
    endColor: Color,
    leading: @Composable () -> Unit,
    width: Dp
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        modifier = Modifier.width(width).height(132.dp)
    ) {
        Box(
            Modifier
                .background(Brush.linearGradient(listOf(startColor, endColor)))
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Box(
                    Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(0.28f)),
                    contentAlignment = Alignment.Center
                ) { leading() }
                Column {
                    Text(value, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text(title, color = Color.White.copy(0.92f), fontSize = 12.sp)
                }
            }
        }
    }
}

/* ----------------------- INTERCHAT BANNER ------------------- */

@Composable
private fun InterChatBanner(onClick: () -> Unit) {
    val blue = Color(0xFF2D6BFF)
    val violet = Color(0xFF7B66FF)
    val textG = Color(0xFF6F7B91)
    val dotG = Color(0xFFA6B0C0)

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(Color(0xFFEFF5FF), Color(0xFFF4F0FF))))
        ) {
            // Arkaplan baloncukları
            Box(
                Modifier.size(64.dp).align(Alignment.TopEnd)
                    .offset(x = 12.dp, y = (-12).dp)
                    .clip(CircleShape).background(blue.copy(0.12f))
            )
            Box(
                Modifier.size(56.dp).align(Alignment.BottomStart)
                    .offset(x = (-10).dp, y = 10.dp)
                    .clip(CircleShape).background(violet.copy(0.10f))
            )

            // Yazının solunda büyük hareketli mavi nokta
            FloatingDot(
                color = blue,
                amplitude = 4f,
                periodMs = 1100,
                size = 12.dp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 54.dp, y = 14.dp)
                    .zIndex(1f)
            )
            // Başlığın sağında açık mavi küçük nokta
            FloatingDot(
                color = blue.copy(alpha = 0.22f),
                amplitude = 2f,
                periodMs = 1300,
                size = 8.dp,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = (-28).dp, y = (-6).dp)
                    .zIndex(1f)
            )

            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {

                // --- Robot kutusu (basılıyken büyür) ---
                val interaction = remember { MutableInteractionSource() }
                val pressed by interaction.collectIsPressedAsState()
                val scale by animateFloatAsState(
                    targetValue = if (pressed) 1.06f else 1f,
                    animationSpec = tween(200, easing = FastOutSlowInEasing)
                )

                Box(
                    Modifier
                        .size(60.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(BorderStroke(1.dp, Color(0xFFE6ECF5)), RoundedCornerShape(16.dp))
                        .clickable(interactionSource = interaction, indication = null) { onClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.selam),
                        contentDescription = "InterChat Robot",
                        modifier = Modifier.size(44.dp),
                        contentScale = ContentScale.Fit
                    )

                    // Robot kutusunun köşesinde küçük hareketli nokta
                    FloatingDot(
                        color = blue,
                        amplitude = 3f,
                        periodMs = 1200,
                        size = 8.dp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 6.dp, y = (-6).dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("👋 ", fontSize = 20.sp)
                        Text(
                            "InterChat ile tanışın!",
                            color = blue,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Yeni AI asistanınız sorularınızı yanıtlamaya hazır.",
                        color = textG,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(6.dp).clip(CircleShape).background(blue))
                        Spacer(Modifier.width(6.dp))
                        Text("Aktif", color = blue, style = MaterialTheme.typography.labelSmall)
                        Text("  •  ", color = dotG, style = MaterialTheme.typography.labelSmall)
                        Text("7/24 Hizmette", color = dotG, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

/* Yukarı-aşağı salınan küçük nokta */
@Composable
private fun FloatingDot(
    color: Color,
    amplitude: Float = 3f,   // +- kaç dp
    periodMs: Int = 1200,
    size: Dp = 8.dp,
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition()
    val y by infinite.animateFloat(
        initialValue = -amplitude,
        targetValue = amplitude,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = periodMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Box(
        modifier
            .offset(y = y.dp)
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}

/* ----------------------- TRANSACTION ROW -------------------- */

@Composable
private fun TransactionRow(tx: Tx) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val icon = when (tx.type) {
                TxType.Expense -> Icons.Outlined.ShoppingCart
                TxType.Income  -> Icons.Outlined.TrendingUp
                TxType.Coffee  -> Icons.Outlined.Coffee
            }
            Box(
                Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFEFF3FB)),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = Color(0xFF6B7A99)) }

            Spacer(Modifier.width(12.dp))
            Column {
                Text(tx.title, fontWeight = FontWeight.SemiBold)
                Text("${tx.category} • ${tx.time}", color = Color(0xFF8B97A8), fontSize = 12.sp)
            }
        }

        Text(
            text = (if (tx.type == TxType.Income) "+" else "−") + tx.amount,
            color = if (tx.type == TxType.Income) Color(0xFF00B060) else Color(0xFFFF4D4F),
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )
    }
}

/* -------------------------- MOCKS --------------------------- */

data class Tx(
    val title: String,
    val category: String,
    val time: String,
    val amount: String,
    val type: TxType
)

enum class TxType { Income, Expense, Coffee }

fun demoTransactions(): List<Tx> = listOf(
    Tx("Migros Market", "Market", "14:30", "127,5", TxType.Expense),
    Tx("Maaş Ödemesi", "Maaş", "09:15", "2.500", TxType.Income),
    Tx("Metro Kartı", "Ulaşım", "08:45", "45", TxType.Expense),
    Tx("Netflix Abonelik", "Eğlence", "12:00", "89,99", TxType.Expense),
    Tx("Freelance Proje", "Ek Gelir", "16:20", "150", TxType.Income),
    Tx("Kahve", "Eğlence", "10:15", "38", TxType.Coffee),
)
