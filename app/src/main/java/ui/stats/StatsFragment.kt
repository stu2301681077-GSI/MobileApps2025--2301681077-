package com.example.kasichka.ui.stats

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.kasichka.data.local.TransactionEntity
import com.example.kasichka.databinding.FragmentStatsBinding
import com.example.kasichka.util.QrCodeGenerator
import com.example.kasichka.viewmodel.TransactionViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    private val transactionViewModel: TransactionViewModel by activityViewModels()

    private var currentMonthIncome: Double = 0.0
    private var currentMonthExpense: Double = 0.0
    private var currentMonthBalance: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.monthTextView.text = "Текущ месец: ${getCurrentMonthLabel()}"

        setupClickListeners()
        observeStats()
    }

    override fun onResume() {
        super.onResume()
        transactionViewModel.loadTransactions()
        transactionViewModel.loadTotals()
    }

    private fun setupClickListeners() {
        binding.generateQrButton.setOnClickListener {
            showQrReportDialog()
        }
    }

    private fun observeStats() {
        transactionViewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            val currentMonthTransactions = transactions.filter { transaction ->
                isCurrentMonth(transaction.date)
            }

            val totalIncome = currentMonthTransactions
                .filter { it.type == TransactionViewModel.TYPE_INCOME }
                .sumOf { it.amount }

            val totalExpense = currentMonthTransactions
                .filter { it.type == TransactionViewModel.TYPE_EXPENSE }
                .sumOf { it.amount }

            val balance = totalIncome - totalExpense

            currentMonthIncome = totalIncome
            currentMonthExpense = totalExpense
            currentMonthBalance = balance

            binding.incomeStatsValueTextView.text = formatMoney(totalIncome)
            binding.expenseStatsValueTextView.text = formatMoney(totalExpense)
            binding.balanceStatsValueTextView.text = formatMoney(balance)

            val expenseTransactions = currentMonthTransactions.filter {
                it.type == TransactionViewModel.TYPE_EXPENSE
            }

            showCategoryStats(
                expenseTransactions = expenseTransactions,
                totalExpense = totalExpense,
            )
        }
    }

    private fun showQrReportDialog() {
        val reportText = buildMonthlyReportText()
        val qrBitmap = QrCodeGenerator.generateQrCode(reportText)

        val imageView = ImageView(requireContext()).apply {
            setImageBitmap(qrBitmap)
            adjustViewBounds = true
            setPadding(
                24.dp(),
                24.dp(),
                24.dp(),
                24.dp(),
            )
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("QR отчет")
            .setMessage("Сканирайте QR кода, за да видите месечния финансов отчет.")
            .setView(imageView)
            .setPositiveButton("Затвори", null)
            .show()
    }

    private fun buildMonthlyReportText(): String {
        return """
        Kasichka - Monthly Report
        Month: ${getCurrentMonthLabelForQr()}
        Income: ${formatMoneyForQr(currentMonthIncome)}
        Expense: ${formatMoneyForQr(currentMonthExpense)}
        Balance: ${formatMoneyForQr(currentMonthBalance)}
    """.trimIndent()
    }

    private fun getCurrentMonthLabelForQr(): String {
        val formatter = SimpleDateFormat("MMMM yyyy", Locale.US)
        return formatter.format(Date())
    }

    private fun formatMoneyForQr(value: Double): String {
        return "${String.format(Locale.US, "%.2f", value)} EUR"
    }

    private fun showCategoryStats(
        expenseTransactions: List<TransactionEntity>,
        totalExpense: Double,
    ) {
        binding.categoriesContainer.removeAllViews()

        if (expenseTransactions.isEmpty() || totalExpense <= 0.0) {
            binding.emptyCategoriesTextView.visibility = View.VISIBLE
            binding.categoriesContainer.visibility = View.GONE
            return
        }

        binding.emptyCategoriesTextView.visibility = View.GONE
        binding.categoriesContainer.visibility = View.VISIBLE

        val categoryTotals = expenseTransactions
            .groupBy { it.category }
            .mapValues { entry ->
                entry.value.sumOf { transaction -> transaction.amount }
            }
            .toList()
            .sortedByDescending { it.second }

        categoryTotals.forEach { categoryItem ->
            val categoryName = categoryItem.first
            val categoryTotal = categoryItem.second
            val percent = ((categoryTotal / totalExpense) * 100).toInt().coerceIn(1, 100)

            addCategoryView(
                categoryName = categoryName,
                categoryTotal = categoryTotal,
                percent = percent,
            )
        }
    }

    private fun addCategoryView(
        categoryName: String,
        categoryTotal: Double,
        percent: Int,
    ) {
        val titleTextView = TextView(requireContext()).apply {
            text = "$categoryName — ${formatMoney(categoryTotal)}"
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
        }

        val titleParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        ).apply {
            topMargin = 16.dp()
        }

        binding.categoriesContainer.addView(titleTextView, titleParams)

        val progressBar = ProgressBar(
            requireContext(),
            null,
            android.R.attr.progressBarStyleHorizontal,
        ).apply {
            max = 100
            progress = percent
        }

        val progressParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        ).apply {
            topMargin = 6.dp()
        }

        binding.categoriesContainer.addView(progressBar, progressParams)

        val percentTextView = TextView(requireContext()).apply {
            text = "$percent% от всички разходи"
            textSize = 14f
        }

        val percentParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        ).apply {
            topMargin = 4.dp()
        }

        binding.categoriesContainer.addView(percentTextView, percentParams)
    }

    private fun isCurrentMonth(timestamp: Long): Boolean {
        val now = Calendar.getInstance()
        val transactionDate = Calendar.getInstance().apply {
            timeInMillis = timestamp
        }

        return now.get(Calendar.YEAR) == transactionDate.get(Calendar.YEAR) &&
                now.get(Calendar.MONTH) == transactionDate.get(Calendar.MONTH)
    }

    private fun getCurrentMonthLabel(): String {
        val formatter = SimpleDateFormat("MMMM yyyy", Locale("bg", "BG"))
        return formatter.format(Date())
    }

    private fun formatMoney(value: Double): String {
        return "${String.format(Locale.US, "%.2f", value)} €"
    }

    private fun Int.dp(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}