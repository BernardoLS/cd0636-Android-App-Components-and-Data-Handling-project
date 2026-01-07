package com.udacity.project.spire.ui.countries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.udacity.project.spire.SpireApplication
import com.udacity.project.spire.databinding.FragmentCountriesBinding

class CountriesFragment : Fragment() {

    private var _binding: FragmentCountriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SimpleTextAdapter

    private val viewModel: CountriesViewModel by viewModels {
        CountriesViewModelFactory(
            repository = (requireActivity().application as? SpireApplication)
                ?.buildingRepository
                ?: throw IllegalStateException("Application must be SpireApplication")
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCountriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = SimpleTextAdapter(emptyList())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )
    }

    private fun observeViewModel() {
        viewModel.countries.observe(viewLifecycleOwner) { countries ->
            adapter.updateData(countries)

            // Show/hide empty state
            val isEmpty = countries.isEmpty()
            binding.emptyState.isVisible = isEmpty
            binding.recyclerView.isVisible = !isEmpty
        }

        // Observe error events
        viewModel.errorEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { errorEvent ->
                Snackbar.make(
                    binding.root,
                    errorEvent.message,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}