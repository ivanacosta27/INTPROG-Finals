package com.mab.buwisbuddyph.dataclass

import com.google.firebase.Timestamp

data class User(
    val userID: String,
    val userFullName: String,
    val userEmail: String,
    val userProfileImage: String,
    val timestamp: Timestamp
)
