package com.example.interchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.interchat.ui.screens.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(
                        onLogin = { email, pass ->
                            // TODO: Burada Firebase Authentication ile giriş yapılacak
                        },
                        onSignUpClick = {
                            // TODO: Kayıt ol sayfasına yönlendirme
                        },
                        onForgotClick = {
                            // TODO: Şifre sıfırlama ekranına yönlendirme
                        }
                    )
                }
            }
        }
    }
}
