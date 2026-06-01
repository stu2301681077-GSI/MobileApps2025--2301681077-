package com.example.kasichka.data.local

data class TransactionEntity(
    val id: Int = 0,
    val amount: Double,
    val type: String,
    val category: String,
    val description: String,
    val date: Long,
    val note: String? = null,
    val photoPath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)