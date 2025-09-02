package com.sdk.qrscannergenerator.di

import android.app.Application
import androidx.room.Room
import com.sdk.qrscannergenerator.data.local.db.QRDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule{
    @Provides
    @Singleton
    fun provideDatabase(app: Application): QRDatabase {
        return Room.databaseBuilder(
            app,
            QRDatabase::class.java,
            "qr_db"
        ).build()
    }
}