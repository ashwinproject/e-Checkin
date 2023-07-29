package com.example.fyp_echeckin

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date

class homeHub : AppCompatActivity() {

    lateinit var currentDateTxt : TextView

    //buttons
    lateinit var frontDeskBtn : Button
    lateinit var reservationBtn : Button
    lateinit var maintenanceBtn : Button
    lateinit var housekeepBtn : Button
    lateinit var makeUserBtn : Button
    lateinit var guestRequestsBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_hub)

        //sets the orientation of the activity to Portrait only
        requestedOrientation =  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //front desk button
       frontDeskBtn = findViewById(R.id.homeHubFrontOffice)

        //reservation button
        reservationBtn = findViewById(R.id.reservationBtnHome)

        //maintenance button
        maintenanceBtn = findViewById(R.id.maintBtnHome)

        //housekeeping button
        housekeepBtn = findViewById(R.id.houseBtnHome)

        //make user button
        makeUserBtn = findViewById(R.id.makeUserBtnHome)

        //guest request button
        guestRequestsBtn = findViewById(R.id.guestRequestBtnHome)

        currentDateTxt = findViewById(R.id.currentDate)
        val dateFormat = SimpleDateFormat("dd/M/yyyy")
        val  todaysDate = dateFormat.format(Date())
        currentDateTxt.text = todaysDate

        frontDeskBtn.setOnClickListener {
            val intent = Intent(this, frontDeskHub::class.java)
            startActivity(intent)
        }

        reservationBtn.setOnClickListener {
            val intent = Intent(this, makeReservation::class.java)
            startActivity(intent)
        }

        maintenanceBtn.setOnClickListener {
            val intent = Intent(this, maintenanceActivity::class.java)
            startActivity(intent)
        }

        housekeepBtn.setOnClickListener {
            val intent = Intent(this, houseKeepingActivity::class.java)
            startActivity(intent)
        }

        makeUserBtn.setOnClickListener {
            val intent = Intent(this, staffSignup::class.java)
            startActivity(intent)
        }

        guestRequestsBtn.setOnClickListener {
            val intent = Intent(this, guestRequestsActivity::class.java)
            startActivity(intent)
        }

    }
}
/*to display the current date and time: https://stackoverflow.com/questions/47006254/how-to-get-current-local-date-and-time-in-kotlin */