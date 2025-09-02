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
            "qr_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideRepository(db: QRDatabase): QRRepository {
        return QrRepositoryImpl(db.qrDao())
    }

    @Provides
    @Singleton
    fun provideInsertQr(repository: QRRepository): InsertQR {
        return InsertQR(repository)
    }

    @Provides
    @Singleton
    fun provideGetHistory(repository: QRRepository): GetHistory {
        return GetHistory(repository)
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
}
