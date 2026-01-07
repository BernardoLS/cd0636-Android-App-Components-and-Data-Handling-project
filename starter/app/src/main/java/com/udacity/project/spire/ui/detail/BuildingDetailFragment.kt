package com.udacity.project.spire.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.udacity.project.spire.R
import com.udacity.project.spire.SpireApplication
import com.udacity.project.spire.databinding.FragmentBuildingDetailBinding
import com.udacity.project.spire.domain.model.Building
import com.udacity.project.spire.domain.model.VisitStatus

class BuildingDetailFragment : Fragment() {

    private var _binding: FragmentBuildingDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BuildingDetailViewModel by viewModels {
        BuildingDetailViewModelFactory(
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
        _binding = FragmentBuildingDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setupButtons()
    }

    private fun observeViewModel() {
        viewModel.building.observe(viewLifecycleOwner) { building ->
            building?.let {
                displayBuildingDetails(it)
            }
        }

        // Observe error events
        viewModel.errorEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { errorEvent ->
                Snackbar.make(
                    binding.root,
                    errorEvent.message,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        // Observe success events
        viewModel.updateSuccess.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Snackbar.make(
                    binding.root,
                    message,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun displayBuildingDetails(building: Building) {
        Log.d("BuildingDetailFragment", "Displaying building details: $building")
        binding.apply {
            //Todo: Bind building details, image loading  and update buttons
            //updateButtons()
        }
    }

    private fun setupButtons() {
        binding.buttonBucketList.setOnClickListener {
            viewModel.building.value?.let { building ->
                // Toggle between BUCKET_LIST and NOT_VISITED
                val newStatus = if (building.visitStatus == VisitStatus.BUCKET_LIST) {
                    VisitStatus.NOT_VISITED
                } else {
                    VisitStatus.BUCKET_LIST
                }
                viewModel.updateVisitStatus(newStatus)
            }
        }

        binding.buttonVisited.setOnClickListener {
            viewModel.building.value?.let { building ->
                // Toggle between VISITED and NOT_VISITED
                val newStatus = if (building.visitStatus == VisitStatus.VISITED) {
                    VisitStatus.NOT_VISITED
                } else {
                    VisitStatus.VISITED
                }
                viewModel.updateVisitStatus(newStatus)
            }
        }
    }

    private fun updateButtons(status: VisitStatus) {
        binding.apply {
            when (status) {
                VisitStatus.NOT_VISITED -> {
                    buttonBucketList.visibility = View.VISIBLE
                    buttonBucketList.text = "Add to Bucket List"
                    buttonVisited.visibility = View.VISIBLE
                    buttonVisited.text = "Mark as Visited"
                }
                VisitStatus.BUCKET_LIST -> {
                    buttonBucketList.visibility = View.VISIBLE
                    buttonBucketList.text = "Remove from Bucket List"
                    buttonVisited.visibility = View.VISIBLE
                    buttonVisited.text = "Mark as Visited"
                }
                VisitStatus.VISITED -> {
                    buttonBucketList.visibility = View.GONE
                    buttonVisited.visibility = View.VISIBLE
                    buttonVisited.text = "Mark as Unvisited"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}