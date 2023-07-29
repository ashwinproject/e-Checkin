package com.example.fyp_echeckin

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class guestSignup : AppCompatActivity() {

    private lateinit var fbAuth : FirebaseAuth

    lateinit var signupEmailET : EditText
    lateinit var signupPasswordET : EditText
    lateinit var signupConfirmET : EditText

    //button
    lateinit var signupBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest_signup)

        //sets the orientation of the activity to Portrait only
        requestedOrientation =  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        fbAuth = FirebaseAuth.getInstance()

        signupEmailET = findViewById(R.id.signup_email_guest)
        signupPasswordET = findViewById(R.id.signup_password_guest)
        signupConfirmET = findViewById(R.id.signup_confirm_guest)

            //button
        signupBtn = findViewById(R.id.signup_button_guest)

        //onCLickListener
        signupBtn.setOnClickListener {
            signUpGuest()
        }

    }

    //need to add forgot password
    fun signUpGuest() {
        val email = signupEmailET.text.toString().trim()
        val password = signupPasswordET.text.toString().trim()
        val confirmPassword = signupConfirmET.text.toString().trim()

        //checks to see if the email address is valid
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
            return
        }

        // checks to see if the password is atleast 6 characters
        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
            return
        }

        //checks to see if the password matches the confirm password
        if (password != confirmPassword) {
            Toast.makeText(this, "Password and confirmation password does not match", Toast.LENGTH_SHORT).show()
            return
        }

        //checks to see if any of the fields are empty
        if (email.isEmpty() && password.isEmpty() && confirmPassword.isEmpty()){
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        //creates a user with email and password
        fbAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // set the user role
                val userDetails = hashMapOf(
                    "email" to email,//for email node , set the user email
                    "userRole" to "guest" //set the userRole for this account to "guest"
                )

                // save the user details to the DB
                FirebaseDatabase.getInstance().getReference("users").child(task.result?.user?.uid ?: "").setValue(userDetails)
                // opens he login page after the user signs up
                val intent = Intent(this, staffLogin::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Sign up failed, please try again: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

}

/*reference to log in and sign up, i referenced a video by a channel called "Android Knowledge" link: https://www.youtube.com/watch?v=SpSzRgbhTa4&t=258s
*/