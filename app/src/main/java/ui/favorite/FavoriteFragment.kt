package ui.favorite

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submissionawal_aplikasidicodingevent.R
import com.example.submissionawal_aplikasidicodingevent.databinding.FragmentFavoriteBinding
import data.local.model.FavoriteEvent
import utils.UiHandler.handleError
import utils.UiHandler.showLoading
import viewmodel.AdapterFavoriteEvent
import viewmodel.MainViewModel
import viewmodel.ViewModelFactory

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var adapterFavoriteEvent: AdapterFavoriteEvent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupAdapter()
        observeViewModel()

        return requireNotNull(binding?.root) { "Binding is null!" }
    }

    private fun setupRecyclerView() {
        binding?.apply {
            val verticalLayout = LinearLayoutManager(requireContext())
            rvFavoriteEvent.layoutManager = verticalLayout
            val itemFavoriteEventDecoration =
                DividerItemDecoration(requireContext(), verticalLayout.orientation)
            rvFavoriteEvent.addItemDecoration(itemFavoriteEventDecoration)
        }
    }

    private fun setupAdapter() {
        adapterFavoriteEvent = AdapterFavoriteEvent { eventId ->
            val bundle = Bundle().apply {
                eventId?.let { putInt("eventId", it) }
            }
            findNavController().navigate(R.id.navigation_detail, bundle)
        }

        binding?.rvFavoriteEvent?.adapter = adapterFavoriteEvent
    }

    private fun observeViewModel() {
        binding?.apply {
            mainViewModel.allFavoriteEvents.observe(viewLifecycleOwner) { listItems ->
                Log.d("FavoriteFragment", "Observed Favorite Events: $listItems")
                setFavoriteEvent(listItems)
                mainViewModel.clearErrorMessage()
            }

            mainViewModel.isLoading.observe(viewLifecycleOwner) {
                showLoading(it, binding?.progressBar!!, binding?.rvFavoriteEvent!!) // Tambahkan recyclerView
            }

            mainViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                handleError(
                    isError = errorMessage != null,
                    message = errorMessage,
                    errorTextView = binding?.tvErrorMessage!!,
                    refreshButton = binding?.btnRefresh!!,
                    recyclerView = binding?.rvFavoriteEvent!! // Pastikan untuk menambahkan recyclerView di sini
                ) {
                    mainViewModel.getAllFavoriteEvent()
                }
            }
        }
    }


    private fun setFavoriteEvent(listFavoriteEvent: List<FavoriteEvent>) {
        adapterFavoriteEvent.submitList(listFavoriteEvent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}