package com.example.fyp_echeckin

import android.app.AlertDialog
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class makeReservation : AppCompatActivity() {

    //date pickers for checking the check in and out date
    lateinit var CIndateP: DatePicker
    lateinit var COdateP: DatePicker

    //edittext
    lateinit var fnameET: EditText
    lateinit var lnameET: EditText
    lateinit var emailET: EditText
    lateinit var phoneNoTxt: EditText
    lateinit var addressTxt: EditText
    lateinit var creditCardNoTxt: EditText
    lateinit var expirynoTxt: EditText
    lateinit var cvvNumberTXT: EditText

    //radio button to select between single or double bed
    lateinit var singleBedRdBtn: RadioButton
    lateinit var doubleBedRdBtn: RadioButton

    //spinners to select the room type and number of guests
    lateinit var roomTypeSP: Spinner
    lateinit var numOfGuestSp: Spinner

    //button
    lateinit var makeReservationBtn: Button

    private lateinit var database: DatabaseReference

    //date picker content saved in this variable
    var checkInDateInput: String? = null
    var checkoutDateInput: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_reservation)

        //sets the orientation of the activity to Portrait only
        requestedOrientation =  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //date picker initialise
        CIndateP = findViewById(R.id.reserv_checkIndate)
        COdateP = findViewById(R.id.reserv_checkOutdate)

        //buttons
        makeReservationBtn = findViewById(R.id.reserv_MakeReservBtn)

        //editTexts
        fnameET = findViewById(R.id.reserv_fName)
        lnameET = findViewById(R.id.reserv_LName)
        emailET = findViewById(R.id.reserv_email)
        phoneNoTxt = findViewById(R.id.reserv_phone)
        addressTxt = findViewById(R.id.reserv_city)
        creditCardNoTxt = findViewById(R.id.reserv_creditCard)
        expirynoTxt = findViewById(R.id.reserv_expiryDate)
        cvvNumberTXT = findViewById(R.id.reserv_cvv)

        //radio buttons
        singleBedRdBtn = findViewById(R.id.reserv_singleBed)
        doubleBedRdBtn = findViewById(R.id.reserv_doubleBed)

        //spinners
        roomTypeSP = findViewById(R.id.reserv_roomType)
        numOfGuestSp = findViewById(R.id.reserv_numbOfGuest)

        //spinner adapters to define the type of rooms and show the different price
        val roomTypes = arrayOf(
            "Classic Double - £250 pn ",
            "Luxury Ash Suite - £650 pn",
            "Penthouse - £900 pn"
        )

        val rTypesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roomTypes)
        roomTypeSP.adapter = rTypesAdapter

       //assign the array adapter with different number of guest
        val numOfGuests = arrayOf("1", "2", "Family - 2 adults, 2 kids")
        val numOfGuestsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, numOfGuests)
        numOfGuestSp.adapter = numOfGuestsAdapter

        // Get todays date
        val todaysDate = Calendar.getInstance()

            //when the user picks a date it stores in either checkIndateInput/CheckOutdateInput in the format of date, month and year
        CIndateP.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            CIndateP.minDate = todaysDate.timeInMillis
            checkInDateInput = "${dayOfMonth}/${monthOfYear + 1}/${year}"
        }

        COdateP.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            COdateP.minDate = CIndateP.minDate
            checkoutDateInput = "${dayOfMonth}/${monthOfYear + 1}/${year}"

        }

        //button on click to call the make reservation function
        makeReservationBtn.setOnClickListener {
            //checks if the date picker is empty or the checkOut date is equal or less then checkIn date
            if ( checkInDateInput.isNullOrBlank() || checkoutDateInput.isNullOrBlank() || checkInDateInput!! >= checkoutDateInput!!){
                Toast.makeText(this, "Please select a appropriate date", Toast.LENGTH_SHORT).show()
            }else {
                reservation()
            }

        }

    }

    private fun reservation() {

        val firstName = fnameET.text.trim().toString()
        val lastName = lnameET.text.trim().toString()
        val email = emailET.text.trim().toString()
        val phoneNumb = phoneNoTxt.text.trim().toString()
        val address = addressTxt.text.trim().toString()
        val creditCardNumber = creditCardNoTxt.text.trim().toString()
        val creditCardExpiry = expirynoTxt.text.trim().toString()
        val newCvv = cvvNumberTXT.text.trim().toString()
        val checkIn = checkInDateInput.toString()
        val checkOut = checkoutDateInput.toString()
        val noOfGuests = numOfGuestSp.selectedItem.toString()
        val roomType = roomTypeSP.selectedItem.toString()

        //to check which radio button is selected, if none then make the default to single bed
        val bedType = when {
            singleBedRdBtn.isChecked -> "Single Bed"
            doubleBedRdBtn.isChecked -> "Double Bed"
            else -> "Single Bed"
        }

        //checks to see if the fields are empty
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phoneNumb.isEmpty() || address.isEmpty() || creditCardNumber.isEmpty()
            || creditCardNumber.isEmpty() || creditCardExpiry.isEmpty()){
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        //checks if the first and last name has only letters
        if (!firstName.matches(Regex("[a-zA-Z]+")) && !lastName.matches(Regex("[a-zA-Z]+")) ){
            Toast.makeText(this, "First name must contain only letters", Toast.LENGTH_SHORT).show()
            return
        }

        //make sure the email address matches the correct format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
            return
        }

        //checks the phone number
        if (!Patterns.PHONE.matcher(phoneNumb).matches()) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show()
            return
        }

        //checks if the user entered credit card number is 16 digits
        if (!creditCardNumber.matches("^\\d{16}$".toRegex())) {
            Toast.makeText(this, "Invalid credit card number, make sure it is 16 characters", Toast.LENGTH_SHORT).show()
            return
        }

        if (newCvv.length < 3) {
            Toast.makeText(this, "Cvv must be at least 3 characters long", Toast.LENGTH_SHORT).show()
            return
        }

        //check the check in and out date and calculate the total price
        val checkInDay = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(checkIn)
        val checkOutDay = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(checkOut)
        val difference = checkOutDay.time - checkInDay.time

        //determine the number of nights
        val totalNights = TimeUnit.MILLISECONDS.toDays(difference).toInt()

        //the total price for different type of rooms per night
        val perNightPrice = when (roomType) {

            "Classic Double - £250 pn " -> 250
            "Luxury Ash Suite - £650 pn" -> 650
            "Penthouse - £900 pn" -> 900
            else -> 0
        }

        //total price for the reservation
        val totalPrice = totalNights * perNightPrice
        //display the total price of the stay in a dialog box
        val totPriceDialog = AlertDialog.Builder(this)
        totPriceDialog.setMessage("The total price for your stay is £$totalPrice for $totalNights nights")
        totPriceDialog.setCancelable(true)
        totPriceDialog.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = totPriceDialog.create()
        alertDialog.show()



        database = FirebaseDatabase.getInstance().getReference("reservations")
        val User = User(
            firstName,
            lastName,
            email,
            phoneNumb,
            address,
            creditCardNumber,
            creditCardExpiry,
            newCvv,
            checkIn,
            checkOut,
            noOfGuests,
            roomType,
            bedType,
            totalPrice,
            roomNumber = " ",
            fullName = "$firstName $lastName",
            status = "Checked In"

        )

        database.child(firstName).setValue(User).addOnSuccessListener {
            fnameET.text.clear()
            lnameET.text.clear()
            emailET.text.clear()
            phoneNoTxt.text.clear()
            addressTxt.text.clear()
            creditCardNoTxt.text.clear()
            expirynoTxt.text.clear()
            roomTypeSP.setSelection(0)
            numOfGuestSp.setSelection(0)
            singleBedRdBtn.isChecked = false
            doubleBedRdBtn.isChecked = false
            cvvNumberTXT.text.clear()

            Toast.makeText(this, "Thank you for your reservation", Toast.LENGTH_SHORT).show()
            dialougeBox()

        }.addOnFailureListener {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()


        }
    }

    //shows the check in and out date after the reservation has been made
    private fun dialougeBox() {
        val bookingDetails = LayoutInflater.from(this).inflate(R.layout.dialog_booking_info, null)

        val ciTV_reserv = bookingDetails.findViewById<TextView>(R.id.checkInDateTV_reserv)
        val coTv_reserv = bookingDetails.findViewById<TextView>(R.id.checkOutDateTV_reserv)

        ciTV_reserv.text = ("Check-In date: " + checkInDateInput)
        coTv_reserv.text = ("Check-Out date: " + checkoutDateInput)

        AlertDialog.Builder(this)
            .setView(bookingDetails)
            .setTitle("Booking Information")
            .setPositiveButton("Confirm") { _, _ ->
                Toast.makeText(this, "Thank you for your reservation", Toast.LENGTH_SHORT).show()
            }
            .show()
    }
}
/** Date picker reference to calculate the total price
 * https://www.tutorialspoint.com/how-to-get-the-differences-between-two-dates-in-android-using-kotlin
 *
 * save data to database
 * https://androidknowledge.com/store-retrieve-firebaserealtime-kotlin-android
 *
 * Create dailog box:
 * https://developer.android.com/develop/ui/views/components/dialogs/ **/
