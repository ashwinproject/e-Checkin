package com.example.fyp_echeckin

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    lateinit var mainLoginBtn : Button
    lateinit var mainReservBtn : Button
    lateinit var mainOtherFunBtn : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //sets the orientation of the activity to Portrait only
        requestedOrientation =  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        mainLoginBtn = findViewById(R.id.main_Loginbtn)
        mainReservBtn = findViewById(R.id.main_MakeReservbtn)
        mainOtherFunBtn = findViewById(R.id.main_OtherFcntBtn)
        mainLoginBtn.setOnClickListener {
            val intent = Intent(this, staffLogin ::class.java)
            startActivity(intent)
        }

        mainReservBtn.setOnClickListener {
            val intent = Intent(this, makeReservation::class.java)
            startActivity(intent)
        }

        mainOtherFunBtn.setOnClickListener {
//            val intent = Intent(this, otherServiceHub::class.java)
//            startActivity(intent)
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_verifyguest, null)
            val roomNumberInput = dialogView.findViewById<EditText>(R.id.rNum_check)
            val lastNameInput = dialogView.findViewById<EditText>(R.id.lName_check)

            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Enter Room Number and Last Name")
                .setPositiveButton("Submit") { _, _ ->
                    val roomNumber = roomNumberInput.text.toString()
                    val lastName = lastNameInput.text.toString()
                    checkInHouse(roomNumber, lastName)
                }
                .setNegativeButton("Cancel", null)
            val dialog = dialogBuilder.create()
            dialog.show()
        }
    }

    private fun checkInHouse(roomNumber: String, lastName: String) {
        val inHouseRef = FirebaseDatabase.getInstance().getReference("Inhouse")
        inHouseRef.orderByChild("roomNumber").equalTo(roomNumber).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val user = childSnapshot.getValue(User::class.java)
                    if (user?.lastname == lastName) {
                        // Open the room service activity
                        val intent = Intent(this@MainActivity, otherServiceHub::class.java)
                        startActivity(intent)
                        return
                    }
                }
                // Show an error message if the room number and last name don't match
                Toast.makeText(this@MainActivity, "Invalid room number or last name", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                // Show an error message if there was a problem with the database query
                Toast.makeText(this@MainActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

/** Background reference:
 * https://www.freepik.com/free-photos-vectors/lilac-background : background reference-->
 * */

