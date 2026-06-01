package com.example.kasichka.util

import com.example.kasichka.data.local.TransactionEntity
import java.util.Calendar

object TransactionCalculator {

    fun calculateTotalByType(
        transactions: List<TransactionEntity>,
        type: String,
    ): Double {
        return transactions
            .filter { it.type == type }
            .sumOf { it.amount }
    }

    fun calculateBalance(
        income: Double,
        expense: Double,
    ): Double {
        return income - expense
    }

    fun getExpenseTotalsByCategory(
        transactions: List<TransactionEntity>,
        expenseType: String = "EXPENSE",
    ): Map<String, Double> {
        return transactions
            .filter { it.type == expenseType }
            .groupBy { it.category }
            .mapValues { entry ->
                entry.value.sumOf { transaction -> transaction.amount }
            }
    }

    fun filterByMonth(
        transactions: List<TransactionEntity>,
        year: Int,
        month: Int,
    ): List<TransactionEntity> {
        return transactions.filter { transaction ->
            isInMonth(
                timestamp = transaction.date,
                year = year,
                month = month,
            )
        }
    }

    fun isInMonth(
        timestamp: Long,
        year: Int,
        month: Int,
    ): Boolean {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
        }

        return calendar.get(Calendar.YEAR) == year &&
                calendar.get(Calendar.MONTH) == month
    }
}

