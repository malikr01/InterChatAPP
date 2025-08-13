// app/src/main/java/com/example/interchat/ui/screens/LoginScreen.kt
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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.interchat.R
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun LoginScreen(
    onLogin: (String, String, Boolean) -> Unit,   // <- rememberMe eklendi
    onSignUpClick: () -> Unit,
    onForgotClick: () -> Unit,
    prefillTc: String? = null,
    prefillPassword: String? = null
) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF7C3AED), // mor
            Color(0xFF4F46E5), // indigo
            Color(0xFF06B6D4)  // camgöbeği
        )
    )

    var tc by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    var rememberMe by rememberSaveable { mutableStateOf(false) }   // <- yeni

    // DataStore’dan gönderilen ön değerleri doldur
    LaunchedEffect(prefillTc, prefillPassword) {
        if (!prefillTc.isNullOrBlank()) tc = prefillTc
        if (!prefillPassword.isNullOrBlank()) password = prefillPassword
        // eğer prefill geldiyse checkbox’ı işaretli başlatmak mantıklı
        if (!prefillTc.isNullOrBlank() || !prefillPassword.isNullOrBlank()) {
            rememberMe = true
        }
    }

    val canLogin = tc.length == 11 && password.length >= 4

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

        Text(
            text = "Hoş geldin! Devam etmek için giriş yap.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(Modifier.height(24.dp))

        // TC Kimlik No
        OutlinedTextField(
            value = tc,
            onValueChange = { input -> tc = input.filter { it.isDigit() }.take(11) },
            leadingIcon = { Icon(Icons.Filled.Badge, contentDescription = null) },
            label = { Text("TC Kimlik No") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        // Şifre
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
            label = { Text("Şifre") },
            singleLine = true,
            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { if (canLogin) onLogin(tc.trim(), password, rememberMe) }
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

        // Beni hatırla
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it })
            Text("Beni hatırla")
        }

        // Şifremi unuttum
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Text(
                text = "Şifremi unuttum?",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onForgotClick() }
            )
        }
        Spacer(Modifier.height(16.dp))

        // Giriş Yap (gradient)
        GradientButton(
            text = "Giriş Yap",
            enabled = canLogin,
            gradient = gradient,
            onClick = { onLogin(tc.trim(), password, rememberMe) }   // <- rememberMe gönder
        )

        Spacer(Modifier.height(16.dp))

        // Kayıt ol yönlendirme
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Hesabın yok mu? ")
            Text(
                text = "Kayıt ol",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onSignUpClick() }
            )
        }

        Spacer(Modifier.height(70.dp))
    }
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
