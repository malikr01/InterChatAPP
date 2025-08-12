package com.example.interchat

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.interchat.ui.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val nav = rememberNavController()

            MaterialTheme {
                NavHost(navController = nav, startDestination = "login") {

                    composable("login") {
                        val ctx = LocalContext.current
                        LoginScreen(
                            onLogin = { email, pass ->
                                if (email.trim().equals("demo@interchat.app", true) && pass == "123456") {
                                    nav.navigate("home") { popUpTo("login") { inclusive = true } }
                                } else {
                                    Toast.makeText(ctx, "Hatalı e‑posta/şifre", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onSignUpClick = {},
                            onForgotClick = {}
                        )
                    }

                    composable("home") {
                        HomeRootScreen( // ← yeni adıyla çağırıyoruz
                            onOpenAccountDetail = { id -> nav.navigate("account/$id") },
                            onOpenCardDetail = { nav.navigate("card") }
                        )
                    }

                    composable(
                        route = "account/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.StringType })
                    ) { backStack ->
                        val id = backStack.arguments?.getString("id") ?: ""
                        AccountDetailScreen(accountId = id, onBack = { nav.popBackStack() })
                    }

                    composable("card") {
                        CardDetailScreen(onBack = { nav.popBackStack() })
                    }
                }
            }
        }
    }
}
