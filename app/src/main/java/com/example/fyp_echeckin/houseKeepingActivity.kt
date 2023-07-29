package com.example.fyp_echeckin

import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class houseKeepingActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase

    //list of all the room numbers
    private val hotelRooms = mutableListOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_house_keeping)

        //sets the orientation of the activity to Portrait only
        requestedOrientation =  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // reference to DB
        database = FirebaseDatabase.getInstance()

        // Assign TextViews for all the rooms
        hotelRooms.add(findViewById(R.id.r1_status))
        hotelRooms.add(findViewById(R.id.r2_status))
        hotelRooms.add(findViewById(R.id.r3_status))
        hotelRooms.add(findViewById(R.id.r4_status))
        hotelRooms.add(findViewById(R.id.r5_status))
        hotelRooms.add(findViewById(R.id.r6_status))
        hotelRooms.add(findViewById(R.id.r7_status))
        hotelRooms.add(findViewById(R.id.r8_status))
        hotelRooms.add(findViewById(R.id.r9_status))
        hotelRooms.add(findViewById(R.id.r10_status))

        // Retrieve guest information from the database
        val inhouseDb = database.getReference("Inhouse")

        inhouseDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                hotelRooms.forEachIndexed { number, tV ->
                    //set the label to the 101-110
                    val currentRoom = if (number == 9) "110" else "10${number + 1}"
                    //checks if there are guest in any room from 101-110
                    val roomNumbRef = inhouseDb.orderByChild("roomNumber").equalTo(currentRoom)

                    roomNumbRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(sShot: DataSnapshot) {
                            val occupied = sShot.children.firstOrNull()?.getValue(User::class.java)
                            //if the room is occupied change room status.
                            changeRoomStatus(tV, occupied)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            //error
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //handle error
            }
        })
    }

    //call this function to change room status TextView
    private fun changeRoomStatus(roomStatus: TextView, guest: User?) {
        //if there is no guest in the room
        if (guest == null) {
            roomStatus.text = "Vacant"
            roomStatus.setTextColor(Color.GREEN)
        } else {
            //if there is a guest in the room 
            roomStatus.text = "Occupied"
            roomStatus.setTextColor(Color.RED)
        }
    }
}
/**to create the mutable list:
 * https://www.javatpoint.com/kotlin-mutablelist-mutablelistof
 * **/