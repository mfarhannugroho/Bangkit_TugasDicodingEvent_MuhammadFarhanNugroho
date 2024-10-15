package di

import android.content.Context
import data.local.database.FavoriteEventRoomDatabase
import data.remote.retrofit.ApiConfig
import data.storage.EventRepository

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = FavoriteEventRoomDatabase.getDatabase(context)
        val dao = database.favoriteEventDao()
        return EventRepository.getInstance(apiService, dao)
    }
}