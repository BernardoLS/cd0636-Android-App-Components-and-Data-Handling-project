package com.udacity.project.spire.ui.buildings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import com.udacity.project.spire.SpireApplication
import com.udacity.project.spire.databinding.FragmentBuildingsBinding
import com.udacity.project.spire.ui.adapter.BuildingLoadStateAdapter
import com.udacity.project.spire.ui.adapter.BuildingPagingAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BuildingsFragment : Fragment() {

    private var _binding: FragmentBuildingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: BuildingPagingAdapter

    private val viewModel: BuildingsViewModel by viewModels {
        BuildingsViewModelFactory(
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
        _binding = FragmentBuildingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeRefresh()
        observeBuildings()
        observeErrors()
    }

    private fun setupRecyclerView() {
        adapter = BuildingPagingAdapter { building ->
            val action = BuildingsFragmentDirections
                .actionBuildingsToDetail(building.id)
            findNavController().navigate(action)
        }

        // Connect LoadStateAdapter to show loading/error states during pagination
        binding.recyclerView.adapter = adapter.withLoadStateFooter(
            footer = BuildingLoadStateAdapter { adapter.retry() }
        )

        // Show empty state when there's no data
        adapter.addLoadStateListener { loadState ->
            val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
            binding.emptyState.isVisible = isListEmpty
            binding.swipeRefresh.isVisible = !isListEmpty
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun observeBuildings() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.buildings.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun observeErrors() {
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