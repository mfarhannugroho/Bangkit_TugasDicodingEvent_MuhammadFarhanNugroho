package viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.local.model.FavoriteEvent
import data.remote.response.Event
import data.remote.response.ListEventsItem
import data.storage.EventRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val eventRepository: EventRepository
) : ViewModel() {

    // LiveData untuk menampung pesan error
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // LiveData untuk status loading
    private val _isLoadingUpcoming = MutableLiveData<Boolean>()
    val isLoadingUpcoming: LiveData<Boolean> = _isLoadingUpcoming

    private val _isLoadingFinished = MutableLiveData<Boolean>()
    val isLoadingFinished: LiveData<Boolean> = _isLoadingFinished

    private val _isLoadingDetail = MutableLiveData<Boolean>()
    val isLoadingDetail: LiveData<Boolean> = _isLoadingDetail

    private val _isLoadingSearch = MutableLiveData<Boolean>()
    val isLoadingSearch: LiveData<Boolean> = _isLoadingSearch

    private val _isLoadingFavorite = MutableLiveData<Boolean>()
    val isLoadingFavorite: LiveData<Boolean> = _isLoadingFavorite

    // LiveData untuk event upcoming dan finished
    private val _upcomingEvent = MutableLiveData<List<ListEventsItem>>()
    val upcomingEvent: LiveData<List<ListEventsItem>> = _upcomingEvent

    private val _finishedEvent = MutableLiveData<List<ListEventsItem>>()
    val finishedEvent: LiveData<List<ListEventsItem>> = _finishedEvent

    // LiveData untuk detail event
    private val _detailEvent = MutableLiveData<Event>()
    val detailEvent: LiveData<Event> = _detailEvent

    // LiveData untuk hasil pencarian event
    private val _searchEvent = MutableLiveData<List<ListEventsItem>>()
    val searchEvent: LiveData<List<ListEventsItem>> = _searchEvent

    // LiveData untuk event favorit
    private val _allFavoriteEvents = MutableLiveData<List<FavoriteEvent>>()
    val allFavoriteEvents: LiveData<List<FavoriteEvent>> get() = _allFavoriteEvents

    init {
        // Memanggil event upcoming dan finished ketika ViewModel diinisialisasi
        getUpcomingEvent()
        getFinishedEvent()
        getAllFavoriteEvent()
    }

    // Fungsi untuk mendapatkan event upcoming
    fun getUpcomingEvent() {
        _isLoadingUpcoming.value = true  // Set loading upcoming menjadi true
        viewModelScope.launch {
            try {
                val result = eventRepository.getUpcomingEvent()
                result.onSuccess {
                    _upcomingEvent.value = it
                    clearErrorMessage()
                }.onFailure {
                    _errorMessage.value = it.message
                }
            } finally {
                _isLoadingUpcoming.value = false  // Set loading upcoming menjadi false setelah selesai
            }
        }
    }

    // Fungsi untuk mendapatkan event finished
    fun getFinishedEvent() {
        _isLoadingFinished.value = true  // Set loading finished menjadi true
        viewModelScope.launch {
            try {
                val result = eventRepository.getFinishedEvent()
                result.onSuccess {
                    _finishedEvent.value = it
                    clearErrorMessage()
                }.onFailure {
                    _errorMessage.value = it.message
                }
            } finally {
                _isLoadingFinished.value = false  // Set loading finished menjadi false setelah selesai
            }
        }
    }

    // Fungsi untuk mendapatkan detail event
    // Fungsi untuk mendapatkan detail event
    fun getDetailEvent(id: Int) {
        _isLoadingDetail.value = true  // Set loading detail menjadi true sebelum fetching
        viewModelScope.launch {
            try {
                val result = eventRepository.getDetailEvent(id)
                result.onSuccess {
                    _detailEvent.value = it
                    clearErrorMessage()
                }.onFailure {
                    _errorMessage.value = it.message
                }
            } finally {
                _isLoadingDetail.value = false  // Set loading detail menjadi false setelah fetching selesai
            }
        }
    }


    // Fungsi untuk melakukan pencarian event dengan parameter active
    // Fungsi untuk melakukan pencarian event dengan parameter active
    fun searchEvent(keyword: String, active: Int) {
        _isLoadingSearch.value = true  // Set loading search menjadi true sebelum fetching
        viewModelScope.launch {
            try {
                val result = eventRepository.searchEvent(keyword, active)  // Gunakan parameter active di sini
                result.onSuccess {
                    _searchEvent.value = it
                    clearErrorMessage()
                }.onFailure {
                    _errorMessage.value = it.message
                }
            } finally {
                _isLoadingSearch.value = false  // Set loading search menjadi false setelah fetching selesai
            }
        }
    }

    // Fungsi untuk menambahkan event ke favorit
    fun insertFavoriteEvent(event: FavoriteEvent) {
        viewModelScope.launch {
            val success = eventRepository.insertFavoriteEvent(event)
            if (!success) {
                _errorMessage.value = "Failed to insert favorite event"
            }
        }
    }

    // Fungsi untuk menghapus event dari favorit
    fun deleteFavoriteEvent(event: FavoriteEvent) {
        viewModelScope.launch {
            val success = eventRepository.deleteFavoriteEvent(event)
            if (!success) {
                _errorMessage.value = "Failed to delete favorite event"
            }
        }
    }

    // Fungsi untuk mendapatkan semua event favorit
    fun getAllFavoriteEvent() {
        _isLoadingFavorite.value = true  // Set loading favorit menjadi true sebelum fetching
        eventRepository.getAllFavoriteEvent().observeForever { favoriteEvents ->
            try {
                Log.d("MainViewModel", "Favorite Events: $favoriteEvents")
                _allFavoriteEvents.value = favoriteEvents
                clearErrorMessage()
            } finally {
                _isLoadingFavorite.value = false  // Set loading favorit menjadi false setelah fetching selesai
            }
        }
    }


    // Fungsi untuk mendapatkan event favorit berdasarkan ID
    fun getFavoriteEventById(eventId: Int): LiveData<FavoriteEvent> {
        return eventRepository.getFavoriteEventById(eventId)
    }

    // Menghapus pesan error
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}