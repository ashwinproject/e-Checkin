package com.example.fyp_echeckin

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class guestReportMaintenance : AppCompatActivity(), AdapterView.OnItemSelectedListener {


    //spinners for room number and issue selection
    private lateinit var roomNumSp: Spinner
    private lateinit var issueSp: Spinner

    //Edit text to give more description
    private lateinit var descriptionET: EditText

    //submit button
    private lateinit var submitBtn: Button

    private lateinit var database: DatabaseReference

    //store the values selected by the user for the room number and issues in these variables
    private var selectedRoom: String? = ""
    private var selectedIssue: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest_report_maintenance)

        //sets the orientation of the activity to Portrait only
        requestedOrientation =  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        /// Get a reference to the Firebase database
        database = FirebaseDatabase.getInstance().getReference("Maintenance")

        // spinner
        roomNumSp = findViewById(R.id.guestRoomSpinner_Maint)
        issueSp = findViewById(R.id.guestMaintReq_maint)

        //edittext
        descriptionET = findViewById(R.id.guestDecription_maint)

        //button
        submitBtn = findViewById(R.id.guestsubmitBtn_maint)


        // set and initialise roomList and generate room number from 101 to 110
        val roomNumbers = mutableListOf<String>()
        for (x in 101..110) {
            roomNumbers.add("Room $x")
        }

        //populate the spinner and set up room numbers which can be accessed by guest
        val arrayAdp = ArrayAdapter(this, android.R.layout.simple_spinner_item, roomNumbers)
        arrayAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roomNumSp.adapter = arrayAdp
        roomNumSp.onItemSelectedListener = this

        // Set up the issue spinner
        val maintIssues = mutableListOf<String>()
        maintIssues.add("") // add a blank option at the start so the user HAS to pick an issue to submit
        maintIssues.addAll(resources.getStringArray(R.array.maint_Issues))//access and populate the array with the "maint_issues" string Array
        val issueAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, maintIssues)
        issueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        issueSp.adapter = issueAdapter
        issueSp.onItemSelectedListener = this

        // submit button on click listener
        submitBtn.setOnClickListener {
            if (selectedRoom.isNullOrEmpty() || selectedIssue.isNullOrEmpty()) { //checks to make sure the user has selected an option and it not empty
                Toast.makeText(this, "Please select a room number and issue.", Toast.LENGTH_SHORT).show()
            } else {
                storeOnDB()
            }
        }

    }

    // Submit the issue to the Firebase database
    private fun storeOnDB() {
        val description = descriptionET.text.toString().trim()
        //create hashMap object called Newissues
        val newIssues = hashMapOf(

            "room" to selectedRoom,
            "issue" to selectedIssue,
            "description" to description,
            "status" to "Not Done"
        )
        database.push().setValue(newIssues).addOnSuccessListener {//using push method to generate a random key, instead of roomNumber which will allow to store multiple issues to be reported in 1 room
            //reset the values of the selection ad edit text
            roomNumSp.setSelection(0)
            issueSp.setSelection(0)
            descriptionET.setText("")
            Toast.makeText(this, "Maintenance issue reported", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }

    }

    //when an item is selected in a spinner
    override fun onItemSelected(spinnerID: AdapterView<*>, view: View?, pos: Int, id: Long) {
        when (spinnerID.id) {
            R.id.guestRoomSpinner_Maint -> { selectedRoom = spinnerID.getItemAtPosition(pos).toString()
            }
            R.id.guestMaintReq_maint -> { selectedIssue = spinnerID.getItemAtPosition(pos).toString()
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
    }
}