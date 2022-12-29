package app.mu.mulabassistant.ui.home

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import app.mu.mulabassistant.R
import app.mu.mulabassistant.ui.home.models.Equipment
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso

class DetailsActivity : AppCompatActivity() {
    private lateinit var ivDetails:ImageView
    private lateinit var tvDescription: MaterialTextView

    private lateinit var tvIsBooked:MaterialTextView
    private lateinit var tvCost :MaterialTextView
    @SuppressLint("UseCompatLoadingForColorStateLists")
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
        var wheretoFindIt = when(equipment.labcategory){
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
        tvIsBooked.text = when(equipment.isbooked)

        {
            true->"Booked"
            false->"Not Booked"
            else -> {
                "Not Available"
            }
        }
        tvIsBooked.backgroundTintList = resources.getColorStateList(selectedColor)





    }
}