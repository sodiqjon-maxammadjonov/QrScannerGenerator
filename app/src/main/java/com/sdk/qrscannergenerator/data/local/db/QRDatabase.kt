package com.sdk.qrscannergenerator.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sdk.qrscannergenerator.data.local.dao.QRDao
import com.sdk.qrscannergenerator.data.local.entity.QREntity

@Database(
    entities = [QREntity::class],
    version = 1,
    exportSchema = false
)
abstract class QRDatabase : RoomDatabase() {
    abstract fun qrDao(): QRDao
}
