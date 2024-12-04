package com.example.fitbattleandroid.di

import com.example.fitbattleandroid.data.repository.FetchGeoFenceInfoRepository
import com.example.fitbattleandroid.data.repositoryImpl.FetchGeoFenceInfoRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DiModule {
    @Binds
    abstract fun bindFetchGeoFenceInfoRepository(
        fetchGeoFenceInfoRepositoryImpl: FetchGeoFenceInfoRepositoryImpl,
    ): FetchGeoFenceInfoRepository
}
