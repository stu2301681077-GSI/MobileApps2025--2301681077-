package com.example.kasichka.ui.addedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.kasichka.databinding.FragmentAddEditTransactionBinding
import com.example.kasichka.data.local.TransactionEntity
import com.example.kasichka.viewmodel.TransactionViewModel

class AddEditTransactionFragment : Fragment() {

    private var _binding: FragmentAddEditTransactionBinding? = null
    private val binding get() = _binding!!

    private val transactionViewModel: TransactionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddEditTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveButton.setOnClickListener {
            saveTransaction()
        }
    }

    private fun saveTransaction() {
        val amountText = binding.amountEditText.text.toString().trim()
        val category = binding.categoryEditText.text.toString().trim()
        val description = binding.descriptionEditText.text.toString().trim()
        val note = binding.noteEditText.text.toString().trim()

        if (amountText.isEmpty()) {
            Toast.makeText(requireContext(), "Моля, въведете сума.", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()

        if (amount == null || amount <= 0.0) {
            Toast.makeText(requireContext(), "Моля, въведете валидна сума.", Toast.LENGTH_SHORT).show()
            return
        }

        if (category.isEmpty()) {
            Toast.makeText(requireContext(), "Моля, въведете категория.", Toast.LENGTH_SHORT).show()
            return
        }

        if (description.isEmpty()) {
            Toast.makeText(requireContext(), "Моля, въведете описание.", Toast.LENGTH_SHORT).show()
            return
        }

        val type = if (binding.incomeRadioButton.isChecked) {
            TransactionViewModel.TYPE_INCOME
        } else {
            TransactionViewModel.TYPE_EXPENSE
        }

        val transaction = TransactionEntity(
            amount = amount,
            type = type,
            category = category,
            description = description,
            date = System.currentTimeMillis(),
            note = note.ifEmpty { null },
            photoPath = null,
        )

        transactionViewModel.insertTransaction(transaction)

        Toast.makeText(requireContext(), "Транзакцията е запазена.", Toast.LENGTH_SHORT).show()

        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}