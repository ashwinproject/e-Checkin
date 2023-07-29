package com.example.fyp_echeckin

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class otherServiceHub : AppCompatActivity() {

    private lateinit var roomServiceBtn : Button
    private lateinit var reportMaintBtn : Button
    private lateinit var helpBtn : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_service_hub)

        //sets the orientation of the activity to Portrait only
        requestedOrientation =  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        roomServiceBtn = findViewById(R.id.roomServiceBtn)
        reportMaintBtn = findViewById(R.id.guestMaintBtn)
        helpBtn = findViewById(R.id.guestHelpBtn)

        roomServiceBtn.setOnClickListener {
            val intent = Intent(this, roomServiceActivity::class.java)
            startActivity(intent)
        }

        reportMaintBtn.setOnClickListener {
            val intent = Intent(this, guestReportMaintenance::class.java)
            startActivity(intent)
        }

        helpBtn.setOnClickListener {
            val intent = Intent(this, guestHelpActivity::class.java)
            startActivity(intent)
        }

    }
}