package com.example.kasichka.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kasichka.data.local.TransactionEntity
import com.example.kasichka.databinding.ItemTransactionBinding
import com.example.kasichka.viewmodel.TransactionViewModel

class TransactionAdapter(
    private val onItemClick: (TransactionEntity) -> Unit,
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private val transactions = mutableListOf<TransactionEntity>()

    fun submitList(newTransactions: List<TransactionEntity>) {
        transactions.clear()
        transactions.addAll(newTransactions)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: TransactionViewHolder,
        position: Int,
    ) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int = transactions.size

    inner class TransactionViewHolder(
        private val binding: ItemTransactionBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: TransactionEntity) {
            binding.descriptionTextView.text = transaction.description
            binding.categoryTextView.text = "Категория: ${transaction.category}"

            val typeText = if (transaction.type == TransactionViewModel.TYPE_INCOME) {
                "Приход"
            } else {
                "Разход"
            }

            binding.typeTextView.text = "Тип: $typeText"

            val sign = if (transaction.type == TransactionViewModel.TYPE_INCOME) {
                "+"
            } else {
                "-"
            }

            binding.amountTextView.text = "$sign${String.format("%.2f", transaction.amount)} €"

            binding.root.setOnClickListener {
                onItemClick(transaction)
            }
        }
    }
}