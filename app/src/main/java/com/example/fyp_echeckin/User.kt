package com.example.fyp_echeckin

data class User(
    val firstname: String? = null,
    val lastname: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val address: String? = null,
    val creditCardNumber: String? = null,
    val creditCardExpiry: String? = null,
    val cvvNo: String? = null,
    val checkIn: String? = null,
    val checkOutDate: String? = null,
    val noOfGuest: String = " ",
    val roomType: String? = null,
    val bedDetails: String? = null,
    val totalPrice: Int? = null,
    val roomNumber: String? = null,
    var status: String? = null,
    val fullName: String = "$firstname $lastname"

)
{
    val userRole: String? = null
}
