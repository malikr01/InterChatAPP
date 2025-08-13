package com.example.interchat.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.HelpCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.SmartToy // Eğer çözülmezse: değiştir -> outlined.Android
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.interchat.ui.screens.*

private data class BottomItem(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNav() {
    val nav = rememberNavController()

    // Sıra: 2 solda (Home, Accounts) + ORTA (ChatAI) + 2 sağda (Transactions, FAQ)
    val bottomItems = listOf(
        BottomItem(Routes.HOME, "Ana Sayfa") { Icon(Icons.Outlined.Home, null) },
        BottomItem(Routes.ACCOUNTS, "Hesaplar") { Icon(Icons.Outlined.AccountCircle, null) },
        BottomItem(Routes.CHAT_AI, "ChatAI") { Icon(Icons.Outlined.SmartToy, null) }, // ikon hata verirse SmartToy importunu Android ile değiştir
        BottomItem(Routes.TRANSACTIONS_HOME, "İşlemler") { Icon(Icons.Outlined.ListAlt, null) },
        BottomItem(Routes.FAQ, "SSS") { Icon(Icons.Outlined.HelpCenter, null) },
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
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(inner)
        ) {
            /* ----- AUTH ----- */
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLogin = { _, _ ->
                        nav.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onSignUpClick = { /* kayıt ekranına yönlendirme */ },
                    onForgotClick = { /* şifre sıfırlama ekranına yönlendirme */ }
                )
            }

            /* ----- TABS ----- */
            composable(Routes.HOME) { HomeScreen() }

            composable(Routes.ACCOUNTS) {
                AccountsScreen(
                    onAccountClick = { id -> nav.navigate(Routes.accountDetail(id)) },
                    onOpenCardDetail = { nav.navigate(Routes.CARD_DETAIL) }
                )
            }

            // ORTA: ChatAI
            composable(Routes.CHAT_AI) { ChatAIScreen() }

            // İşlemler ana menüsü
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

            /* ----- PERSONAL INFO (opsiyonel) ----- */
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

            /* ----- ACCOUNT / CARD DETAILS ----- */
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

            /* ----- TRANSACTIONS SUB PAGES ----- */
            composable(Routes.TX_TRANSFER)  { TransferScreen(onBack = { nav.popBackStack() }) }
            composable(Routes.TX_BILL)      { BillPaymentScreen(onBack = { nav.popBackStack() }) }
            composable(Routes.TX_TOPUP)     { TopUpScreen(onBack = { nav.popBackStack() }) }
            composable(Routes.TX_SCHEDULED) { ScheduledPaymentsScreen(onBack = { nav.popBackStack() }) }
            composable(Routes.TX_HISTORY)   { TransactionHistoryScreen(onBack = { nav.popBackStack() }) }

            /* ----- TRANSACTIONS > HESAPLAMALAR ----- */
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
