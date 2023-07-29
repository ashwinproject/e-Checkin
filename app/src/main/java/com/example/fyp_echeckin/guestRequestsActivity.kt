package com.example.fyp_echeckin

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class guestRequestsActivity : AppCompatActivity() {

    private lateinit var guestReqLV : ListView
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest_requests)

        //initialise listview
        guestReqLV =  findViewById(R.id.staffGuestRequest_notDone_LV)

        //sets the orientation of the activity to Portrait only
        requestedOrientation =  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        /// Get a reference to the Firebase database
        database = FirebaseDatabase.getInstance().getReference("guestRequest")

        updateLV()
        //function to update the listView
    }

    private fun updateLV() {
        //searches database to show the issue status which are "Not done"
        database.orderByChild("status").equalTo("Not done").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(sShot: DataSnapshot) {

                val displayIssues = mutableListOf<String>()

                for (sShot in sShot.children) {
                    // access the list of all the values to to store in the variable to save in the array adapter
                    val roomNumb = sShot.child("roomNumber").getValue(String::class.java)
                    val department = sShot.child("department").getValue(String::class.java)
                    val requests = sShot.child("request").getValue(String::class.java)
                    val issueStatus = sShot.child("status").getValue(String::class.java)
                    displayIssues.add("$roomNumb - $department - $requests - $issueStatus")//adding the issues from the variables to the adapter
                }
                val arayAdap = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, displayIssues)
                guestReqLV.adapter = arayAdap

                // onCLick listener fo the listView
                guestReqLV.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    val issue = sShot.children.elementAt(position).key //contains the key of the child node at the position
                    val dialogAlert = AlertDialog.Builder(this@guestRequestsActivity).create()
                    dialogAlert.setTitle("Update the status if this Issue")
                    dialogAlert.setMessage("Do you want to mark this issue as done?")
                    dialogAlert.setButton(AlertDialog.BUTTON_POSITIVE, "Yes") { _, _ ->
                        // Update the issue status to "Done" from "Not Done" in database
                        if (issue != null) {
                            database.child(issue).child("status").setValue("Done").addOnSuccessListener {
                                Toast.makeText(this@guestRequestsActivity, "Issue has been solved, Well done!", Toast.LENGTH_SHORT).show()
                                updateLV()
                            }.addOnFailureListener {
                                Toast.makeText(this@guestRequestsActivity, "Failed", Toast.LENGTH_SHORT).show()
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
}