package com.example.fyp_echeckin

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.fyp_echeckin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class staffLogin : AppCompatActivity() {

    //editTexts
    lateinit var emailTxtLogin: EditText
    lateinit var passTxtLogin: EditText

    //button
    lateinit var loginBtnLogin: Button

    //redirect text
    lateinit var signUpTxtLogin: TextView

    private lateinit var auth: FirebaseAuth // firebase auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_login)

        //sets the orientation of the activity to Portrait only
        requestedOrientation =  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //initialise firebase auth
        auth = FirebaseAuth.getInstance()

        //edit text initialise
        emailTxtLogin = findViewById(R.id.login_email_staff)
        passTxtLogin = findViewById(R.id.login_password_staff)

        //button initialise
        loginBtnLogin = findViewById(R.id.login_button_staff)

        signUpTxtLogin = findViewById(R.id.signUpRedirect_staff) //open guest signup page

        loginBtnLogin.setOnClickListener {
            val emailinp = emailTxtLogin.text.toString()
            val passwordinp = passTxtLogin.text.toString()

            if (emailinp.isEmpty() || passwordinp.isEmpty()) {
                Toast.makeText(this, "Email or Password cannot be empty, please enter your email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
                //makes sure the email or password field is not empty

            }

            if (!Patterns.EMAIL_ADDRESS.matcher(emailinp).matches()) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
               //makes sure the user enters the valid email
            }

            if (passwordinp.length < 6) {
                Toast.makeText(this, "The password must be atleast 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
                //makes sure the user enters atleast 6 characters for password

            }
            auth.signInWithEmailAndPassword(emailinp, passwordinp).addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        // Fetch user data from the Realtime Database
                        val userID = auth.currentUser?.uid
                        val db = FirebaseDatabase.getInstance()
                        val usersNode = db.getReference("users")
                        /**checking the userRole by by the unique user id when the guest is logged in**/
                        usersNode.child(userID ?: "").get().addOnSuccessListener { sShot ->
                            val user = sShot.getValue(User::class.java)
                            user?.let {
                                //Check the userRole field on firebase database and open their respective activity
                                val intent = when (it.userRole) {
                                    "front_office" -> Intent(this, homeHub::class.java) //if the user is front desk team user
                                    "guest" -> Intent(this, makeReservation::class.java) //if the user is a guest user
                                    else -> Intent(this, MainActivity::class.java)
                                }
                                startActivity(intent)
                            }
                        }.addOnFailureListener { exception ->
                            Toast.makeText(this, "Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, authTask.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
        }
        //opens up the guest sign up page
        signUpTxtLogin.setOnClickListener {
            val intent = Intent(this, guestSignup::class.java)
            startActivity(intent)
        }
    }
}

/** reference to log in: https://www.geeksforgeeks.org/login-and-registration-in-android-using-firebase-in-kotlin/ **/