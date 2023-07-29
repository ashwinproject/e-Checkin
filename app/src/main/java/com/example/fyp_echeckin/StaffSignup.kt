package com.example.fyp_echeckin

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.fyp_echeckin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class staffSignup : AppCompatActivity() {

    private lateinit var signUpAuth : FirebaseAuth

    lateinit var signupEmailTxt : EditText
    lateinit var signupPasswordTxt : EditText
    lateinit var signupConfirmTxt : EditText

    //button
    lateinit var signupBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_signup)

        //sets the orientation of the activity to Portrait only
        requestedOrientation =  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        signUpAuth = FirebaseAuth.getInstance()

        //initialise editText
        signupEmailTxt = findViewById(R.id.signup_email_staff)
        signupPasswordTxt = findViewById(R.id.signup_password_staff)
        signupConfirmTxt = findViewById(R.id.signup_confirm_staff)

        //initialise button
        signupBtn = findViewById(R.id.signup_button_staff)

        //set button on click listener
        signupBtn.setOnClickListener {
            signUp()
                //calls the signup function
        }
    }

    private fun signUp() {
        val emailInp = signupEmailTxt.text.trim().toString()
        val passwordInp = signupPasswordTxt.text.trim().toString()
        val confirmPassword = signupConfirmTxt.text.trim().toString()

        if (!Patterns.EMAIL_ADDRESS.matcher(emailInp).matches()) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if the password least 6 characters long
        if (passwordInp.length < 6) {
            Toast.makeText(this, "Password must be 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

            //check if password matches confirm password
        if (passwordInp != confirmPassword) {
            Toast.makeText(this, "Password and confirmation do not match, please try again", Toast.LENGTH_SHORT).show()
            return
        }

        //check if the fields are empty
        if (emailInp.isEmpty() && passwordInp.isEmpty() && confirmPassword.isEmpty()){
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }


        signUpAuth.createUserWithEmailAndPassword(emailInp, passwordInp).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Add userRole field to user info
                val user = hashMapOf(
                    "email" to emailInp,
                    "userRole" to "front_office" //user role is set to be front office when the user signs up
                )

                // Save user info to Firebase Realtime Database
                FirebaseDatabase.getInstance().getReference("users").child(task.result?.user?.uid ?: "").setValue(user)

                // Redirect to login screen after signUp
                val intent = Intent(this, staffLogin::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Sign up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

/** Reference for log in and sign up
 * https://www.geeksforgeeks.org/login-and-registration-in-android-using-firebase-in-kotlin/
 * reference to log in and sign up, i referenced a video by a channel called "Android Knowledge" link: https://www.youtube.com/watch?v=SpSzRgbhTa4&t=258s
 * **/