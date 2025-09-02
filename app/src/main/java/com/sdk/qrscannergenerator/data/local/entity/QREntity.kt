package com.sdk.qrscannergenerator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_history")
data class QREntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
    val createdAt: Long = System.currentTimeMillis(),
    val content: String,
    val type: String,
    val isGenerated: Boolean
)