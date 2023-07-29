package com.example.fyp_echeckin

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class frontDeskHub : AppCompatActivity() {
    //buttons
    lateinit var arrivalBtn : Button
    lateinit var inhouseBtn : Button
    lateinit var checkOutBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_front_desk_hub)

        //sets the orientation of the activity to Portrait only
        requestedOrientation =  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        arrivalBtn = findViewById(R.id.arrivalsBtnHH)
        inhouseBtn = findViewById(R.id.inHouseBtnHH)
        checkOutBtn = findViewById(R.id.checkOutBtnHH)

        //set onClick listener for all the buttons to open a certain activity
        arrivalBtn.setOnClickListener {
            val intent = Intent(this, arrivalsActivity::class.java)
            startActivity(intent)
        }

        inhouseBtn.setOnClickListener {
            val intent = Intent(this, inHouseActivity::class.java)
            startActivity(intent)
        }

        checkOutBtn.setOnClickListener {
            val intent = Intent(this, checkOutActivity::class.java)
            startActivity(intent)
        }


    }
}