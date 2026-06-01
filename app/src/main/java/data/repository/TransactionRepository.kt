package com.example.kasichka.data.repository

import com.example.kasichka.data.local.TransactionEntity
import com.example.kasichka.data.local.TransactionLocalDataSource

class TransactionRepository(
    private val localDataSource: TransactionLocalDataSource,
) {

    fun getAllTransactions(): List<TransactionEntity> {
        return localDataSource.getAllTransactions()
    }

    fun getTransactionById(id: Int): TransactionEntity? {
        return localDataSource.getTransactionById(id)
    }

    fun insertTransaction(transaction: TransactionEntity): Long {
        return localDataSource.insertTransaction(transaction)
    }

    fun updateTransaction(transaction: TransactionEntity): Int {
        return localDataSource.updateTransaction(transaction)
    }

    fun deleteTransaction(transaction: TransactionEntity): Int {
        return localDataSource.deleteTransaction(transaction)
    }

    fun getTotalByType(type: String): Double {
        return localDataSource.getTotalByType(type)
    }
}