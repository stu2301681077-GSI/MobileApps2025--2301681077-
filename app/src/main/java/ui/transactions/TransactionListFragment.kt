package com.example.kasichka.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kasichka.R
import com.example.kasichka.databinding.FragmentTransactionListBinding
import com.example.kasichka.viewmodel.TransactionViewModel

class TransactionListFragment : Fragment() {

    private var _binding: FragmentTransactionListBinding? = null
    private val binding get() = _binding!!

    private val transactionViewModel: TransactionViewModel by activityViewModels()

    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTransactionListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observeTransactions()
    }

    override fun onResume() {
        super.onResume()
        transactionViewModel.loadTransactions()
        transactionViewModel.loadTotals()
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(
            onItemClick = {
                // Детайлен екран ще добавим в следващ етап.
            },
        )

        binding.transactionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }
    }

    private fun setupClickListeners() {
        binding.addTransactionButton.setOnClickListener {
            findNavController().navigate(R.id.addEditTransactionFragment)
        }
    }

    private fun observeTransactions() {
        transactionViewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.submitList(transactions)

            if (transactions.isEmpty()) {
                binding.emptyTextView.visibility = View.VISIBLE
                binding.transactionsRecyclerView.visibility = View.GONE
            } else {
                binding.emptyTextView.visibility = View.GONE
                binding.transactionsRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}