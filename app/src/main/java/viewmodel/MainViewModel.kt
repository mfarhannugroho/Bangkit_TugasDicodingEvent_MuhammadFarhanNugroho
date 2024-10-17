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
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

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
        _isLoading.value = true
        viewModelScope.launch {
            val result = eventRepository.getUpcomingEvent()
            _isLoading.value = false
            result.onSuccess {
                _upcomingEvent.value = it
                clearErrorMessage()
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

    // Fungsi untuk mendapatkan event finished
    fun getFinishedEvent() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = eventRepository.getFinishedEvent()
            _isLoading.value = false
            result.onSuccess {
                _finishedEvent.value = it
                clearErrorMessage()
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

    // Fungsi untuk mendapatkan detail event
    fun getDetailEvent(id: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = eventRepository.getDetailEvent(id)
            _isLoading.value = false
            result.onSuccess {
                _detailEvent.value = it
                clearErrorMessage()
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

    // Fungsi untuk melakukan pencarian event dengan parameter active
    fun searchEvent(keyword: String, active: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = eventRepository.searchEvent(keyword, active)  // Gunakan parameter active di sini
            _isLoading.value = false
            result.onSuccess {
                _searchEvent.value = it
                clearErrorMessage()
            }.onFailure {
                _errorMessage.value = it.message
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
        _isLoading.value = true
        eventRepository.getAllFavoriteEvent().observeForever { favoriteEvents ->
            Log.d("MainViewModel", "Favorite Events: $favoriteEvents")
            _isLoading.value = false
            _allFavoriteEvents.value = favoriteEvents
            clearErrorMessage()
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