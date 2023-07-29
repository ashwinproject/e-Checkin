package com.example.fyp_echeckin

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class inHouseActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var arrayAdap: ArrayAdapter<String>

    //edit text
    private lateinit var lNameET : EditText
    private lateinit var roomNumET : EditText

    //listView
    private lateinit var resultLV : ListView

    //button
    private lateinit var inHouseSearchBtn : Button

    private var selectedUser: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_house)

        //sets the orientation of the activity to Portrait only
        requestedOrientation =  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //get the database reference
        database = FirebaseDatabase.getInstance().getReference("Inhouse")

        lNameET = findViewById(R.id.lastNameEditTextIH)
        roomNumET = findViewById(R.id.roomNumberEditTextIH)

        //initialise button
        inHouseSearchBtn = findViewById(R.id.searchButtonIH)

        //initialise the lv with array adapter
        arrayAdap = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        resultLV = findViewById(R.id.userListViewIH)
        resultLV.adapter = arrayAdap

        //set buttonOn click listener
        inHouseSearchBtn.setOnClickListener {
            //get user input for last name and room Number
            val lastNameUser =  lNameET.text.toString().trim()
            val roomNumberGuest = roomNumET.text.toString().trim()

            //if room number not empty search by room number function
            if (roomNumberGuest.isNotEmpty()) {
                searchByRoomNumber(roomNumberGuest)

            } else if (lastNameUser.isNotEmpty()) {//search by last name
                searchByLastName(lastNameUser)
            } else if (roomNumberGuest.isEmpty() && lastNameUser.isEmpty()){ //if both the fields are empty call the showAllGuest function
                showAllGuest()

            }

        }

        //onItemClick listener to display the selected user by position
        resultLV.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val userClickedOn = arrayAdap?.getItem(position)
            if (userClickedOn != null) {
                displayUser(userClickedOn)
            }
        }

    }

    private fun showAllGuest() {//searches the guest inHouse guest
        val search = database.orderByChild("roomNumber")
        search.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(sShot: DataSnapshot) {
                arrayAdap?.clear()
                if (sShot.hasChildren()) {
                    for (reservationSnapshot in sShot.children) {
                        val user = reservationSnapshot.getValue(User::class.java)
                        if (user != null) {
                            val fullName = "${user.firstname} ${user.lastname}"
                            arrayAdap?.add(fullName)
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, "No inHouse guests found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //handles error
            }
        })
    }

    //display the inHouse guest
    private fun displayUser(userOI: String) {
        val search = database.orderByChild("fullName").equalTo(userOI)
        search.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (reservationSnapshot in dataSnapshot.children){
                    val user = reservationSnapshot.getValue(User::class.java)
                    if (user != null){
                        selectedUser = reservationSnapshot.key
                        showUserDetails(user)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //error
            }
        })

    }

    //search by last name of the guest
    private fun searchByLastName(lastName: String) {

        val search = database.orderByChild("lastname").equalTo(lastName)
        print(lastName)
        search.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(sShot: DataSnapshot){
                arrayAdap?.clear()

                if (sShot.hasChildren()) {

                    for (reservationSnapshot in sShot.children) {
                        val user = reservationSnapshot.getValue(User::class.java)
                        if (user != null) {
                            val fullName = "${user.firstname} ${user.lastname}"
                            arrayAdap?.add(fullName)
                        }
                    }
                }else{
                    Toast.makeText(applicationContext, "Guest not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError){
                Log.e(ContentValues.TAG, "onCancelled", databaseError.toException())
            }
        })


    }

    private fun searchByRoomNumber(roomNumber: String) {

        val query = database.orderByChild("roomNumber").equalTo(roomNumber)
        query.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot){
                arrayAdap?.clear()
                if(dataSnapshot.hasChildren()){
                    for (reservationSnapshot in dataSnapshot.children){
                        val user = reservationSnapshot.getValue(User::class.java)
                        if (user != null){
                            val fullName = "${user.firstname} ${user.lastname}"
                            arrayAdap?.add(fullName)
                        }
                    }
                }else {
                    Toast.makeText(applicationContext, "Room not found", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(databaseError: DatabaseError){
                Log.e(ContentValues.TAG, "onCancelled", databaseError.toException())
            }
        })

    }

    private fun showUserDetails(user: User) {
        val dBuilder = AlertDialog.Builder(this)
        val dView = layoutInflater.inflate(R.layout.inhouseandco, null)

        // Initialize dialog views
        val fullNameTv = dView.findViewById<TextView>(R.id.fullNameTvIH)
        val emailTv = dView.findViewById<TextView>(R.id.emailTVIH)
        val phoneNumb = dView.findViewById<TextView>(R.id.phoneTvIH)
        val addressTv = dView.findViewById<TextView>(R.id.addressTvIH)
        val checkInDateTv = dView.findViewById<TextView>(R.id.checkinIH)
        val checkOutDateTv = dView.findViewById<TextView>(R.id.checkOutIH)
        val roomTypeTv = dView.findViewById<TextView>(R.id.roomTypeTvIH)
        val totPriceTv = dView.findViewById<TextView>(R.id.totalPriceTvIH)
        val creditcardNo = dView.findViewById<TextView>(R.id.ccTvIH)
        val expiryNo = dView.findViewById<TextView>(R.id.ccExpiryTvIH)
        val ccCVVTv = dView.findViewById<TextView>(R.id.cvvTvIH)
        val noOfGuestTv = dView.findViewById<TextView>(R.id.noOfGuestTVIH)
        val roomNumbTv = dView.findViewById<TextView>(R.id.roomTypeTvIH)


        fullNameTv.text = ( "Name: " + " " +user.fullName)
        emailTv.text = ("Email: " + " " +user.email)
        phoneNumb.text = ("Phone Number: " + " " + user.phoneNumber)
        addressTv.text =("Address: " + " " + user.address)
        checkInDateTv.text = ("Check in Date: " + " " + user.checkIn)
        checkOutDateTv.text = ("Check Out Date: " + " " + user.checkOutDate)
        roomTypeTv.text = ("Room Type: " + " " + user.roomType)
        totPriceTv.text = ("Total Price : " + " " + "£" + user.totalPrice)
        creditcardNo.text = ("Credit Card Number: " + " " + user.creditCardNumber)
        expiryNo.text = ("Credit Card Expiry: " + " " + user.creditCardExpiry)
        ccCVVTv.text = ("CVV number: " + " " + user.cvvNo)
        noOfGuestTv.text = ("Number of guests: " + " " + user.noOfGuest)
        roomNumbTv.text = ("Room Number: " + " " + user.roomNumber)


        dBuilder.setPositiveButton("Okay"){ dialog, which ->
            dialog.cancel()
        }

        // Set layout
        dBuilder.setView(dView)
        // create and show the dialog
        val dialog = dBuilder.create()
        dialog.show()


    }

}

/* references for this code
*
* for search by last name and room number function: https://www.section.io/engineering-education/custom-searching-and-filtering-in-firebase-database-in-android/
*for userDetailsFunction: https://developer.android.com/reference/android/app/AlertDialog.Builder
* moving data from 1 node to another: https://firebase.google.com/docs/database/android/read-and-write
* dialog box: https://developer.android.com/develop/ui/views/components/dialogs
*  */