package com.example.interchat.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.interchat.ui.screens.BalanceScreen
import com.example.interchat.ui.screens.BillPaymentScreen
import com.example.interchat.ui.screens.CardInfoScreen
import com.example.interchat.ui.screens.LoginScreen
import com.example.interchat.ui.screens.PersonalInfoScreen
import com.example.interchat.ui.screens.RecentOpsScreen
import com.example.interchat.ui.screens.ScheduledPaymentsScreen
import com.example.interchat.ui.screens.TopUpScreen
import com.example.interchat.ui.screens.TransactionHistoryScreen
import com.example.interchat.ui.screens.TransactionsHomeScreen
import com.example.interchat.ui.screens.TransactionsScreen
import com.example.interchat.ui.screens.TransferScreen

@Composable
fun AppRoot() {
    val nav = rememberNavController()

    NavHost(
        navController = nav,
        startDestination = Routes.LOGIN
    ) {
        // 1) LOGIN — projenizdeki imzaya göre: onLogin(email, password), diğerleri opsiyonel
        composable(Routes.LOGIN) {
            LoginScreen(
                onLogin = { email, password ->
                    nav.navigate(Routes.TABS_ACCOUNTS) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onSignUpClick = { /* opsiyonel */ },
                onForgotClick = { /* opsiyonel */ }
            )
        }

        // 2) 4'lü alt bar için shell entry point'ler
        composable(Routes.TABS_CHAT)         { TabShell(startRoute = Routes.TABS_CHAT) }
        composable(Routes.TABS_ACCOUNTS)     { TabShell(startRoute = Routes.TABS_ACCOUNTS) }
        composable(Routes.TABS_TRANSACTIONS) { TabShell(startRoute = Routes.TABS_TRANSACTIONS) }
        composable(Routes.TABS_FAQ)          { TabShell(startRoute = Routes.TABS_FAQ) }
    }
}

@Composable
private fun TabShell(startRoute: String) {
    val tabsNav = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val backstack by tabsNav.currentBackStackEntryAsState()
                val current = backstack?.destination?.route.orEmpty()
                listOf(
                    Routes.TABS_CHAT         to "Chat",
                    Routes.TABS_ACCOUNTS     to "Hesaplar",
                    Routes.TABS_TRANSACTIONS to "İşlemler",
                    Routes.TABS_FAQ          to "SSS"
                ).forEach { (route, label) ->
                    NavigationBarItem(
                        selected = current.startsWith(route),
                        onClick = {
                            tabsNav.navigate(route) {
                                popUpTo(tabsNav.graph.startDestinationId) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        icon = { },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { pad ->
        NavHost(
            navController = tabsNav,
            startDestination = startRoute,
            modifier = Modifier.padding(pad)
        ) {
            // TAB 1: Chat – HomeScreen yoksa basit placeholder metin
            composable(Routes.TABS_CHAT) {
                Text("Chat", style = MaterialTheme.typography.headlineSmall)
            }

            // TAB 2: Hesaplar – menü + alt sayfalar
            addAccountsGraph(tabsNav)

            // TAB 3: İşlemler – detaylı alt grafik
            addTransactionsGraph(tabsNav)

            // TAB 4: SSS – placeholder
            composable(Routes.TABS_FAQ) {
                Text("SSS", style = MaterialTheme.typography.headlineSmall)
            }
        }
    }
}

/* ---------- Hesaplar Sekmesi Grafiği ---------- */

private fun NavGraphBuilder.addAccountsGraph(tabsNav: NavHostController) {
    // Hesaplar ana sayfası: kişisel menü (4 buton)
    composable(Routes.TABS_ACCOUNTS) {
        PersonalInfoScreen(
            onOpenBalance = { tabsNav.navigate(Routes.BALANCE) },
            onOpenTx      = { tabsNav.navigate(Routes.TX) },
            onOpenCard    = { tabsNav.navigate(Routes.CARD) },
            onOpenRecent  = { tabsNav.navigate(Routes.RECENT) }
        )
    }
    // Alt sayfalar (geri = navigateUp)
    composable(Routes.BALANCE) { BalanceScreen(onBack = { tabsNav.navigateUp() }) }
    composable(Routes.TX)      { TransactionsScreen(onBack = { tabsNav.navigateUp() }) }
    composable(Routes.CARD)    { CardInfoScreen(onBack = { tabsNav.navigateUp() }) }
    composable(Routes.RECENT)  { RecentOpsScreen(onBack = { tabsNav.navigateUp() }) }
}

/* ---------- İşlemler Sekmesi Grafiği ---------- */

private fun NavGraphBuilder.addTransactionsGraph(tabsNav: NavHostController) {
    // İşlemler ana menü
    composable(Routes.TABS_TRANSACTIONS) {
        TransactionsHomeScreen(
            onTransfer  = { tabsNav.navigate(Routes.TX_TRANSFER) },
            onBill      = { tabsNav.navigate(Routes.TX_BILL) },
            onTopUp     = { tabsNav.navigate(Routes.TX_TOPUP) },
            onScheduled = { tabsNav.navigate(Routes.TX_SCHEDULED) },
            onHistory   = { tabsNav.navigate(Routes.TX_HISTORY) }
        )
    }
    // Alt sayfalar (geri = navigateUp)
    composable(Routes.TX_TRANSFER)  { TransferScreen(onBack = { tabsNav.navigateUp() }) }
    composable(Routes.TX_BILL)      { BillPaymentScreen(onBack = { tabsNav.navigateUp() }) }
    composable(Routes.TX_TOPUP)     { TopUpScreen(onBack = { tabsNav.navigateUp() }) }
    composable(Routes.TX_SCHEDULED) { ScheduledPaymentsScreen(onBack = { tabsNav.navigateUp() }) }
    composable(Routes.TX_HISTORY)   { TransactionHistoryScreen(onBack = { tabsNav.navigateUp() }) }
}
