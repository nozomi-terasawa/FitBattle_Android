package com.example.fitbattleandroid.utils.extensions

fun String.isEmailValid(): Boolean {
    val emailRegex = "^[A-Za-z0-9._+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

    return emailRegex.matches(this)
}
