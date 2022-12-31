package com.mu.labassistant.studentapp.ui.home.adapters

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText

import com.google.android.material.textview.MaterialTextView
import com.google.firebase.database.FirebaseDatabase
import com.mu.labassistant.studentapp.R
import com.mu.labassistant.studentapp.ui.home.models.EquipmentRequest
import com.squareup.picasso.Picasso

class NotificationAdapter constructor(options: FirebaseRecyclerOptions<EquipmentRequest>) :
    FirebaseRecyclerAdapter<EquipmentRequest, NotificationAdapter.EquipmentViewModel>(options) {
    class EquipmentViewModel internal constructor(view: View) : RecyclerView.ViewHolder(view) {



        @SuppressLint("SetTextI18n")
        internal fun setProductName(
            equipment: EquipmentRequest,
            position: Int
        ) {
            val tvTitle: MaterialTextView = itemView.findViewById(R.id.tvTitle)
            val tvDescription: MaterialTextView = itemView.findViewById(R.id.tvDescription)
            val tvPosition: MaterialTextView = itemView.findViewById(R.id.tvPosition)
            val tvStatus: MaterialTextView = itemView.findViewById(R.id.tvStatus)
            val tvReason: MaterialTextView = itemView.findViewById(R.id.tvReason)


            val profileImage = itemView.findViewById<ImageView>(R.id.ivEquipmentImage)
            Picasso.get().load(equipment.equipment?.imageone).placeholder(R.drawable.loadingimage).into(profileImage)
            tvTitle.text = equipment.equipment?.name
            tvDescription.text= equipment.equipment?.labcategory
            tvPosition.text = (position+1).toString()

            val status: String = if (equipment.isApproved == true){
                " \tApproved\t "
            }else{
                " \tAwaiting Approval\t "
            }
            tvStatus.text =status
//                when(equipment.isApproved){
//                true->" \tApproved\t "
//                false->" \tAwaiting Approval\t "
//                else->"N/A"
//
//            }
            val selectedColor = when(equipment.isApproved){
                true->android.R.color.holo_green_light
                else -> {
                    android.R.color.holo_orange_dark
                }
            }
            tvReason.text = when(equipment.isPaid){
                true->" \t(Paid)\t "
                else->" \t(You haven't Paid Ksh. ${equipment.cost})\t "

            }
            tvStatus.backgroundTintList = itemView.context.resources.getColorStateList(selectedColor)





            itemView.setOnClickListener {
                when(equipment.isApproved == true && equipment.isPaid == true){
                    true->{
                        Toast.makeText(
                            itemView.context,
                            "This Equipment has already been approved.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else->{
                    showPaymentConfirmationSheet(equipment)

                }

                }





            }



        }

        private fun showPaymentConfirmationSheet(equipment: EquipmentRequest) {

            // on below line we are creating a new bottom sheet dialog.
            val dialog = BottomSheetDialog(itemView.context)

            // on below line we are inflating a layout file which we have created.
            val view = LayoutInflater.from(itemView.context).inflate(R.layout.payment_verification_dialog, null)

            // on below line we are creating a variable for our button
            // which we are using to dismiss our dialog.
            val btnClose = view.findViewById<Button>(R.id.idBtnDismiss)
            val idTVAmountPayable = view.findViewById<TextView>(R.id.idTVAmountPayable)
            val etConfirmationCode = view.findViewById<TextInputEditText>(R.id.etConfirmationCode)


            idTVAmountPayable.text = "Total Amount Payable: Ksh. ${equipment.cost}"

            // on below line we are adding on click listener
            // for our dismissing the dialog button.
            btnClose.setOnClickListener {
                // on below line we are calling a dismiss
                // method to close our dialog.


                val confirmationCode = etConfirmationCode.text.toString()
                if(!TextUtils.isEmpty(confirmationCode)){

                   val notificationsDb = FirebaseDatabase.getInstance().reference.child("MUAPP/EQUIPMENTREQUESTS")
                    notificationsDb.child(equipment.userId!!).child(equipment.equipment?.id!!).child("paymentcode").setValue(confirmationCode)
                    notificationsDb.child(equipment.equipmentAdmin!!).child(equipment.equipment.id).child("paymentcode").setValue(confirmationCode).addOnCompleteListener {
                        confirmpayment->
                        if (confirmpayment.isSuccessful){
                            Toast.makeText(itemView.context, "Confirmation code successfully updated, Please wait for approval", Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(itemView.context, confirmpayment.exception?.message, Toast.LENGTH_LONG).show()

                        }
                    }


                    //We can update the equipment confirmation code and await for approval.


                }else{
                    Toast.makeText(
                        itemView.context,
                        "Sorry, please add a valid confirmation code",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dialog.dismiss()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipmentViewModel {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_equipment_request, parent, false)


        return EquipmentViewModel(view)
    }


    override fun onBindViewHolder(holder: EquipmentViewModel, position: Int, model: EquipmentRequest) {

        holder.setProductName(model,position)
    }


}