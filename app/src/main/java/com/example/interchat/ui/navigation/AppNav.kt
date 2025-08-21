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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.interchat.data.session.UserSession
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
    val nav   = rememberNavController()
    val scope = rememberCoroutineScope()
    val ctx   = LocalContext.current

    val store    = remember { com.example.interchat.data.CredentialsStore(ctx) }
    val authRepo = remember { com.example.interchat.data.MockAuthRepository(store) }
    val loginUC  = remember { LoginWithTcUseCase(authRepo) }

    val bottomItems = listOf(
        BottomItem(Routes.HOME, "Ana Sayfa")            { Icon(Icons.Outlined.Home, null) },
        BottomItem(Routes.ACCOUNTS, "Hesaplar")         { Icon(Icons.Outlined.AccountCircle, null) },
        BottomItem(Routes.CHAT_AI, "ChatAI")            { Icon(Icons.Outlined.SmartToy, null) },
        BottomItem(Routes.TRANSACTIONS_HOME, "İşlemler"){ Icon(Icons.Outlined.ListAlt, null) },
        BottomItem(Routes.FAQ, "SSS")                   { Icon(Icons.Outlined.HelpCenter, null) }
    )
    val bottomRoutes = bottomItems.map { it.route }

    Scaffold(
        bottomBar = {
            val current = nav.currentBackStackEntryAsState().value?.destination?.route
            if (current in bottomRoutes) {
                NavigationBar {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            selected = current == item.route,
                            onClick = {
                                // ChatAI sekmesine basınca önce splash göster
                                val target = if (item.route == Routes.CHAT_AI) Routes.CHAT_SPLASH else item.route
                                nav.navigate(target) {
                                    popUpTo(nav.graph.findStartDestination().id) { saveState = true }
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
            startDestination = "splash",
            modifier = Modifier.padding(inner)
        ) {
            // Uygulama açılışındaki splash
            composable("splash") {
                SplashScreen {
                    nav.navigate(Routes.LOGIN) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            }

            composable(Routes.LOGIN) {
                LoginScreen(
                    onLogin = { tc, pass, _ ->
                        scope.launch {
                            when (val res = loginUC(tc, pass)) {
                                is R.Ok -> {
                                    UserSession.setUserId("u1") // mock kullanıcı
                                    nav.navigate(Routes.HOME) {
                                        popUpTo(Routes.LOGIN) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                                is R.Err -> Toast.makeText(ctx, res.msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onSignUpClick = { nav.navigate(Routes.REGISTER) },
                    onForgotClick = { nav.navigate(Routes.FORGOT) },
                    prefillTc = null,
                    prefillPassword = null
                )
            }

            // Home: Chat'e giderken CHAT_SPLASH'a yönlendir
            composable(Routes.HOME) {
                HomeScreen(
                    onOpenChat = { nav.navigate(Routes.CHAT_SPLASH) },
                    onOpenTransactions = { nav.navigate(Routes.TRANSACTIONS_HOME) },
                    onLoggedOut = {
                        nav.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Routes.ACCOUNTS) {
                AccountsScreen(
                    onAccountClick   = { id -> nav.navigate(Routes.accountDetail(id)) },
                    onOpenCardDetail = { nav.navigate(Routes.CARD_DETAIL) }
                )
            }

            // Chat'e özel splash sayfası
            composable(Routes.CHAT_SPLASH) {
                SplashScreen {
                    nav.navigate(Routes.CHAT_AI) {
                        popUpTo(Routes.CHAT_SPLASH) { inclusive = true } // splash'i temizle
                        launchSingleTop = true
                    }
                }
            }

            composable(Routes.CHAT_AI) { ChatAIScreen() }

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

            composable(Routes.REGISTER) {
                RegisterScreen(
                    onBack = { nav.popBackStack() },
                    onRegistered = { _, _ ->
                        Toast.makeText(ctx, "Kayıt tamamlandı", Toast.LENGTH_SHORT).show()
                        nav.popBackStack()
                    }
                )
            }

            composable(Routes.FORGOT) {
                ForgotPasswordScreen(
                    onBack = { nav.popBackStack() },
                    onDone = {
                        Toast.makeText(ctx, "Mock sıfırlama", Toast.LENGTH_SHORT).show()
                        nav.popBackStack()
                    }
                )
            }

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

            composable(
                route = Routes.ACCOUNT_DETAIL_ROUTE,
                arguments = listOf(navArgument(Routes.ARG_ID) { type = NavType.StringType })
            ) { backStack ->
                val id = backStack.arguments?.getString(Routes.ARG_ID).orEmpty()
                AccountDetailScreen(accountId = id, onBack = { nav.popBackStack() })
            }
            composable(Routes.CARD_DETAIL) { CardDetailScreen(onBack = { nav.popBackStack() }) }

            composable(Routes.TX_TRANSFER)  { TransferScreen(onBack = { nav.popBackStack() }) }
            composable(Routes.TX_BILL)      { BillPaymentScreen(onBack = { nav.popBackStack() }) }
            composable(Routes.TX_TOPUP)     { TopUpScreen(onBack = { nav.popBackStack() }) }
            composable(Routes.TX_SCHEDULED) { ScheduledPaymentsScreen(onBack = { nav.popBackStack() }) }
            composable(Routes.TX_HISTORY)   { TransactionHistoryScreen(onBack = { nav.popBackStack() }) }

            composable(Routes.TX_CALCULATORS) {
                Scaffold(
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
                        FinancialCalculationsScreen(
                            onLoanCalcClick = { nav.navigate(Routes.CALC_LOAN) },
                            onPlanClick     = { nav.navigate(Routes.CALC_PLANS) },
                            onInvestClick   = { nav.navigate(Routes.CALC_INV) },
                            onFxClick       = { nav.navigate(Routes.CALC_FX) }
                        )
                    }
                }
            }

            composable(Routes.CALC_LOAN)  { KrediFaizHesaplamaScreen(onBack = { nav.popBackStack() }) }
            composable(Routes.CALC_PLANS) { OdemePlanlariScreen(onBack = { nav.popBackStack() }) }
            composable(Routes.CALC_INV)   { YalinYatirimScreen(onBack = { nav.popBackStack() }) }
            composable(Routes.CALC_FX)    { DovizHesaplamaScreen(onBack = { nav.popBackStack() }) }
        }
    }
}
