package com.sdk.qrscannergenerator.di

import android.app.Application
import androidx.room.Room
import com.sdk.qrscannergenerator.data.local.db.QRDatabase
import com.sdk.qrscannergenerator.domain.repository.QRRepository
import com.sdk.qrscannergenerator.domain.repositoryimpl.QrRepositoryImpl
import com.sdk.qrscannergenerator.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): QRDatabase {
        return Room.databaseBuilder(
            app,
            QRDatabase::class.java,
            QRDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideRepository(db: QRDatabase): QRRepository {
        return QrRepositoryImpl(db.qrDao())
    }

    // Use Cases
    @Provides
    @Singleton
    fun provideInsertQR(repository: QRRepository): InsertQR {
        return InsertQR(repository)
    }

    @Provides
    @Singleton
    fun provideGetHistory(repository: QRRepository): GetHistory {
        return GetHistory(repository)
    }

    @Provides
    @Singleton
    fun provideGetGeneratedHistory(repository: QRRepository): GetGeneratedHistory {
        return GetGeneratedHistory(repository)
    }

    @Provides
    @Singleton
    fun provideGetScannedHistory(repository: QRRepository): GetScannedHistory {
        return GetScannedHistory(repository)
    }

    @Provides
    @Singleton
    fun provideSearchHistory(repository: QRRepository): SearchHistory {
        return SearchHistory(repository)
    }

    @Provides
    @Singleton
    fun provideGetQRById(repository: QRRepository): GetQRById {
        return GetQRById(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteQr(repository: QRRepository): DeleteQr {
        return DeleteQr(repository)
    }

    @Provides
    @Singleton
    fun provideClearHistory(repository: QRRepository): ClearHistory {
        return ClearHistory(repository)
    }

    @Provides
    @Singleton
    fun provideClearGeneratedHistory(repository: QRRepository): ClearGeneratedHistory {
        return ClearGeneratedHistory(repository)
    }

    @Provides
    @Singleton
    fun provideClearScannedHistory(repository: QRRepository): ClearScannedHistory {
        return ClearScannedHistory(repository)
    }

    @Provides
    @Singleton
    fun provideGetHistoryCount(repository: QRRepository): GetHistoryCount {
        return GetHistoryCount(repository)
    }
}