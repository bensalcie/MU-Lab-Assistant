package app.mu.mulabassistant.ui.notifications.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.mu.mulabassistant.R
import app.mu.mulabassistant.ui.notifications.BookingsDetailsActivity
import app.mu.mulabassistant.ui.notifications.models.EquipmentRequest
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog

import com.google.android.material.textview.MaterialTextView
import com.google.firebase.database.*
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
            val tvHeader: MaterialTextView = itemView.findViewById(R.id.tvHeader)



            val profileImage = itemView.findViewById<ImageView>(R.id.ivEquipmentImage)
            Picasso.get().load(equipment.equipment?.imageone).placeholder(R.drawable.loadingimage).into(profileImage)

            tvDescription.text= equipment.equipment?.labcategory
            tvPosition.text = (position+1).toString()
            tvStatus.text = when(equipment.isApproved){
                true->" \tApproved\t "
                else->" \tAwaiting your Approval\t "

            }
            tvHeader.text = "New equipment request! "
            val selectedColor = when(equipment.isApproved){
                true->android.R.color.holo_green_light
                else -> {
                    android.R.color.holo_orange_dark
                }
            }
            tvReason.text = when(equipment.isPaid){
                true->" \t(Paid)\t "
                else->" \t(You haven't verified their  Ksh. ${equipment.cost}) payment\t "

            }
            tvStatus.backgroundTintList = itemView.context.resources.getColorStateList(selectedColor)





            itemView.setOnClickListener {

                //showPaymentConfirmationSheet(equipment)


                val bundle = Bundle()
                bundle.putString("name",equipment.equipment?.name)
                bundle.putString("description",equipment.equipment?.description)
                bundle.putString("imageone",equipment.equipment?.imageone)
                bundle.putString("labcategory",equipment.equipment?.labcategory)
                bundle.putString("id",equipment.equipment?.id)

                bundle.putString("paymentcode",equipment.paymentcode)

                bundle.putString("userId",equipment.userId)
                bundle.putString("equipmentAdmin",equipment.equipmentAdmin)


                equipment.cost?.let { it1 -> bundle.putDouble("cost", it1) }
                equipment.equipment?.date?.let { it1 -> bundle.putLong("date", it1) }
                equipment.equipment?.avaibalilitydate?.let { it1 -> bundle.putLong("avaibalilitydate", it1) }
                equipment.equipment?.isbooked?.let { it1 -> bundle.putBoolean("isbooked", it1) }


                itemView.context.startActivity(Intent(itemView.context,BookingsDetailsActivity::class.java).putExtras(bundle))

            }
            val usersDb = FirebaseDatabase.getInstance().reference.child("MUAPP/MUSTUDENTS")
            equipment.userId?.let {
                usersDb.child(it).addListenerForSingleValueEvent(object: ValueEventListener{
                    /**
                     * This method will be called with a snapshot of the data at this location. It will also be called
                     * each time that data changes.
                     *
                     * @param snapshot The current data at the location
                     */
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.exists()){
                            val username = snapshot.child("name").value.toString()


                            tvTitle.text = "$username requested to use the  ${equipment.equipment?.name}"

                        }
                    }

                    /**
                     * This method will be triggered in the event that this listener either failed at the server, or
                     * is removed as a result of the security and Firebase Database rules. For more information on
                     * securing your data, see: [ Security
         * Quickstart](https://firebase.google.com/docs/database/security/quickstart)
                     *
                     * @param error A description of the error that occurred
                     */
                    override fun onCancelled(error: DatabaseError) {

                    }

                })
            }



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