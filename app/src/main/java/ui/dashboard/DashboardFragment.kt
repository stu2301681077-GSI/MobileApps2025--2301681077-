package com.example.kasichka.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kasichka.R
import com.example.kasichka.databinding.FragmentDashboardBinding
import com.example.kasichka.ui.transactions.TransactionAdapter
import com.example.kasichka.viewmodel.TransactionViewModel
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val transactionViewModel: TransactionViewModel by activityViewModels()

    private lateinit var recentTransactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupRecentTransactions()
        setupClickListeners()
        observeDashboardData()
    }

    override fun onResume() {
        super.onResume()
        transactionViewModel.loadTransactions()
        transactionViewModel.loadTotals()
    }

    private fun setupRecentTransactions() {
        recentTransactionAdapter = TransactionAdapter(
            onItemClick = { transaction ->
                val bundle = bundleOf("transactionId" to transaction.id)
                findNavController().navigate(R.id.transactionDetailFragment, bundle)
            },
        )

        binding.recentTransactionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recentTransactionAdapter
        }
    }

    private fun setupClickListeners() {
        binding.addTransactionButton.setOnClickListener {
            findNavController().navigate(R.id.addEditTransactionFragment)
        }
    }

    private fun observeDashboardData() {
        transactionViewModel.balance.observe(viewLifecycleOwner) { balance ->
            binding.balanceValueTextView.text = formatMoney(balance)
        }

        transactionViewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            binding.incomeValueTextView.text = formatMoney(income)
        }

        transactionViewModel.totalExpense.observe(viewLifecycleOwner) { expense ->
            binding.expenseValueTextView.text = formatMoney(expense)
        }

        transactionViewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            val recentTransactions = transactions.take(5)
            recentTransactionAdapter.submitList(recentTransactions)

            if (recentTransactions.isEmpty()) {
                binding.emptyRecentTextView.visibility = View.VISIBLE
                binding.recentTransactionsRecyclerView.visibility = View.GONE
            } else {
                binding.emptyRecentTextView.visibility = View.GONE
                binding.recentTransactionsRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun formatMoney(value: Double): String {
        return "${String.format(Locale.US, "%.2f", value)} €"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}