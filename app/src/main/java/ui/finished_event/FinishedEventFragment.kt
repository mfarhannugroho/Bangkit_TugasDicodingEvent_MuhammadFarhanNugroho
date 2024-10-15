package ui.finished_event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submissionawal_aplikasidicodingevent.R
import com.example.submissionawal_aplikasidicodingevent.databinding.FragmentFinishedEventBinding
import viewmodel.AdapterVerticalEvent
import viewmodel.MainViewModel
import viewmodel.ViewModelFactory

class FinishedEventFragment : Fragment() {  // Hapus <MainViewModel> yang tidak diperlukan

    private var _binding: FragmentFinishedEventBinding? = null
    private val binding get() = _binding
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var adapterVertical: AdapterVerticalEvent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedEventBinding.inflate(inflater, container, false)

        setupSearchView()
        setupRecyclerView()
        observeViewModel()

        return requireNotNull(binding?.root) { "Binding is null!" }
    }

    private fun setupSearchView() {
        binding?.apply {
            searchView.setupWithSearchBar(binding?.searchBar)

            searchView.editText.setOnEditorActionListener { _, _, _ ->
                val keyword = searchView.text.toString()
                mainViewModel.searchEvent(keyword)  // Memastikan MainViewModel memiliki fungsi searchEvent()

                val currentText = searchView.text

                searchView.hide()

                searchView.editText.text = currentText

                true
            }
        }
    }

    private fun setupRecyclerView() {
        binding?.apply {
            val verticalLayout = LinearLayoutManager(requireContext())
            rvFinishedEvent.layoutManager = verticalLayout
            val itemFinishedEventDecoration =
                DividerItemDecoration(requireContext(), verticalLayout.orientation)
            rvFinishedEvent.addItemDecoration(itemFinishedEventDecoration)
            adapterVertical = AdapterVerticalEvent { eventId ->
                val bundle = Bundle().apply {
                    if (eventId != null) {
                        putInt("eventId", eventId)
                    }
                }
                findNavController().navigate(R.id.navigation_detail, bundle)
            }
            rvFinishedEvent.adapter = adapterVertical
        }
    }

    private fun observeViewModel() {
        // Pastikan MainViewModel memiliki LiveData isLoading, finishedEvent, dan searchEvent
        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        mainViewModel.finishedEvent.observe(viewLifecycleOwner) { listItems ->
            adapterVertical.submitList(listItems)
        }

        mainViewModel.searchEvent.observe(viewLifecycleOwner) { listItems ->
            adapterVertical.submitList(listItems)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}