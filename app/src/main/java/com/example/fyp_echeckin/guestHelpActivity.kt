package com.example.fyp_echeckin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase

class guestHelpActivity : AppCompatActivity() {


    //initialise radio button
    private lateinit var departmentRdBtn: RadioGroup

    //initialise issues spinner
    private lateinit var issuesSP: Spinner

    //initialise editText
    private lateinit var roomNumET: EditText

    //request submit button
    private lateinit var submitBtn: Button

    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest_help)

        // Initialize views

        //radio button
        departmentRdBtn = findViewById(R.id.departments_radioBtn)

        //spinner
        issuesSP = findViewById(R.id.guestReq_sp)

        //edit text
        roomNumET = findViewById(R.id.guestIssue_roomNumET)

        //submit button
        submitBtn = findViewById(R.id.guestIssue_submitBtn)

        // Initialize Firebase database
        database = FirebaseDatabase.getInstance()

        // Initialize adapter layout
        val arrayAdap = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item,
        )

        arrayAdap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        issuesSP.adapter = arrayAdap

        // check which department has been selected
        departmentRdBtn.setOnCheckedChangeListener { _, checkedId ->
            val departOptions = when (checkedId) {
                //if front desk request is selected
                R.id.rButton_reception -> frontDeskOptions()
                //if housekeeping option is selected
                R.id.rButton_hsk -> hskOptions()
                //if concierge option is selected
                R.id.rButton_concierge -> conciergeOptions()
                else -> frontDeskOptions()
            }
            arrayAdap.clear()
            arrayAdap.addAll(departOptions) //adds the options to the listview
        }

        // set on clickListener to call this function
        submitBtn.setOnClickListener {
            submitToDB()
        }

    }

    //function used to save data to database
    private fun submitToDB() {

        //to get user input
        val guestRoom = roomNumET.text.toString()
        //to set the user input to int
        val newGuestRoom = guestRoom.toIntOrNull()

        //check if the guest input is empty or not 101-110
        if (newGuestRoom == null || newGuestRoom < 101 || newGuestRoom > 110) {
            Toast.makeText(this, "Enter a room between 101-110", Toast.LENGTH_SHORT).show()
        } else {

            val department = when (departmentRdBtn.checkedRadioButtonId) {
                R.id.rButton_reception -> "Reception"
                R.id.rButton_hsk -> "Housekeeping"
                R.id.rButton_concierge -> "Concierge"
                else -> ""
            }
            //the issue selected by the guest
            val issueSelected = issuesSP.selectedItem.toString()

            //checks to see if the fields are empty
            if (department.isEmpty() || issueSelected.isEmpty() || guestRoom.isEmpty()) {
                Toast.makeText(
                    this, "Please fill out all the fields", Toast.LENGTH_SHORT
                ).show()
                return
            }

            // Create object to save it to Firebase database
            val guestIssues =
                GuestRequest(department, issueSelected, guestRoom, status = "Not done")
            //gets reference to guestreqest node
            val guestReq_ref = database.reference.child("guestRequest")
            //save it to db
            val submitIssue = guestReq_ref.push()
            submitIssue.setValue(guestIssues)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Issue submitted successfully, A staff member will be with you shortly",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    //a toast to let the user know issue has been submitted
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error ", Toast.LENGTH_SHORT).show()
                }
        }

    }

    private fun frontDeskOptions(): List<String> {
        return listOf(
            "Late check-out",
            "Extend Booking",
            "Room change",
            "Amend booking",
            "Others"
        )
    }

    private fun hskOptions(): List<String> {
        return listOf(
            "Extra Towel",
            "Extra Toiletries",
            "Request Bedding change",
            "Extra Water Bottles",
            "Iron & iron board",
            "Others"
        )
    }

    private fun conciergeOptions(): List<String> {
        return listOf(
            "Taxi booking",
            "Tour Information",
            "Restaurant Booking",
            "Luggage pick up",
            "Others"
        )
    }

}

/**To read and write from database:
 * https://theengineerscafe.com/save-and-retrieve-data-firebase-android/.
 * I took inspiration from this code
 * **/