package com.example.fitbattleandroid.utils.extensions

import com.google.android.gms.location.Priority

fun Int.toPriorityString(): String =
    when (this) {
        Priority.PRIORITY_BALANCED_POWER_ACCURACY -> "PRIORITY_BALANCED_POWER_ACCURACY"
        Priority.PRIORITY_HIGH_ACCURACY -> "PRIORITY_HIGH_ACCURACY"
        Priority.PRIORITY_LOW_POWER -> "PRIORITY_LOW_POWER"
        else -> "UNKNOWN_PRIORITY"
    }
