package com.example.kasichka.util

object TransactionValidator {

    fun parseAmount(input: String): Double? {
        val normalizedInput = input
            .trim()
            .replace(",", ".")

        val amount = normalizedInput.toDoubleOrNull()

        return if (amount != null && amount > 0.0) {
            amount
        } else {
            null
        }
    }

    fun isRequiredTextValid(input: String): Boolean {
        return input.trim().isNotEmpty()
    }
}

