package com.example.interchat.ui.common

/**
 * Ekran genelinde tek seferlik UI olayları (snackbar, yönlendirme vb.).
 */
sealed class UiEvent {
    data class ShowSnackbar(val message: String, val actionLabel: String? = null) : UiEvent()
}
