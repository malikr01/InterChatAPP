package com.example.interchat.domain

sealed interface R<out T> {
    data class Ok<T>(val data: T) : R<T>
    data class Err(val msg: String, val cause: Throwable? = null) : R<Nothing>
}

fun String.isValidTc(): Boolean =
    length == 11 && all { it.isDigit() } && first() != '0'
