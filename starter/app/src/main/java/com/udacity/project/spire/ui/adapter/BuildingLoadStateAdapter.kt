package com.udacity.project.spire.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.project.spire.databinding.ItemLoadStateBinding

/**
 * LoadStateAdapter for displaying loading and error states in the RecyclerView.
 * Shows a progress bar while loading and an error message with retry button on failure.
 *
 * @param retry Callback invoked when the retry button is clicked
 */
class BuildingLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<BuildingLoadStateAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = ItemLoadStateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LoadStateViewHolder(binding, retry)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    /**
     * ViewHolder for load state items.
     * Displays loading progress or error message based on the load state.
     */
    class LoadStateViewHolder(
        private val binding: ItemLoadStateBinding,
        private val retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonRetry.setOnClickListener {
                retry()
            }
        }

        fun bind(loadState: LoadState) {
            binding.apply {
                // Show progress bar only when loading
                progressBar.isVisible = loadState is LoadState.Loading

                // Show error message and retry button only on error
                textError.isVisible = loadState is LoadState.Error
                buttonRetry.isVisible = loadState is LoadState.Error

                // Set error message if present
                if (loadState is LoadState.Error) {
                    textError.text = loadState.error.localizedMessage
                        ?: "An error occurred while loading data"
                }
            }
        }
    }
}