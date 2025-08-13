package com.example.interchat.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.interchat.R

@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    onSignUpClick: () -> Unit,
    onForgotClick: () -> Unit
) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF7C3AED), // mor
            Color(0xFF4F46E5), // indigo
            Color(0xFF06B6D4)  // camgöbeği
        )
    )

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    val canLogin = email.isNotBlank() && password.length >= 6

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo_interchat),
            contentDescription = "InterChat Logo",
            modifier = Modifier.size(300.dp)
        )
        Spacer(Modifier.height(12.dp))

        // Başlık
       /* Text(
            text = "InterChat",
            style = TextStyle(
                fontSize = 70.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF6146E5)
            ),
            textAlign = TextAlign.Center
        )
       */ Spacer(Modifier.height(4.dp))
        Text(
            text = "Hoş geldin! Devam etmek için giriş yap.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(Modifier.height(24.dp))

        // E‑posta
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            label = { Text("E‑posta") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        // Şifre
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            label = { Text("Şifre") },
            singleLine = true,
            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { if (canLogin) onLogin(email.trim(), password) }
            ),
            trailingIcon = {
                Text(
                    text = if (showPass) "Gizle" else "Göster",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .clickable { showPass = !showPass }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        // Şifremi unuttum
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Text(
                text = "Şifremi unuttum?",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onForgotClick() }
            )
        }
        Spacer(Modifier.height(20.dp))

        // Giriş butonu (gradient)
        GradientButton(
            text = "Giriş Yap",
            enabled = canLogin,
            gradient = gradient,
            onClick = { onLogin(email.trim(), password) }
        )

        Spacer(Modifier.height(16.dp))

        // Kayıt ol yönlendirme
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Hesabın yok mu ? ")
            Text(
                text = "Kayıt ol",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onSignUpClick() }
            )
        }

        Spacer(Modifier.height(70.dp))

        // (Opsiyonel) Google ile devam et
      /*  OutlinedButton(
            onClick = { /* TODO: Google Sign-In */ },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Google ile devam et")
      */  }
    }


@Composable
fun GradientButton(
    text: String,
    enabled: Boolean,
    gradient: Brush,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(
                brush = if (enabled) gradient
                else Brush.horizontalGradient(listOf(Color.LightGray, Color.LightGray)),
                shape = shape
            )
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(
            onLogin = { _, _ -> },
            onSignUpClick = {},
            onForgotClick = {}
        )
    }
}


