package app.mu.mulabassistant.ui.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import app.mu.mulabassistant.R
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
            Picasso.get().load(equipment.imageone).placeholder(android.R.drawable.ic_menu_gallery).into(profileImage)
            tvTitle.text = equipment.name
            tvDescription.text= equipment.description
            tvStatus.text = when(equipment.isbooked){
                true->"Booked"
                false->"Not Booked"
                else -> {
                    "Not Available"
                }
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