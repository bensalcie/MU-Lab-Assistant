package com.mu.labassistant.studentapp

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.mu.labassistant.studentapp.ui.home.models.Equipment
import com.squareup.picasso.Picasso
import java.util.concurrent.TimeUnit


class DetailsActivity : AppCompatActivity() {
    private lateinit var ivDetails: ImageView
    private lateinit var tvDescription: MaterialTextView

    private lateinit var tvIsBooked: MaterialTextView
    private lateinit var tvCost : MaterialTextView
    private lateinit var equipmentRequestsDb: DatabaseReference
    private lateinit var pd:ProgressDialog
    private lateinit var equipmentDb: DatabaseReference



    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        ivDetails = findViewById(R.id.ivDetails)
        pd= ProgressDialog(this)
        pd.setTitle("Requesting equipment")
        equipmentRequestsDb = FirebaseDatabase.getInstance().reference.child("MUAPP/EQUIPMENTREQUESTS")
        equipmentDb = FirebaseDatabase.getInstance().reference.child("MUAPP/EQUIPMENT")

        mAuth = FirebaseAuth.getInstance()
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
            id =bundle?.getString("id"),
            labcategory =  bundle?.getString("labcategory"),




            )



        pd.setMessage("Please wait as we request for ${equipment.name}")

        supportActionBar?.title = equipment.name
        Picasso.get().load(equipment.imageone).placeholder(R.drawable.loadingimage).into(ivDetails)
        val wheretoFindIt = when(equipment.labcategory){
            null -> ""
            else -> "Found in ${equipment.labcategory}"
        }
        tvCost.text = "Costs ${equipment.cost} Ksh to Book (Per Day)\n$wheretoFindIt"
        tvDescription.text = equipment.description

        val selectedColor = when(equipment.isbooked){
            true->android.R.color.holo_red_light
            false->android.R.color.holo_green_light
            else -> {
                android.R.color.white
            }
        }
        tvIsBooked.text =  when(equipment.isbooked){
            true->" Booked "
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

                        val days: Long = TimeUnit.MILLISECONDS.toDays(it.second-it.first)
                        var cost = equipment.cost?.times(days)
                        if (cost==0.0){
                            cost=equipment.cost
                        }

                        val userid= mAuth.currentUser?.uid

                        val isPaidFor = false
                        val isApproved = false

                        val adminId = "AAblMJyrwscybZ7qm5vt54O4b4M2"
                        // Inform request to use equipment.

                        val equipmentRequestMap = HashMap<String,Any>()
                        if (userid != null) {
                            equipmentRequestMap["userId"] = userid
                            equipmentRequestMap["equipment"] = equipment
                            equipmentRequestMap["isPaid"] = isPaidFor
                            equipmentRequestMap["isApproved"] = isApproved
                            equipmentRequestMap["equipmentAdmin"] = adminId
                            equipmentRequestMap["requeststartday"] = it.first
                            equipmentRequestMap["requestendday"] = it.second
                            equipmentRequestMap["cost"] = cost!!
                            equipmentRequestMap["days"] = days




                            //Add to requests DB.
                            //   Add Request to Personal account.

                            pd.show()
                            equipment.id?.let { it1 ->
                                equipmentRequestsDb.child(userid).child(it1).updateChildren(equipmentRequestMap).addOnCompleteListener { userRequest->
                                    if (userRequest.isSuccessful){
                                        equipmentRequestsDb.child(adminId).child(equipment.id).updateChildren(equipmentRequestMap).addOnCompleteListener { adminRequest->
                                            if (adminRequest.isSuccessful){
                                                pd.dismiss()
                                                //We can dismiss the date picker.
                                                //    Order was placed.
                                                datePicker.dismiss()
                                                Toast.makeText(this, "Order placed, please go ahead and make the payment. ",
                                                    Toast.LENGTH_SHORT).show()
                                                equipment.id.let { it1 -> equipmentDb.child(it1).child("isbooked").setValue(true) }


                                            }else {
                                                pd.dismiss()
                                                Toast.makeText(this, adminRequest.exception?.message, Toast.LENGTH_SHORT).show()

                                            }
                                        }


                                    }else{
                                        pd.dismiss()
                                        Toast.makeText(this, userRequest.exception?.message, Toast.LENGTH_SHORT).show()

                                    }
                                }
                            }


                        }










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