// app/src/main/java/com/example/interchat/ui/navigation/AppNav.kt
package com.example.interchat.ui.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.HelpCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.interchat.domain.LoginWithTcUseCase
import com.example.interchat.domain.R
import com.example.interchat.ui.screens.*
import kotlinx.coroutines.launch

private data class BottomItem(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNav() {
    val nav = rememberNavController()
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    // DataStore + Repo
    val store    = remember { com.example.interchat.data.CredentialsStore(ctx) }
    val authRepo = remember { com.example.interchat.data.MockAuthRepository(store) }
    val loginUC  = remember { LoginWithTcUseCase(authRepo) }

    // Auto-fill / auto-login → REMEMBER alanı
    val remembered by store.remembered.collectAsState(initial = null to null)
    val prefillTc = remembered.first
    val prefillPass = remembered.second

    // Bottom bar
    val bottomItems = listOf(
        BottomItem(Routes.HOME, "Ana Sayfa") { Icon(Icons.Outlined.Home, null) },
        BottomItem(Routes.ACCOUNTS, "Hesaplar") { Icon(Icons.Outlined.AccountCircle, null) },
        BottomItem(Routes.CHAT_AI, "ChatAI") { Icon(Icons.Outlined.SmartToy, null) },
        BottomItem(Routes.TRANSACTIONS_HOME, "İşlemler") { Icon(Icons.Outlined.ListAlt, null) },
        BottomItem(Routes.FAQ, "SSS") { Icon(Icons.Outlined.HelpCenter, null) }
    )
    val bottomRoutes = bottomItems.map { it.route }

    Scaffold(
        bottomBar = {
            val backStackEntry by nav.currentBackStackEntryAsState()
            val current = backStackEntry?.destination?.route
            if (current in bottomRoutes) {
                NavigationBar {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            selected = current == item.route,
                            onClick = {
                                nav.navigate(item.route) {
                                    popUpTo(nav.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = item.icon,
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { inner ->
        NavHost(
            navController = nav,
            startDestination = if (prefillTc != null && prefillPass != null) Routes.HOME else Routes.LOGIN,
            modifier = Modifier.padding(inner)
        ) {
            /* ---------- AUTH ---------- */
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLogin = { tc, pass, remember ->
                        scope.launch {
                            when (val res = loginUC(tc, pass)) {
                                is R.Ok -> {
                                    if (remember) store.saveRemember(tc, pass) else store.clearRemember()
                                    nav.navigate(Routes.HOME) {
                                        popUpTo(Routes.LOGIN) { inclusive = true }
                                    }
                                }
                                is R.Err -> Toast.makeText(ctx, res.msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onSignUpClick = { nav.navigate(Routes.REGISTER) },
                    onForgotClick = { nav.navigate(Routes.FORGOT) },
                    prefillTc = prefillTc,
                    prefillPassword = prefillPass
                )
            }

            /* ---------- TABS ---------- */
            composable(Routes.HOME) {
                HomeScreen(
                    onLogout = {
                        scope.launch {
                            store.clearRemember() // sadece remember temizlensin
                            nav.navigate(Routes.LOGIN) {
                                popUpTo(Routes.HOME) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(Routes.ACCOUNTS) {
                AccountsScreen(
                    onAccountClick = { id -> nav.navigate(Routes.accountDetail(id)) },
                    onOpenCardDetail = { nav.navigate(Routes.CARD_DETAIL) }
                )
            }

            // 🔹 ChatAI: canlı desteğe yönlendirme burada
            composable(Routes.CHAT_AI) {
                ChatAIScreen(
                    onConnectLiveSupport = { nav.navigate(Routes.FAQ) } // istersen SUPPORT rotasına götür
                )
            }

            composable(Routes.TRANSACTIONS_HOME) {
                TransactionsHomeScreen(
                    onTransfer     = { nav.navigate(Routes.TX_TRANSFER) },
                    onBill         = { nav.navigate(Routes.TX_BILL) },
                    onTopUp        = { nav.navigate(Routes.TX_TOPUP) },
                    onScheduled    = { nav.navigate(Routes.TX_SCHEDULED) },
                    onHistory      = { nav.navigate(Routes.TX_HISTORY) },
                    onCalculations = { nav.navigate(Routes.TX_CALCULATORS) }
                )
            }

            composable(Routes.FAQ) { FaqScreen() }

            /* ---------- AUTH SUBPAGES ---------- */
            composable(Routes.REGISTER) {
                RegisterScreen(
                    onBack = { nav.popBackStack() },
                    onRegistered = { tc, pass ->
                        scope.launch {
                            when (val r = authRepo.register(tc, pass)) {
                                is R.Ok -> {
                                    Toast.makeText(ctx, "Kayıt tamamlandı", Toast.LENGTH_SHORT).show()
                                    nav.popBackStack()
                                }
                                is R.Err -> Toast.makeText(ctx, r.msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }

            composable(Routes.FORGOT) {
                ForgotPasswordScreen(
                    onBack = { nav.popBackStack() },
                    onDone = {
                        scope.launch {
                            store.clearRemember()
                            Toast.makeText(ctx, "Mock temizlendi", Toast.LENGTH_SHORT).show()
                            nav.popBackStack()
                        }
                    }
                )
            }

            /* ---------- PERSONAL INFO ---------- */
            composable(Routes.PERSONAL_INFO) {
                PersonalInfoScreen(
                    onOpenBalance = { nav.navigate(Routes.BALANCE) },
                    onOpenTx      = { nav.navigate(Routes.TX_LIST) },
                    onOpenCard    = { nav.navigate(Routes.CARD_INFO) },
                    onOpenRecent  = { nav.navigate(Routes.RECENT_OPS) }
                )
            }
            composable(Routes.BALANCE)    { BalanceScreen(onBack = { nav.popBackStack() }) }
            composable(Routes.TX_LIST)    { TransactionsScreen(onBack = { nav.popBackStack() }) }
            composable(Routes.CARD_INFO)  { CardInfoScreen(onBack = { nav.popBackStack() }) }
            composable(Routes.RECENT_OPS) { RecentOpsScreen(onBack = { nav.popBackStack() }) }

            /* ---------- ACCOUNT / CARD DETAILS ---------- */
            composable(
                route = Routes.ACCOUNT_DETAIL_ROUTE,
                arguments = listOf(navArgument(Routes.ARG_ID) { type = NavType.StringType })
            ) { backStack ->
                val id = backStack.arguments?.getString(Routes.ARG_ID).orEmpty()
                AccountDetailScreen(accountId = id, onBack = { nav.popBackStack() })
            }
            composable(Routes.CARD_DETAIL) {
                CardDetailScreen(onBack = { nav.popBackStack() })
            }

            /* ---------- TRANSACTIONS > CALCULATORS ---------- */
            composable(Routes.TX_CALCULATORS) {
                androidx.compose.material3.Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Hesaplamalar") },
                            navigationIcon = {
                                IconButton(onClick = { nav.popBackStack() }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                                }
                            }
                        )
                    }
                ) { pad ->
                    Box(Modifier.padding(pad)) {
                        FinancialCalculationsScreen()
                    }
                }
            }
        }
    }
}
