package com.example.kasichka.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.kasichka.data.local.TransactionEntity
import com.example.kasichka.databinding.FragmentTransactionDetailBinding
import com.example.kasichka.viewmodel.TransactionViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.os.bundleOf
import com.example.kasichka.R

class TransactionDetailFragment : Fragment() {

    private var _binding: FragmentTransactionDetailBinding? = null
    private val binding get() = _binding!!

    private val transactionViewModel: TransactionViewModel by activityViewModels()

    private var currentTransaction: TransactionEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTransactionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val transactionId = arguments?.getInt(ARG_TRANSACTION_ID) ?: -1

        if (transactionId == -1) {
            Toast.makeText(requireContext(), "Невалидна транзакция.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        observeTransaction()
        setupClickListeners()

        transactionViewModel.loadTransactionById(transactionId)
    }

    private fun observeTransaction() {
        transactionViewModel.selectedTransaction.observe(viewLifecycleOwner) { transaction ->
            if (transaction == null) {
                Toast.makeText(requireContext(), "Транзакцията не е намерена.", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
                return@observe
            }

            currentTransaction = transaction
            showTransaction(transaction)
        }
    }

    private fun showTransaction(transaction: TransactionEntity) {
        binding.descriptionValueTextView.text = transaction.description

        val sign = if (transaction.type == TransactionViewModel.TYPE_INCOME) {
            "+"
        } else {
            "-"
        }

        binding.amountValueTextView.text =
            "$sign${String.format(Locale.US, "%.2f", transaction.amount)} €"

        val typeText = if (transaction.type == TransactionViewModel.TYPE_INCOME) {
            "Приход"
        } else {
            "Разход"
        }

        binding.typeValueTextView.text = "Тип: $typeText"
        binding.categoryValueTextView.text = "Категория: ${transaction.category}"
        binding.dateValueTextView.text = "Дата: ${formatDate(transaction.date)}"
        binding.noteValueTextView.text = "Бележка: ${transaction.note ?: "-"}"
    }

    private fun setupClickListeners() {
        binding.editButton.setOnClickListener {
            val transaction = currentTransaction ?: return@setOnClickListener

            val bundle = bundleOf(ARG_TRANSACTION_ID to transaction.id)
            findNavController().navigate(R.id.addEditTransactionFragment, bundle)
        }

        binding.deleteButton.setOnClickListener {
            showDeleteDialog()
        }
    }

    private fun showDeleteDialog() {
        val transaction = currentTransaction ?: return

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Изтриване")
            .setMessage("Сигурни ли сте, че искате да изтриете тази транзакция?")
            .setNegativeButton("Отказ", null)
            .setPositiveButton("Изтрий") { _, _ ->
                transactionViewModel.deleteTransaction(transaction)
                Toast.makeText(requireContext(), "Транзакцията е изтрита.", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            .show()
    }

    private fun formatDate(timestamp: Long): String {
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_TRANSACTION_ID = "transactionId"
    }
}