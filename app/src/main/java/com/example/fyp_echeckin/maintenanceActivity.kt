package com.example.fyp_echeckin

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class maintenanceActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    //spinners for room number and issue selection
    private lateinit var roomNumSp: Spinner
    private lateinit var issueSp: Spinner

    //Edit text to give more description
    private lateinit var descriptionET: EditText

    //submit button
    private lateinit var submitBtn: Button

    private lateinit var database: DatabaseReference

    //listview to display maintenance issues
    private lateinit var maintIssuesLV: ListView

    //store the values selected by the user for the room number and issues in these variables
    private var selectedRoom: String? = ""
    private var selectedIssue: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maintenance)

        //sets the orientation of the activity to Portrait only
        requestedOrientation =  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        /// Get a reference to the Firebase database
        database = FirebaseDatabase.getInstance().getReference("Maintenance")

        // spinner
        roomNumSp = findViewById(R.id.roomSpstaff_Maint)
        issueSp = findViewById(R.id.issueSpStaff_maint)

        //edittext
        descriptionET = findViewById(R.id.staffDescription_maint)

        //button
        submitBtn = findViewById(R.id.staffSubmitBtn_maint)

        //listView
        maintIssuesLV = findViewById(R.id.notDoneListView_staffMaint)

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
        updateLV()
    }

    private fun updateLV() {
        //Displays all the maintenance issues yet to be fixed
        database.orderByChild("status").equalTo("Not Done").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val displayIssues = mutableListOf<String>()
                for (sShot in dataSnapshot.children) {// access the list of all the values to to store in the variable to save in the array adapter
                    val roomNumb = sShot.child("room").getValue(String::class.java)
                    val issueToBeFixed = sShot.child("issue").getValue(String::class.java)
                    val issueDescription = sShot.child("description").getValue(String::class.java)
                    val issueStatus = sShot.child("status").getValue(String::class.java)
                    displayIssues.add("$roomNumb - $issueToBeFixed - $issueDescription - $issueStatus")//adding the issues from the variables to the adapter
                }
                val notDoneAdap = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, displayIssues)
                maintIssuesLV.adapter = notDoneAdap

                // onCLick listener fo the listView
                maintIssuesLV.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
//                    val selectedItem = maintIssuesLV.getItemAtPosition(position) as String
                    val issue = dataSnapshot.children.elementAt(position).key //contains the key of the child node at the position
                    val dialogAlert = AlertDialog.Builder(this@maintenanceActivity).create()
                    dialogAlert.setTitle("Update the status if this Issue")
                    dialogAlert.setMessage("Do you want to mark this issue as done?")
                    dialogAlert.setButton(AlertDialog.BUTTON_POSITIVE, "Yes") { _, _ ->
                        // Update the issue status to "Done" from "Not_Done" in database
                        if (issue != null) {
                            database.child(issue).child("status").setValue("Done").addOnSuccessListener {
                                Toast.makeText(this@maintenanceActivity, "Issue has been solved, Well done!", Toast.LENGTH_SHORT).show()
                                updateLV()
                            }.addOnFailureListener {
                                Toast.makeText(this@maintenanceActivity, "Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    dialogAlert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    dialogAlert.show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
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
            updateLV()
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
            R.id.roomSpstaff_Maint -> { selectedRoom = spinnerID.getItemAtPosition(pos).toString()
            }
            R.id.issueSpStaff_maint -> { selectedIssue = spinnerID.getItemAtPosition(pos).toString()
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
    }

}
/** Array adapters and list view reference
 * https://www.geeksforgeeks.org/android-listview-in-kotlin/
 * 
 * Adapter view reference:AdapterView.OnItemSelectedListener
 * https://developer.android.com/reference/kotlin/android/widget/AdapterView.OnItemSelectedListener**/

/** reference to research and display all issues from database "Maintenance" node
 * Reference:https://firebase.google.com/docs/database/android/read-and-write
 * second reference: https://stackoverflow.com/questions/56199072/how-to-read-data-from-firebase-in-android-studio-using-kotlin**/