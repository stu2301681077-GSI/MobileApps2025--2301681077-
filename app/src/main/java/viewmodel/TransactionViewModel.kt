package com.example.kasichka.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kasichka.data.local.KasichkaDatabaseHelper
import com.example.kasichka.data.local.TransactionEntity
import com.example.kasichka.data.local.TransactionLocalDataSource
import com.example.kasichka.data.repository.TransactionRepository
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    private val _allTransactions = MutableLiveData<List<TransactionEntity>>(emptyList())
    val allTransactions: LiveData<List<TransactionEntity>> = _allTransactions

    private val _totalIncome = MutableLiveData(0.0)
    val totalIncome: LiveData<Double> = _totalIncome

    private val _totalExpense = MutableLiveData(0.0)
    val totalExpense: LiveData<Double> = _totalExpense

    private val _balance = MutableLiveData(0.0)
    val balance: LiveData<Double> = _balance

    private val _selectedTransaction = MutableLiveData<TransactionEntity?>()
    val selectedTransaction: LiveData<TransactionEntity?> = _selectedTransaction

    init {
        val databaseHelper = KasichkaDatabaseHelper(application)
        val localDataSource = TransactionLocalDataSource(databaseHelper)
        repository = TransactionRepository(localDataSource)

        loadTransactions()
        loadTotals()
    }

    fun loadTransactions() {
        executor.execute {
            val transactions = repository.getAllTransactions()
            _allTransactions.postValue(transactions)
        }
    }

    fun loadTotals() {
        executor.execute {
            val income = repository.getTotalByType(TYPE_INCOME)
            val expense = repository.getTotalByType(TYPE_EXPENSE)
            val currentBalance = income - expense

            _totalIncome.postValue(income)
            _totalExpense.postValue(expense)
            _balance.postValue(currentBalance)
        }
    }

    fun insertTransaction(transaction: TransactionEntity) {
        executor.execute {
            repository.insertTransaction(transaction)
            refreshData()
        }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        executor.execute {
            repository.updateTransaction(transaction)
            refreshData()
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        executor.execute {
            repository.deleteTransaction(transaction)
            refreshData()
        }
    }

    fun loadTransactionById(id: Int) {
        executor.execute {
            val transaction = repository.getTransactionById(id)
            _selectedTransaction.postValue(transaction)
        }
    }

    private fun refreshData() {
        val transactions = repository.getAllTransactions()
        val income = repository.getTotalByType(TYPE_INCOME)
        val expense = repository.getTotalByType(TYPE_EXPENSE)
        val currentBalance = income - expense

        _allTransactions.postValue(transactions)
        _totalIncome.postValue(income)
        _totalExpense.postValue(expense)
        _balance.postValue(currentBalance)
    }

    override fun onCleared() {
        super.onCleared()
        executor.shutdown()
    }

    companion object {
        const val TYPE_INCOME = "INCOME"
        const val TYPE_EXPENSE = "EXPENSE"
    }
}