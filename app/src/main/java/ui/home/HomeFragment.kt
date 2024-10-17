package ui.home

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
import com.example.submissionawal_aplikasidicodingevent.databinding.FragmentHomeBinding
import data.remote.response.ListEventsItem
import utils.UiHandler.handleError
import viewmodel.AdapterHorizontalEvent
import viewmodel.AdapterVerticalEvent
import viewmodel.MainViewModel
import viewmodel.ViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var adapterVertical: AdapterVerticalEvent
    private lateinit var adapterHorizontal: AdapterHorizontalEvent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerViews()
        setupAdapters()
        observeViewModel()

        // Pengecekan apakah data sudah ada atau belum di LiveData
        if (mainViewModel.upcomingEvent.value == null) {
            // Jika belum ada, fetch data upcoming events
            mainViewModel.getUpcomingEvent()
        }

        if (mainViewModel.finishedEvent.value == null) {
            // Jika belum ada, fetch data finished events
            mainViewModel.getFinishedEvent()
        }

        return requireNotNull(binding?.root) { "Binding is null!" }
    }

    private fun setupRecyclerViews() {
        binding?.apply {
            val horizontalLayout =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvUpcomingEvent.layoutManager = horizontalLayout
            rvUpcomingEvent.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    horizontalLayout.orientation
                )
            )
            val verticalLayout = LinearLayoutManager(requireContext())
            rvFinishedEvent.layoutManager = verticalLayout
            rvFinishedEvent.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    verticalLayout.orientation
                )
            )
        }
    }

    private fun setupAdapters() {
        adapterVertical = AdapterVerticalEvent { eventId ->
            val bundle = Bundle().apply {
                if (eventId != null) {
                    putInt("eventId", eventId)
                }
            }
            findNavController().navigate(R.id.navigation_detail, bundle)
        }

        adapterHorizontal = AdapterHorizontalEvent { eventId ->
            val bundle = Bundle().apply {
                if (eventId != null) {
                    putInt("eventId", eventId)
                }
            }
            findNavController().navigate(R.id.navigation_detail, bundle)
        }

        binding?.apply {
            rvUpcomingEvent.adapter = adapterHorizontal
            rvFinishedEvent.adapter = adapterVertical
        }
    }

    private fun observeViewModel() {
        binding?.apply {
            // Observer untuk upcoming events
            mainViewModel.upcomingEvent.observe(viewLifecycleOwner) { listItems ->
                setUpcomingEvent(listItems)
                mainViewModel.clearErrorMessage()
            }

            // Observer untuk finished events
            mainViewModel.finishedEvent.observe(viewLifecycleOwner) { listItems ->
                setFinishedEvent(listItems)
                mainViewModel.clearErrorMessage()
            }

            // Observer untuk loading upcoming events
            mainViewModel.isLoadingUpcoming.observe(viewLifecycleOwner) { isLoading ->
                showLoadingUpcoming(isLoading)  // Pastikan menggunakan loading yang terpisah
            }

            // Observer untuk loading finished events
            mainViewModel.isLoadingFinished.observe(viewLifecycleOwner) { isLoading ->
                showLoadingFinished(isLoading)  // Pastikan menggunakan loading yang terpisah
            }

            mainViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
                handleError(
                    isError = errorMessage != null,
                    message = errorMessage,
                    errorTextView = tvErrorMessage,
                    refreshButton = btnRefresh,
                    recyclerView = rvFinishedEvent
                ) {
                    mainViewModel.getUpcomingEvent()
                    mainViewModel.getFinishedEvent()
                }
            }
        }
    }

    private fun showLoadingUpcoming(isLoading: Boolean) {
        if (isLoading) {
            binding?.progressBar?.visibility = View.VISIBLE // Tampilkan ProgressBar untuk upcoming event
            binding?.rvUpcomingEvent?.visibility = View.GONE // Sembunyikan RecyclerView upcoming saat loading
        } else {
            binding?.progressBar?.visibility = View.GONE // Sembunyikan ProgressBar setelah loading selesai
            binding?.rvUpcomingEvent?.visibility = View.VISIBLE // Tampilkan RecyclerView upcoming setelah loading selesai
        }
    }

    private fun showLoadingFinished(isLoading: Boolean) {
        if (isLoading) {
            binding?.progressBar?.visibility = View.VISIBLE // Tampilkan ProgressBar untuk finished event
            binding?.rvFinishedEvent?.visibility = View.GONE // Sembunyikan RecyclerView finished saat loading
        } else {
            binding?.progressBar?.visibility = View.GONE // Sembunyikan ProgressBar setelah loading selesai
            binding?.rvFinishedEvent?.visibility = View.VISIBLE // Tampilkan RecyclerView finished setelah loading selesai
        }
    }

    private fun setUpcomingEvent(listUpcomingEvent: List<ListEventsItem>) {
        val limitedList =
            if (listUpcomingEvent.size > 5) listUpcomingEvent.take(5) else listUpcomingEvent
        adapterHorizontal.submitList(limitedList)
    }

    private fun setFinishedEvent(listFinishedEvent: List<ListEventsItem>) {
        val limitedList =
            if (listFinishedEvent.size > 5) listFinishedEvent.takeLast(5) else listFinishedEvent
        adapterVertical.submitList(limitedList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}