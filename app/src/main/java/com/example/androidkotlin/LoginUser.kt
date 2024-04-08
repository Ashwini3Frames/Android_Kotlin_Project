package com.example.androidkotlin

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "loginusers")
data class LoginUser(
    @PrimaryKey(autoGenerate = true)
    val id: Int, // Assuming id is of type Int
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val password: String
)
