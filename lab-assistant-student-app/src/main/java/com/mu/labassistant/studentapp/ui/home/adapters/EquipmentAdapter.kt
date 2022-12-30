package com.mu.labassistant.studentapp.ui.home.adapters

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.mu.labassistant.studentapp.ui.home.models.Equipment

import com.google.android.material.textview.MaterialTextView
import com.mu.labassistant.studentapp.DetailsActivity
import com.mu.labassistant.studentapp.R
import com.squareup.picasso.Picasso

class EquipmentAdapter constructor(options: FirebaseRecyclerOptions<Equipment>) :
    FirebaseRecyclerAdapter<Equipment, EquipmentAdapter.EquipmentViewModel>(options) {
    class EquipmentViewModel internal constructor(view: View) : RecyclerView.ViewHolder(view) {



        internal fun setProductName(
            equipment: Equipment
        ) {
            val tvTitle: MaterialTextView = itemView.findViewById(R.id.tvTitle)
            val tvDescription: MaterialTextView = itemView.findViewById(R.id.tvDescription)
            val tvStatus: MaterialTextView = itemView.findViewById(R.id.tvStatus)


            val profileImage = itemView.findViewById<ImageView>(R.id.ivEquipmentImage)
            Picasso.get().load(equipment.imageone).placeholder(R.drawable.loadingimage).into(profileImage)
            tvTitle.text = equipment.name
            tvDescription.text= equipment.labcategory
            tvStatus.text = when(equipment.isbooked){
                true->" \tBooked\t "
                false->" \tBook\t "
                else -> {
                    "Not Available"
                }
            }
            val selectedColor = when(equipment.isbooked){
                true->android.R.color.holo_red_light
                false->android.R.color.holo_green_light
                else -> {
                    android.R.color.white
                }
            }
            tvStatus.backgroundTintList = itemView.context.resources.getColorStateList(selectedColor)





            itemView.setOnClickListener {

                val bundle = Bundle()
                bundle.putString("name",equipment.name)
                bundle.putString("description",equipment.description)
                bundle.putString("imageone",equipment.imageone)
                bundle.putString("labcategory",equipment.labcategory)
                bundle.putString("key",equipment.key)
                equipment.cost?.let { it1 -> bundle.putDouble("cost", it1) }
                equipment.date?.let { it1 -> bundle.putLong("date", it1) }
                equipment.avaibalilitydate?.let { it1 -> bundle.putLong("avaibalilitydate", it1) }
                equipment.isbooked?.let { it1 -> bundle.putBoolean("isbooked", it1) }
                itemView.context.startActivity(Intent(it.context, DetailsActivity::class.java).putExtras(bundle))

            }



        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipmentViewModel {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_equipment, parent, false)


        return EquipmentViewModel(view)
    }


    override fun onBindViewHolder(holder: EquipmentViewModel, position: Int, model: Equipment) {

        holder.setProductName(model)
    }


}