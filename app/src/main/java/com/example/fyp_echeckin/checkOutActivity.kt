package com.example.fyp_echeckin

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

class checkOutActivity : AppCompatActivity() {

    //last name edit text
    private lateinit var lNameET : EditText
    private lateinit var roomNumbET : EditText

    //button
    private lateinit var searchBtn : Button

    //listview
    private lateinit var coListView : ListView

    //database
    private lateinit var database: DatabaseReference

    //array adapter
    var arrayAdap: ArrayAdapter<String>? = null

    //store the currently selected user from listView
    private var selectedUser: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_out)

        //sets the orientation of the activity to Portrait only
        requestedOrientation =  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //edit texts
        lNameET = findViewById(R.id.lastNameEditTextCO)
        roomNumbET = findViewById(R.id.roomNumberEditTextCO)

        //search button
        searchBtn =  findViewById(R.id.searchBtnCO)

        //listview
        coListView = findViewById(R.id.userListViewCO)

        //database path of the node
        database = FirebaseDatabase.getInstance().getReference("Inhouse")

        // Set up user list adapter LAYOUT
        arrayAdap = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        coListView.adapter = arrayAdap

        searchBtn.setOnClickListener {
            
            //get the user input for last name and roomNumber and store them in the variable
            val lNameET = lNameET.text.trim().toString()
            val roomNumET = roomNumbET.text.trim().toString()

            //checks to see if the user is searching by last name or room number
            if (lNameET.isNotEmpty()){
                searchByLastName(lNameET)
            }else if(roomNumET.isNotEmpty()){
                searchByRoomNumber(roomNumET)
            }
        }

        //get the current position of the selected person on the listView
        coListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val userClickedOn = arrayAdap?.getItem(position)
            if (userClickedOn != null) {
                displayUserDetails(userClickedOn)
            }
        }
    }

    //this function is called when the user searches by room number
    private fun searchByRoomNumber(roomNumber: String) {
        val search = database.orderByChild("roomNumber").equalTo(roomNumber)
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
                    //if there is no guest found with that name
                    Toast.makeText(applicationContext, "No guests found or enter the correct room number. 101 -110", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    private fun searchByLastName(lastNameInp: String) {
        val search = database.orderByChild("lastname").startAt(lastNameInp).endAt(lastNameInp + "\uf8ff")
        search.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(sShot: DataSnapshot) {
                arrayAdap?.clear()
                if (sShot.hasChildren()) {
                    for (reservationSnapshot in sShot.children) {
                        val user = reservationSnapshot.getValue(User::class.java)
                        if (user != null) {
                            val fullName = "${user.firstname} ${user.lastname}"
                            arrayAdap?.add(fullName)
                            searchByLastName(lastNameInp = String())
                        }
                    }
                }else{
                    Toast.makeText(applicationContext, "No guests found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun displayUserDetails(userFullName: String) {

        val search = database.orderByChild("fullName").equalTo(userFullName)
        search.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(sShot: DataSnapshot) {
                for (reservationSnapshot in sShot.children) {
                    val user = reservationSnapshot.getValue(User::class.java)
                    if (user != null) {
                        selectedUser = reservationSnapshot.key
                        UserDetailsDialog(user)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }

    private fun UserDetailsDialog(user: User) {

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

        dBuilder.setPositiveButton("Checkout") { dialog, which ->

            // get database reference
            val database = FirebaseDatabase.getInstance()

            // "inhouse"
            val inhouseNode = database.getReference("Inhouse")

            inhouseNode.child(selectedUser!!).get().addOnSuccessListener { sShot ->
                // get data from the "inhouse" node
                val user = sShot.getValue(User::class.java)

                // change the users status to checked out to update housekeeping
                user?.status = "Checked Out"

                // get reference to "checkedOut" node
                val checkOutNode = database.getReference("checkOut")
                checkOutNode.child(selectedUser!!).setValue(user).addOnSuccessListener {
                    // remove the user from inhouse NODE
                    inhouseNode.child(selectedUser!!).removeValue().addOnSuccessListener {
                        //toast to display to the user know checkOut has been successful
                        Toast.makeText(this, "Check-Out successful", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            searchByLastName(lastNameInp = String())
        }

        // cancel button in the dialog
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
* for search by room number and last name function: https://www.section.io/engineering-education/custom-searching-and-filtering-in-firebase-database-in-android/
*for userDetailsFunction: https://developer.android.com/reference/android/app/AlertDialog.Builder
* moving data from 1 node to another: https://firebase.google.com/docs/database/android/read-and-write
*  */