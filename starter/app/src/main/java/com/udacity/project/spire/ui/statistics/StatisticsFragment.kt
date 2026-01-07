package com.udacity.project.spire.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.udacity.project.spire.SpireApplication
import com.udacity.project.spire.databinding.FragmentStatisticsBinding

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StatisticsViewModel by viewModels {
        StatisticsViewModelFactory(
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
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.statistics.observe(viewLifecycleOwner) { stats ->
            binding.apply {
                textTotalBuildings.text = stats.totalBuildings.toString()
                textVisitedCount.text = stats.visitedCount.toString()
                textBucketListCount.text = stats.bucketListCount.toString()
                textMetersClimbed.text = "${String.format("%,d", stats.totalMetersClimbed)}m"
                textCountriesExplored.text = stats.countriesExplored.toString()
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Show/hide progress indicator if available in layout
            // For now, we'll just rely on the Snackbar for feedback
        }

        // Observe error events
        viewModel.errorEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { errorEvent ->
                Snackbar.make(
                    binding.root,
                    errorEvent.message,
                    Snackbar.LENGTH_LONG
                ).setAction("Retry") {
                    viewModel.loadStatistics()
                }.show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}