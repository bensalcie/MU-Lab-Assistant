package com.mu.labassistant.studentapp

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textview.MaterialTextView
import com.mu.labassistant.studentapp.ui.home.models.Equipment
import com.squareup.picasso.Picasso
import java.util.*


class DetailsActivity : AppCompatActivity() {
    private lateinit var ivDetails: ImageView
    private lateinit var tvDescription: MaterialTextView

    private lateinit var tvIsBooked: MaterialTextView
    private lateinit var tvCost : MaterialTextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        ivDetails = findViewById(R.id.ivDetails)
        tvDescription = findViewById(R.id.tvDescription)
        tvCost = findViewById(R.id.tvCost)
        tvIsBooked = findViewById(R.id.tvIsBooked)
        val bundle  = intent.extras
        val equipment = Equipment(
            name = bundle?.getString("name"),
            description = bundle?.getString("description"),
            avaibalilitydate = bundle?.getLong("avaibalilitydate"),
            cost = bundle?.getDouble("cost"),
            date = bundle?.getLong("date"),
            imageone = bundle?.getString("imageone"),
            isbooked = bundle?.getBoolean("isbooked"),
            key =bundle?.getString("key"),
            labcategory =  bundle?.getString("labcategory"),




            )
        supportActionBar?.title = equipment.name
        Picasso.get().load(equipment.imageone).placeholder(R.drawable.loadingimage).into(ivDetails)
        val wheretoFindIt = when(equipment.labcategory){
            null -> ""
            else -> "Found in ${equipment.labcategory}"
        }
        tvCost.text = "Costs ${equipment.cost} Ksh to Book\n$wheretoFindIt"
        tvDescription.text = equipment.description

        val selectedColor = when(equipment.isbooked){
            true->android.R.color.holo_red_light
            false->android.R.color.holo_green_light
            else -> {
                android.R.color.white
            }
        }
        tvIsBooked.text =  when(equipment.isbooked){
            true->" \tBooked\t "
            false->" \tBook\t "
            else -> {
                "Not Available"
            }
        }
        when(equipment.isbooked){
            true->{
                tvIsBooked.setOnClickListener {
                    Toast.makeText(this, "This equipment is already booked.", Toast.LENGTH_SHORT)
                        .show()
                }

            }
            false-> {
                tvIsBooked.setOnClickListener {

                    // Initiation date picker with
                    // MaterialDatePicker.Builder.datePicker()
                    // and building it using build()
                    val datePicker = MaterialDatePicker.Builder.dateRangePicker().build()
                    datePicker.show(supportFragmentManager, "DatePicker")

                    // Setting up the event for when ok is clicked
                    datePicker.addOnPositiveButtonClickListener {
                        Toast.makeText(this, "First ${it.first} Second${it.second}", Toast.LENGTH_LONG).show()

                    }

                    // Setting up the event for when cancelled is clicked
                    datePicker.addOnNegativeButtonClickListener {
                        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                    }

                    // Setting up the event for when back button is pressed
                    datePicker.addOnCancelListener {
                        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                    }



                }

            }
            else -> {
                tvIsBooked.setOnClickListener {
                    Toast.makeText(this, "This equipment cant be booked at this moment, under maintenance.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        tvIsBooked.backgroundTintList = resources.getColorStateList(selectedColor)





    }
}