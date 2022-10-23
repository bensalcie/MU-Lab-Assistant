package app.mu.mulabassistant.ui.home.adapters

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import app.mu.mulabassistant.R
import app.mu.mulabassistant.ui.home.DetailsActivity
import app.mu.mulabassistant.ui.home.models.Equipment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.database.DatabaseError
import com.squareup.picasso.Picasso

class EquipmentAdapter constructor(options: FirebaseRecyclerOptions<Equipment>) :
    FirebaseRecyclerAdapter<Equipment, EquipmentAdapter.EquipmentViewModel>(options) {
    class EquipmentViewModel internal constructor(view: View) : RecyclerView.ViewHolder(view) {



        internal fun setProductName(
            equipment: Equipment) {
            val tvTitle: MaterialTextView = itemView.findViewById(R.id.tvTitle)
            val tvDescription: MaterialTextView = itemView.findViewById(R.id.tvDescription)
            val tvStatus: MaterialTextView = itemView.findViewById(R.id.tvStatus)


            val profileImage = itemView.findViewById<ImageView>(R.id.ivEquipmentImage)
            Picasso.get().load(equipment.imageone).placeholder(R.drawable.loadingimage).into(profileImage)
            tvTitle.text = equipment.name
            tvDescription.text= equipment.labcategory
            tvStatus.text = when(equipment.isbooked){
                true->"Booked"
                false->"Not Booked"
                else -> {
                    "Not Available"
                }
            }

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
                itemView.context.startActivity(Intent(it.context,DetailsActivity::class.java).putExtras(bundle))

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