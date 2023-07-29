package com.example.fyp_echeckin

data class GuestRequest(
    val department: String = "",
    val request: String = "",
    val roomNumber: String = "",
    val status : String = "Not Done"
)
