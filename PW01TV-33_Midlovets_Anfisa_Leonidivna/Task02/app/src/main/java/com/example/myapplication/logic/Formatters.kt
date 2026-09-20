package com.example.myapplication.logic

import java.util.Locale

/** Форматує число для відображення: розділювачі тисяч і задана кількість знаків після коми. */
fun formatNumber(value: Double, decimals: Int = 0): String =
    String.format(Locale.getDefault(), "%,.${decimals}f", value)
