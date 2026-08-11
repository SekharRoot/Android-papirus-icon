package com.example.data

import kotlinx.coroutines.flow.Flow

class IconRepository(private val iconDao: IconDao) {
    val allRequests: Flow<List<RequestEntity>> = iconDao.getAllRequests()
    val allFavorites: Flow<List<FavoriteEntity>> = iconDao.getAllFavorites()

    suspend fun insertRequest(packageName: String, appName: String) {
        iconDao.insertRequest(RequestEntity(packageName, appName))
    }

    suspend fun insertRequests(requests: List<RequestEntity>) {
        iconDao.insertRequests(requests)
    }

    suspend fun deleteRequest(packageName: String) {
        iconDao.deleteRequestByPackage(packageName)
    }

    suspend fun clearAllRequests() {
        iconDao.deleteAllRequests()
    }

    suspend fun addFavorite(iconId: String) {
        iconDao.insertFavorite(FavoriteEntity(iconId))
    }

    suspend fun removeFavorite(iconId: String) {
        iconDao.deleteFavoriteById(iconId)
    }
}
