package com.example.kasichka.ui.addedit

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.kasichka.data.local.TransactionEntity
import com.example.kasichka.databinding.FragmentAddEditTransactionBinding
import com.example.kasichka.viewmodel.TransactionViewModel
import java.io.File
import com.example.kasichka.util.TransactionValidator

class AddEditTransactionFragment : Fragment() {

    private var _binding: FragmentAddEditTransactionBinding? = null
    private val binding get() = _binding!!

    private val transactionViewModel: TransactionViewModel by activityViewModels()

    private var transactionId: Int = -1
    private var currentTransaction: TransactionEntity? = null

    private var currentPhotoPath: String? = null
    private var temporaryPhotoFile: File? = null

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture(),
    ) { success ->
        if (success) {
            val photoFile = temporaryPhotoFile

            if (photoFile != null) {
                currentPhotoPath = photoFile.absolutePath
                showPhotoPreview(photoFile.absolutePath)
                Toast.makeText(requireContext(), "Снимката е добавена.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Снимката не беше запазена.", Toast.LENGTH_SHORT).show()
        }
    }

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

        transactionId = arguments?.getInt(ARG_TRANSACTION_ID) ?: -1

        setupScreenMode()
        setupClickListeners()
    }

    private fun setupScreenMode() {
        if (transactionId == -1) {
            binding.titleTextView.text = "Добавяне на транзакция"
            binding.saveButton.text = "Запази"
        } else {
            binding.titleTextView.text = "Редактиране на транзакция"
            binding.saveButton.text = "Запази промените"

            observeTransactionForEdit()
            transactionViewModel.loadTransactionById(transactionId)
        }
    }

    private fun observeTransactionForEdit() {
        transactionViewModel.selectedTransaction.observe(viewLifecycleOwner) { transaction ->
            if (transaction == null || transaction.id != transactionId) {
                return@observe
            }

            currentTransaction = transaction
            currentPhotoPath = transaction.photoPath
            fillFields(transaction)
        }
    }

    private fun fillFields(transaction: TransactionEntity) {
        binding.amountEditText.setText(transaction.amount.toString())
        binding.categoryEditText.setText(transaction.category)
        binding.descriptionEditText.setText(transaction.description)
        binding.noteEditText.setText(transaction.note.orEmpty())

        if (transaction.type == TransactionViewModel.TYPE_INCOME) {
            binding.incomeRadioButton.isChecked = true
        } else {
            binding.expenseRadioButton.isChecked = true
        }

        transaction.photoPath?.let { photoPath ->
            showPhotoPreview(photoPath)
        }
    }

    private fun setupClickListeners() {
        binding.takePhotoButton.setOnClickListener {
            openCamera()
        }

        binding.saveButton.setOnClickListener {
            saveTransaction()
        }
    }

    private fun openCamera() {
        val photoFile = createImageFile()
        temporaryPhotoFile = photoFile

        val photoUri: Uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            photoFile,
        )

        takePictureLauncher.launch(photoUri)
    }

    private fun createImageFile(): File {
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            "receipt_${System.currentTimeMillis()}_",
            ".jpg",
            storageDir,
        )
    }

    private fun showPhotoPreview(photoPath: String) {
        val photoFile = File(photoPath)

        if (photoFile.exists()) {
            binding.receiptPreviewImageView.setImageURI(Uri.fromFile(photoFile))
            binding.receiptPreviewImageView.visibility = View.VISIBLE
        }
    }

    private fun saveTransaction() {
        val amountText = binding.amountEditText.text.toString().trim()
        val category = binding.categoryEditText.text.toString().trim()
        val description = binding.descriptionEditText.text.toString().trim()
        val note = binding.noteEditText.text.toString().trim()

        val amount = TransactionValidator.parseAmount(amountText)

        if (amount == null) {
            Toast.makeText(requireContext(), "Моля, въведете валидна сума.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!TransactionValidator.isRequiredTextValid(category)) {
            Toast.makeText(requireContext(), "Моля, въведете категория.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!TransactionValidator.isRequiredTextValid(description)) {
            Toast.makeText(requireContext(), "Моля, въведете описание.", Toast.LENGTH_SHORT).show()
            return
        }

        val type = if (binding.incomeRadioButton.isChecked) {
            TransactionViewModel.TYPE_INCOME
        } else {
            TransactionViewModel.TYPE_EXPENSE
        }

        if (transactionId == -1) {
            createTransaction(
                amount = amount,
                type = type,
                category = category,
                description = description,
                note = note,
            )
        } else {
            updateTransaction(
                amount = amount,
                type = type,
                category = category,
                description = description,
                note = note,
            )
        }
    }

    private fun createTransaction(
        amount: Double,
        type: String,
        category: String,
        description: String,
        note: String,
    ) {
        val transaction = TransactionEntity(
            amount = amount,
            type = type,
            category = category,
            description = description,
            date = System.currentTimeMillis(),
            note = note.ifEmpty { null },
            photoPath = currentPhotoPath,
        )

        transactionViewModel.insertTransaction(transaction)

        Toast.makeText(requireContext(), "Транзакцията е запазена.", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    private fun updateTransaction(
        amount: Double,
        type: String,
        category: String,
        description: String,
        note: String,
    ) {
        val oldTransaction = currentTransaction

        if (oldTransaction == null) {
            Toast.makeText(requireContext(), "Транзакцията не е заредена.", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedTransaction = oldTransaction.copy(
            amount = amount,
            type = type,
            category = category,
            description = description,
            note = note.ifEmpty { null },
            photoPath = currentPhotoPath,
        )

        transactionViewModel.updateTransaction(updatedTransaction)

        Toast.makeText(requireContext(), "Промените са запазени.", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_TRANSACTION_ID = "transactionId"
    }
}