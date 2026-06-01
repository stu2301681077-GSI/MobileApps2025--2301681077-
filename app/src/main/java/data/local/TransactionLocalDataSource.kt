package com.example.kasichka.data.local

import android.content.ContentValues

class TransactionLocalDataSource(
    private val databaseHelper: KasichkaDatabaseHelper,
) {

    fun getAllTransactions(): List<TransactionEntity> {
        val transactions = mutableListOf<TransactionEntity>()
        val db = databaseHelper.readableDatabase

        val cursor = db.query(
            KasichkaDatabaseHelper.TABLE_TRANSACTIONS,
            null,
            null,
            null,
            null,
            null,
            "${KasichkaDatabaseHelper.COLUMN_DATE} DESC",
        )

        cursor.use {
            while (it.moveToNext()) {
                transactions.add(it.toTransactionEntity())
            }
        }

        return transactions
    }

    fun getTransactionById(id: Int): TransactionEntity? {
        val db = databaseHelper.readableDatabase

        val cursor = db.query(
            KasichkaDatabaseHelper.TABLE_TRANSACTIONS,
            null,
            "${KasichkaDatabaseHelper.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null,
        )

        cursor.use {
            return if (it.moveToFirst()) {
                it.toTransactionEntity()
            } else {
                null
            }
        }
    }

    fun insertTransaction(transaction: TransactionEntity): Long {
        val db = databaseHelper.writableDatabase

        val values = ContentValues().apply {
            put(KasichkaDatabaseHelper.COLUMN_AMOUNT, transaction.amount)
            put(KasichkaDatabaseHelper.COLUMN_TYPE, transaction.type)
            put(KasichkaDatabaseHelper.COLUMN_CATEGORY, transaction.category)
            put(KasichkaDatabaseHelper.COLUMN_DESCRIPTION, transaction.description)
            put(KasichkaDatabaseHelper.COLUMN_DATE, transaction.date)
            put(KasichkaDatabaseHelper.COLUMN_NOTE, transaction.note)
            put(KasichkaDatabaseHelper.COLUMN_PHOTO_PATH, transaction.photoPath)
            put(KasichkaDatabaseHelper.COLUMN_CREATED_AT, transaction.createdAt)
        }

        return db.insert(KasichkaDatabaseHelper.TABLE_TRANSACTIONS, null, values)
    }

    fun updateTransaction(transaction: TransactionEntity): Int {
        val db = databaseHelper.writableDatabase

        val values = ContentValues().apply {
            put(KasichkaDatabaseHelper.COLUMN_AMOUNT, transaction.amount)
            put(KasichkaDatabaseHelper.COLUMN_TYPE, transaction.type)
            put(KasichkaDatabaseHelper.COLUMN_CATEGORY, transaction.category)
            put(KasichkaDatabaseHelper.COLUMN_DESCRIPTION, transaction.description)
            put(KasichkaDatabaseHelper.COLUMN_DATE, transaction.date)
            put(KasichkaDatabaseHelper.COLUMN_NOTE, transaction.note)
            put(KasichkaDatabaseHelper.COLUMN_PHOTO_PATH, transaction.photoPath)
            put(KasichkaDatabaseHelper.COLUMN_CREATED_AT, transaction.createdAt)
        }

        return db.update(
            KasichkaDatabaseHelper.TABLE_TRANSACTIONS,
            values,
            "${KasichkaDatabaseHelper.COLUMN_ID} = ?",
            arrayOf(transaction.id.toString()),
        )
    }

    fun deleteTransaction(transaction: TransactionEntity): Int {
        val db = databaseHelper.writableDatabase

        return db.delete(
            KasichkaDatabaseHelper.TABLE_TRANSACTIONS,
            "${KasichkaDatabaseHelper.COLUMN_ID} = ?",
            arrayOf(transaction.id.toString()),
        )
    }

    fun getTotalByType(type: String): Double {
        val db = databaseHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT SUM(${KasichkaDatabaseHelper.COLUMN_AMOUNT}) FROM ${KasichkaDatabaseHelper.TABLE_TRANSACTIONS} WHERE ${KasichkaDatabaseHelper.COLUMN_TYPE} = ?",
            arrayOf(type),
        )

        cursor.use {
            return if (it.moveToFirst()) {
                it.getDouble(0)
            } else {
                0.0
            }
        }
    }

    private fun android.database.Cursor.toTransactionEntity(): TransactionEntity {
        return TransactionEntity(
            id = getInt(getColumnIndexOrThrow(KasichkaDatabaseHelper.COLUMN_ID)),
            amount = getDouble(getColumnIndexOrThrow(KasichkaDatabaseHelper.COLUMN_AMOUNT)),
            type = getString(getColumnIndexOrThrow(KasichkaDatabaseHelper.COLUMN_TYPE)),
            category = getString(getColumnIndexOrThrow(KasichkaDatabaseHelper.COLUMN_CATEGORY)),
            description = getString(getColumnIndexOrThrow(KasichkaDatabaseHelper.COLUMN_DESCRIPTION)),
            date = getLong(getColumnIndexOrThrow(KasichkaDatabaseHelper.COLUMN_DATE)),
            note = getString(getColumnIndexOrThrow(KasichkaDatabaseHelper.COLUMN_NOTE)),
            photoPath = getString(getColumnIndexOrThrow(KasichkaDatabaseHelper.COLUMN_PHOTO_PATH)),
            createdAt = getLong(getColumnIndexOrThrow(KasichkaDatabaseHelper.COLUMN_CREATED_AT)),
        )
    }
}