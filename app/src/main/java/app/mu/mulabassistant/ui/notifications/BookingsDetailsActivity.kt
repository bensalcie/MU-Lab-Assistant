package app.mu.mulabassistant.ui.notifications

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import app.mu.mulabassistant.R
import app.mu.mulabassistant.ui.home.models.Equipment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class BookingsDetailsActivity : AppCompatActivity() {
    private lateinit var ivDetails: ImageView
    private lateinit var tvDescription: MaterialTextView

    private lateinit var tvIsBooked: MaterialTextView
    private lateinit var tvCost : MaterialTextView
    private lateinit var equipmentRequestsDb: DatabaseReference
    private lateinit var pd: ProgressDialog
    private lateinit var equipmentDb: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookings_details)
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

        val paymentcode = bundle?.getString("paymentcode")
        val userId = bundle?.getString("userId")
        val equipmentAdmin = bundle?.getString("equipmentAdmin")





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
        tvIsBooked.text =  when(paymentcode){
            "default"->" Pending Payment "
            else -> {
                "Approve"
            }
        }
        when(paymentcode){
            "default"->{
                tvIsBooked.setOnClickListener {
                    Toast.makeText(this, "Payments to this Equipment haven't been done.", Toast.LENGTH_SHORT)
                        .show()
                }

            }
            else-> {
                tvIsBooked.setOnClickListener {

                    // Just show the handshake dialog to verify the amount and the code.

                    showHandShakeDialogtoVerify(equipment,paymentcode,userId,equipmentAdmin)




                }

            }


        }
        tvIsBooked.backgroundTintList = resources.getColorStateList(selectedColor)





    }
    private fun showHandShakeDialogtoVerify(
        equipment: Equipment,
        paymentcode: String?,
        userId: String?,
        equipmentAdmin: String?
    ) {

        // on below line we are creating a new bottom sheet dialog.
        val dialog = BottomSheetDialog(this)

        // on below line we are inflating a layout file which we have created.
        val view = LayoutInflater.from(this).inflate(R.layout.payment_verification_dialog, null)

        // on below line we are creating a variable for our button
        // which we are using to dismiss our dialog.
        val btnClose = view.findViewById<Button>(R.id.idBtnReject)
        val btnAccept = view.findViewById<Button>(R.id.idBtnApprove)


        val idTVAmountPayable = view.findViewById<TextView>(R.id.idTVAmountPayable)
        val idTVCourseDuration = view.findViewById<TextView>(R.id.idTVCourseDuration)
        idTVCourseDuration.text = "PLEASE CHECK IF THE INFORMATION MATCH\nCONFIRMATION CODE:$paymentcode"

        idTVAmountPayable.text = "Total Amount Payable: Ksh. ${equipment.cost}"

        btnAccept.setOnClickListener {
            //Payment successful, so we can go ahead and approve equipment for use.
            // Update status of the Equipment request.


            //Delete the booking request, Make equipment available for booking by others.
            if (userId != null) {
                pd.setTitle("Approving user request")
                pd.setMessage("Please wait as we approve this request...")
                pd.show()
                equipment.id?.let { it1 -> equipmentRequestsDb.child(userId).child(it1).child("isApproved").setValue(true) }
                equipment.id?.let { it1 ->
                    equipmentRequestsDb.child(userId).child(it1).child("isPaid").setValue(true).addOnCompleteListener { userUpdate->
                        if (userUpdate.isSuccessful){
                            pd.dismiss()
                        }else{
                            pd.dismiss()

                        }
                    }
                }



            }

            if (equipmentAdmin != null) {
                pd.setTitle("Approving admin request")
                pd.setMessage("Please wait as we approve this request...")
                pd.show()

                //
                equipmentRequestsDb.child(equipmentAdmin).child(equipment.id!!).child("isApproved").setValue(true)
                equipmentRequestsDb.child(equipmentAdmin).child(equipment.id).child("isPaid").setValue(true).addOnCompleteListener {
                        userUpdate->
                    if (userUpdate.isSuccessful){

                        //We can go ahead and notify user, that the request is approved by sms
                        pd.dismiss()
                        dialog.dismiss()

                        Toast.makeText(this, "Equipment successfully approved", Toast.LENGTH_SHORT).show()
                        finish()

                    }else{
                        pd.dismiss()
                        dialog.dismiss()

                        Toast.makeText(this, userUpdate.exception?.message, Toast.LENGTH_SHORT).show()

                    }
                }

                equipment.id.let { it1 -> equipmentDb.child(it1).child("isbooked").setValue(true) }



            }

        }

        // on below line we are adding on click listener
        // for our dismissing the dialog button.
        btnClose.setOnClickListener {
            // on below line we are calling a dismiss
            // method to close our dialog.

            //Delete the booking request, Make equipment available for booking by others.


            if (userId != null) {
                pd.setTitle("Cancelling user request")
                pd.setMessage("Please wait as we cancel this request...")
                pd.show()
                equipment.id?.let { it1 -> equipmentRequestsDb.child(userId).child(it1).removeValue().addOnCompleteListener {
                    if (it.isSuccessful){
                        pd.dismiss()

                    }else{
                        pd.dismiss()
                    }
                } }
            }

            if (equipmentAdmin != null) {
                pd.setTitle("Cancelling user request")
                pd.setMessage("Please wait as we cancel this request...")
                pd.show()
                equipment.id?.let { it1 ->
                    equipmentRequestsDb.child(equipmentAdmin).child(it1).removeValue().addOnCompleteListener { deleteRequest->
                        if (deleteRequest.isSuccessful){
                            pd.dismiss()
                            dialog.dismiss()
                            Toast.makeText(this, "This Equipment has been made available", Toast.LENGTH_SHORT).show()
                            finish()

                        }else{
                            pd.dismiss()
                            dialog.dismiss()
                            Toast.makeText(this, deleteRequest.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                equipment.id.let { it1 ->
                    if (it1 != null) {
                        equipmentDb.child(it1).child("isbooked").setValue(false)
                    }
                }

            }
            //dialog.dismiss()

        }
        // below line is use to set cancelable to avoid
        // closing of dialog box when clicking on the screen.
        dialog.setCancelable(true)

        // on below line we are setting
        // content view to our view.
        dialog.setContentView(view)

        // on below line we are calling
        // a show method to display a dialog.
        dialog.show()

    }

}