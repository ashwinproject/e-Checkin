package com.example.fyp_echeckin

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.FirebaseDatabase

class roomServiceActivity : AppCompatActivity() {

    //spinners
    private lateinit var roomNumSp: Spinner
    private lateinit var categorySp: Spinner

    //button
    private lateinit var orderBtn: Button
    private lateinit var quantityET: EditText

    //add to order button
    private lateinit var addToOrder: Button

    private var RoomNumbSelected: String = ""
    private var selectedFoodCategory: String = ""
    private var selectedFoodItem: String = ""

    //food price as set to 0
    private var selectedFoodPrice: Double = 0.0
    private var quantityOrdered: Int = 1

    //to hold the items which have been added to the order
    private val itemsAddedToOrder = mutableListOf<Pair<String, Int>>()

    //create an object to hold the roomServiceMenu
    private val roomServiceMenu = mapOf(
        "Breakfast" to listOf(
            MenuItem("Chocolate Chip Pancakes - £8.00", 8.00),
            MenuItem("Full English Breakfast - £14.00", 14.00),
            MenuItem("Scrambled Eggs - £4.00 ", 4.00),
            MenuItem("Omelette - £4.00", 4.00),
            MenuItem("Indian Full Breakfast - £19.00", 19.00)
        ),
        "Lunch" to listOf(
            MenuItem("Half Chicken - £8.00", 8.00),
            MenuItem("Full Chicken - £15.00", 15.00),
            MenuItem("Grilled Chicken Wrap - £7.00", 7.00),
            MenuItem("Fish and Chips - £10.00", 10.00),
            MenuItem("Grilled Chicken Burger - £7.00", 7.00)
        ),
        "Dinner" to listOf(
            MenuItem("Steak and Chips - £20.00", 20.00),
            MenuItem("Fish Rice and Curry - £5.00", 5.00),
            MenuItem("Chicken Rice and Curry - £10.00", 10.00),
            MenuItem("Full Chicken and Chips - £18.00", 18.00),
        ),
        "Snacks" to listOf(
            MenuItem("Chips - £2.00", 2.00),
            MenuItem("Grilled Mushroom - £3.00", 3.00),
            MenuItem("Mozzarella Sticks - £7.00", 7.00),
            MenuItem("BBQ Wings - £6.00", 6.00),
            MenuItem("Coleslaw - £5.00", 5.00)
        ),
        "Drinks" to listOf(
            MenuItem("Coca-Cola - £1", 1.000),
            MenuItem("7UP - £1", 1.00),
            MenuItem("Fanta -£1", 1.00),
            MenuItem("Coke Zero -£1", 1.00),
            MenuItem("Water -£1", 1.00)
        )
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_service)

        //sets the orientation of the activity to Portrait only
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        roomNumSp = findViewById(R.id.room_number_spinner)
        categorySp = findViewById(R.id.category_spinner)
        orderBtn = findViewById(R.id.order_button)
        quantityET = findViewById(R.id.quantity_edit_text)
        addToOrder = findViewById(R.id.add_to_order_button)

        //populates the room number array and set the array adapters layout
        val roomNumberArray = resources.getStringArray(R.array.room_numbers)
        val arrayAdap = ArrayAdapter(this, android.R.layout.simple_spinner_item, roomNumberArray)
        roomNumSp.adapter = arrayAdap

        //get the current selected item
        roomNumSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                pos: Int,
                id: Long
            ) {
                //convert the selected item by position to string
                RoomNumbSelected = parent.getItemAtPosition(pos).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        //layout and populate menu item adapter
        val arrayAdapterMenu = ArrayAdapter(this, android.R.layout.simple_spinner_item, roomServiceMenu.keys.toList())
        categorySp.adapter = arrayAdapterMenu

        categorySp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                pos: Int,
                id: Long
            ) {
                //sets the food category to which ever item the user selects
                selectedFoodCategory = parent.getItemAtPosition(pos).toString()
                //sets the food item to empty
//                selectedFoodItem = ""
                //foodPrice to 0
                selectedFoodPrice = 0.0
                //and update the menu item spinner
                menuItemSpinner()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        addToOrder.setOnClickListener {
            addToOrder()
        }


        orderBtn.setOnClickListener {
            orderRoomService()
        }
    }

    //adds the selected item to the order
    private fun addToOrder() {
        //gets the user input for quantity or if the guest set to 1
        val quantityInp = quantityET.text.toString().toIntOrNull() ?: 1
        quantityOrdered = quantityInp

        //check the amount of quantity ordered
        if (quantityInp == null || quantityInp < 1 || quantityInp > 3){
            Toast.makeText(this, "You are only allowed to order up to 3 of the same items", Toast.LENGTH_SHORT).show()
            return
        }

        else if (selectedFoodItem.isBlank()) {
            Toast.makeText(this, "Please select a menu item", Toast.LENGTH_SHORT).show()
            return
        }
        else{
            //selected item price and name as the list of the room item from selected category is retrieved
            val selectedItemPrice = roomServiceMenu[selectedFoodCategory]!!.find { it.name == selectedFoodItem }!!.price
            val orderItems = selectedItemPrice * quantityInp

                //items added to the order by the selected food item and quantity
            itemsAddedToOrder.add(Pair(selectedFoodItem, quantityInp))

            Toast.makeText(this, "$selectedFoodItem has been added to the order", Toast.LENGTH_SHORT).show()

            //set the quantity to 1 after item has been added to the order
            quantityET.setText("1")
        }
    }

    private fun orderRoomService() {
//        var orderTotal = 0.0
        if (itemsAddedToOrder.isEmpty()) {
            Toast.makeText(this, "You have not ordered any items", Toast.LENGTH_SHORT).show()
            return
        }


        //finds the food and gets the total price for the items that have been ordered.
        val totalOrder = itemsAddedToOrder.sumOf { roomServiceItem ->
            roomServiceMenu[selectedFoodCategory]?.find { it.name == roomServiceItem.first }?.price?.times(roomServiceItem.second) ?: 0.0
        }

        //join to string function is used, to all the items added the the order
        val allItems = itemsAddedToOrder.joinToString("\n") { roomServiceItem ->

            //object that matches the name of the item to the selected category
            val menuItem = roomServiceMenu[selectedFoodCategory]?.find { it.name == roomServiceItem.first }
            if (menuItem != null) {
                //first item is the items name and second is the price which is multiplied
                "${roomServiceItem.first} - £${roomServiceItem.second * menuItem.price}"
            } else {
                //when the item is not available
                "${roomServiceItem.first} - £0.0"
            }
        }
//        //total number of items in the order
//        val numberOfItems = itemsAddedToOrder.sumOf { it.second }

        //A dialog builder to let the user know the order details
        val dBuilder = AlertDialog.Builder(this)

        //shows the total order and the items ordered
        dBuilder.setMessage("Total: £${totalOrder}\n\nItems Ordered:\n$allItems")

        //confirm button to save data to database
        dBuilder.setPositiveButton("Confirm Order") { _, _ ->

            val roomServiceNode = FirebaseDatabase.getInstance().getReference("roomService")

                //saves the data to th database
            val userOrder = roomServiceNode.push()
            userOrder.child("roomNumber").setValue(RoomNumbSelected)
            userOrder.child("orderTotal").setValue(totalOrder)

            val foodItems = userOrder.child("order_items")
            itemsAddedToOrder.forEach {
                foodItems.push().setValue(it.first to it.second)
            }

            Toast.makeText(this, "Order has been placed successfully ! Your order will be with you in Room: $RoomNumbSelected", Toast.LENGTH_SHORT).show()

            //clear all the fields and selected items after an order is placed
            itemsAddedToOrder.clear()
            selectedFoodItem = ""
            selectedFoodPrice = 0.0
            quantityOrdered = 1
            quantityET.setText("")
        }
        //cancel button
        dBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        //create the dialog box and display
        val alertDialog = dBuilder.create()
        alertDialog.show()
    }


    //
    private fun menuItemSpinner() {

        //gets the name of the item from selected food category
        val itemName = roomServiceMenu[selectedFoodCategory]!!.map { it.name }

        //sets the layout and populates the array adapter with the name
        val arrapAdap = ArrayAdapter(this, android.R.layout.simple_spinner_item, itemName)
        arrapAdap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        //gets the spinner view and sets the adapter
        findViewById<Spinner>(R.id.menu_item_spinner).adapter = arrapAdap
        findViewById<Spinner>(R.id.menu_item_spinner).onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    //get the name of the selected food
                    selectedFoodItem = parent.getItemAtPosition(pos).toString()

                    //get the price of the selected food
                    selectedFoodPrice = roomServiceMenu[selectedFoodCategory]!!.find { it.name == selectedFoodItem }!!.price
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
    }
}

/**Array adapter:
 * https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView**
 *
 * Menu:
 * https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/list-of.html
 *
 * Array adapter:
 * https://developer.android.com/reference/kotlin/android/widget/AdapterView.OnItemSelectedListener\
 * https://developer.android.com/reference/android/widget/ArrayAdapter
 * https://www.tabnine.com/code/java/methods/android.widget.AdapterView/getItemAtPosition
 *
 * Join to string:
 * https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/join-to-string.html
 *
 * Dialog box:
 * https://www.digitalocean.com/blog/introducing-premium-cpu-optimized-droplets
 *
 * Saving data to database:
 * https://firebase.google.com/docs/database/admin/save-data*/