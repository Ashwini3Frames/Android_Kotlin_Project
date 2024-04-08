package com.example.androidkotlin

import androidx.room.Entity

import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val gender: String,
    val role:String,
    val profilePicture: ByteArray? // ByteArray to store the image data
)

