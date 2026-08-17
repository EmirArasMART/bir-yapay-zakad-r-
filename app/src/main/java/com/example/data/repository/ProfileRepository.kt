package com.example.data.repository

import com.example.data.local.ProfileDao
import com.example.data.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ProfileRepository(private val profileDao: ProfileDao) {
    val userProfile: Flow<UserProfile?> = profileDao.getUserProfile()

    suspend fun getProfileDirect(): UserProfile = withContext(Dispatchers.IO) {
        val existing = profileDao.getUserProfileDirect()
        if (existing != null) {
            existing
        } else {
            val defaultProfile = UserProfile()
            profileDao.insertOrUpdateProfile(defaultProfile)
            defaultProfile
        }
    }

    suspend fun updateProfile(profile: UserProfile) = withContext(Dispatchers.IO) {
        profileDao.insertOrUpdateProfile(profile)
    }
}
