package com.example.fyp_echeckin

import android.app.AlertDialog
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class arrivalsActivity : AppCompatActivity() {

    //datepicker
    private lateinit var arivalDatePicker: DatePicker

    //button
    private lateinit var searchBtn: Button

    //arrivalView
    private lateinit var arrivalView: ListView

    //database
    lateinit var database: DatabaseReference

    //array adapter
    var arrayAdap: ArrayAdapter<String>? = null

    //stores the current date
    var arrivalDay: String? = null

    //the current selected user in the ListView
    private var selectedUser: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arrivals)

        //sets the orientation of the activity to Portrait only
        requestedOrientation =  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Initialize views
        arivalDatePicker = findViewById(R.id.arrival_datePicker)

        //button
        searchBtn = findViewById(R.id.arrival_searchBtn)

        //listview
        arrivalView = findViewById(R.id.arrival_listView)

        // Initialize database reference
        database = FirebaseDatabase.getInstance().getReference("reservations")

        // Set up user list adapter
        arrayAdap = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        arrivalView.adapter = arrayAdap

        // Set up date picker listener by day , month and year
        arivalDatePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            arrivalDay = "${dayOfMonth}/${monthOfYear + 1}/${year}"
        }

        // Set up search button click listener
        searchBtn.setOnClickListener {
            checkArrival()
        }

        //gets the position of the selected position on the listview
        arrivalView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val userClickedOn = arrayAdap?.getItem(position)
            if (userClickedOn != null) {
                displayDetails(userClickedOn)
            }
    }
}
    //check the reference of the source at the bottom
    private fun checkArrival() {
        //checks the database "checkIn" node to see if any has the same date as the arrivals date
        val search = database.orderByChild("checkIn").equalTo(arrivalDay)
        //listener for a single value event
        search.addListenerForSingleValueEvent(object : ValueEventListener {
            //check weather the data specified location is changed
            override fun onDataChange(sShot: DataSnapshot) {
                arrayAdap?.clear()
                //for loop that checks every child in the node for the specific date
                for (userCheckingIn in sShot.children) {
                    val user = userCheckingIn.getValue(User::class.java)
                    if (user != null) {
                        //adds the full name of the user checking in on the specific date
                        val fullName = "${user.firstname} ${user.lastname}"
                        arrayAdap?.add(fullName)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //handles errors
            }
        })
    }

    //Displays the details of the user who's name is clicked on listView
    private fun displayDetails(userOI: String) {

        //searches user who is equal to "fullName" node and cross checks it with the user OI
        val search = database.orderByChild("fullName").equalTo(userOI)
        search.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(sShot: DataSnapshot) {
                for (userClicked in sShot.children) {
                    val user = userClicked.getValue(User::class.java)
                    if (user != null) {
                        selectedUser = userClicked.key
                        userDetailsDialog(user)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun userDetailsDialog(user: User) {


        val dBuilder = AlertDialog.Builder(this)
            //set the dialog layout to "dialog_checkin"
        val dView = layoutInflater.inflate(R.layout.dialog_checkin, null)

        // Initialize initialise the views
        val firstNameTv = dView.findViewById<TextView>(R.id.firstNameTv_checkIn)
        val lastNameTv = dView.findViewById<TextView>(R.id.lastNameTv_checkIn)
        val emailTv = dView.findViewById<TextView>(R.id.emailTv_checkIn)
        val phoneNumb = dView.findViewById<TextView>(R.id.phoneTv_checkIn)
        val address = dView.findViewById<TextView>(R.id.addressTv)
        val creditcardNo = dView.findViewById<TextView>(R.id.creditCardNTv_checkIn)
        val expiryNo = dView.findViewById<TextView>(R.id.expireyNoTv_checkIn)
        val checkIndate = dView.findViewById<TextView>(R.id.checkInDateTv_checkIn)
        val checkOutDate = dView.findViewById<TextView>(R.id.checkOutDateTv_checkIn)
        val roomTypeTv = dView.findViewById<TextView>(R.id.roomTypeTv_checkIn)

        // set the dialog views and assign them
        firstNameTv.text = ( "First Name: " + " " +user.firstname)
        lastNameTv.text = ("Last Name: " + " " + user.lastname)
        emailTv.text = ("Email: " + " " +user.email)
        phoneNumb.text = ("Phone Number: " + " " + user.phoneNumber)
        address.text =("Address: " + " " + user.address)
        creditcardNo.text = ("Credit Card Number: " + " " + user.creditCardNumber)
        expiryNo.text = ("Credit Card Expiry: " + " " + user.creditCardExpiry)
        checkIndate.text = ("Check in Date: " + " " + user.checkIn)
        checkOutDate.text = ("Check Out Date: " + " " + user.checkOutDate)
        roomTypeTv.text = ("Room type: " + " " + user.roomType)

        // Add Confirm button to dialog
        dBuilder.setPositiveButton("Check-In") { dialog, which ->
            // room number entered by the user
            val checkInroomNo = dView.findViewById<EditText>(R.id.roomNumberET).text.toString()

            val database = FirebaseDatabase.getInstance()

            // get the "Inhouse" node
            val inhousedb = database.getReference("Inhouse")

            //chesk to see if the room number the user who is trying to check the guest into, is already occupied
            inhousedb.orderByChild("roomNumber").equalTo(checkInroomNo).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(sShot: DataSnapshot) {
                    if (sShot.exists()) {
                        //a toat to let the user know there is a guest in the room
                        Toast.makeText(this@arrivalsActivity, "There is a guest in the room entered, please select a different room", Toast.LENGTH_SHORT).show()
                        return
                    } else {
                        //checks to see if the room number entered is from 101-110
                        val roomNumbInp = checkInroomNo.toIntOrNull()
                        if (roomNumbInp == null || roomNumbInp < 101 || roomNumbInp > 110) {
                            // if the room number entered isnt from 101 to 110
                            Toast.makeText(this@arrivalsActivity, "Please enter a valid room number from 101 - 110", Toast.LENGTH_SHORT).show()
                            return
                        }

                        // get the "reservations" node
                        val reservationsRef = database.getReference("reservations")

                        // access the details of selected user
                        reservationsRef.child(selectedUser!!).get().addOnSuccessListener { reservationSnapshot ->
                            // move the data to the "Inhouse" node
                            inhousedb.child(selectedUser!!).setValue(reservationSnapshot.value).addOnSuccessListener {
                                // set the room number as the roomnumber entered by the user
                                inhousedb.child(selectedUser!!).child("roomNumber").setValue(checkInroomNo).addOnSuccessListener {
                                    // remove the users data from "reservations" node as its moved to differnt node
                                    reservationsRef.child(selectedUser!!).removeValue().addOnSuccessListener {
                                        // show a success message
                                        Toast.makeText(this@arrivalsActivity, "Check-in successful", Toast.LENGTH_SHORT).show()
                                        checkArrival()
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // handle errors here
                }
            })
        }

        // cancel button added to dialog
        dBuilder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }
        // Set dialog layout
        dBuilder.setView(dView)
        // Show dialog
        val dialog = dBuilder.create()
        dialog.show()
    }

}

/* references for this code
*
* for checkArrival function: https://www.section.io/engineering-education/custom-searching-and-filtering-in-firebase-database-in-android/
*for userDetailsFunction: https://developer.android.com/reference/android/app/AlertDialog.Builder
* moving data from 1 node to another: https://firebase.google.com/docs/database/android/read-and-write
* https://www.appsloveworld.com/nodejs/100/448/moving-or-copying-data-from-one-node-to-another-in-firebase-database
*  */
