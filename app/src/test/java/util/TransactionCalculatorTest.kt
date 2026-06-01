package com.example.kasichka.util

import com.example.kasichka.data.local.TransactionEntity
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class TransactionCalculatorTest {

    @Test
    fun calculateTotalByType_returnsCorrectIncomeTotal() {
        val transactions = listOf(
            createTransaction(amount = 1000.0, type = "INCOME"),
            createTransaction(amount = 200.0, type = "INCOME"),
            createTransaction(amount = 50.0, type = "EXPENSE"),
        )

        val result = TransactionCalculator.calculateTotalByType(
            transactions = transactions,
            type = "INCOME",
        )

        assertEquals(1200.0, result, 0.001)
    }

    @Test
    fun calculateTotalByType_returnsCorrectExpenseTotal() {
        val transactions = listOf(
            createTransaction(amount = 1000.0, type = "INCOME"),
            createTransaction(amount = 80.0, type = "EXPENSE"),
            createTransaction(amount = 20.0, type = "EXPENSE"),
        )

        val result = TransactionCalculator.calculateTotalByType(
            transactions = transactions,
            type = "EXPENSE",
        )

        assertEquals(100.0, result, 0.001)
    }

    @Test
    fun calculateBalance_returnsIncomeMinusExpense() {
        val result = TransactionCalculator.calculateBalance(
            income = 1000.0,
            expense = 250.0,
        )

        assertEquals(750.0, result, 0.001)
    }

    @Test
    fun getExpenseTotalsByCategory_groupsExpensesCorrectly() {
        val transactions = listOf(
            createTransaction(amount = 50.0, type = "EXPENSE", category = "Храна"),
            createTransaction(amount = 30.0, type = "EXPENSE", category = "Храна"),
            createTransaction(amount = 20.0, type = "EXPENSE", category = "Транспорт"),
            createTransaction(amount = 1000.0, type = "INCOME", category = "Заплата"),
        )

        val result = TransactionCalculator.getExpenseTotalsByCategory(transactions)

        assertEquals(80.0, result["Храна"] ?: 0.0, 0.001)
        assertEquals(20.0, result["Транспорт"] ?: 0.0, 0.001)
        assertEquals(null, result["Заплата"])
    }

    @Test
    fun filterByMonth_returnsOnlyTransactionsFromSelectedMonth() {
        val juneDate = createDate(
            year = 2026,
            month = Calendar.JUNE,
            day = 1,
        )

        val julyDate = createDate(
            year = 2026,
            month = Calendar.JULY,
            day = 1,
        )

        val transactions = listOf(
            createTransaction(amount = 50.0, type = "EXPENSE", date = juneDate),
            createTransaction(amount = 20.0, type = "EXPENSE", date = julyDate),
        )

        val result = TransactionCalculator.filterByMonth(
            transactions = transactions,
            year = 2026,
            month = Calendar.JUNE,
        )

        assertEquals(1, result.size)
        assertEquals(50.0, result.first().amount, 0.001)
    }

    private fun createTransaction(
        amount: Double,
        type: String,
        category: String = "Друго",
        date: Long = System.currentTimeMillis(),
    ): TransactionEntity {
        return TransactionEntity(
            amount = amount,
            type = type,
            category = category,
            description = "Test transaction",
            date = date,
        )
    }

    private fun createDate(
        year: Int,
        month: Int,
        day: Int,
    ): Long {
        return Calendar.getInstance().apply {
            set(year, month, day, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
