package app.mu.mulabassistant.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ProgressBar
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.mu.mulabassistant.R
import app.mu.mulabassistant.databinding.FragmentHomeBinding
import app.mu.mulabassistant.ui.home.adapters.EquipmentAdapter
import app.mu.mulabassistant.ui.home.models.Equipment
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
//    private val binding get() = _binding!!
    private lateinit var equipmentDb: DatabaseReference
    private lateinit var equipmentAdapter:EquipmentAdapter
    private lateinit var equipmentStorage: StorageReference

    private lateinit var equipmentsProgress:ProgressBar
    private lateinit var addEquipmentFragment:ExtendedFloatingActionButton
    private lateinit var imageSlider:ImageSlider
    private lateinit var root:View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

//        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        equipmentDb = FirebaseDatabase.getInstance().reference.child("MUAPP/EQUIPMENT")
        equipmentStorage = FirebaseStorage.getInstance().reference.child("MUAPP/EQUIPMENT")



         root = inflater.inflate(R.layout.fragment_home, container, false)
        equipmentsProgress = root.findViewById(R.id.equipmentsProgress)
        addEquipmentFragment = root.findViewById(R.id.addEquipmentFragment)
        imageSlider= root.findViewById(R.id.image_slider)


        addEquipmentFragment.setOnClickListener {
            findNavController().navigate(R.id.addEquipmentFragment)
        }
        equipmentsProgress.visibility = View.VISIBLE

        getRecentImages()
        attachEquipments()



        return root
    }

    private fun getRecentImages() {

        val imageList:ArrayList<SlideModel> = ArrayList()


        val listAllTask: Task<ListResult> = equipmentStorage.list(10)
        listAllTask.addOnCompleteListener{ result ->
            val items: List<StorageReference> = result.result!!.items.reversed()

            //cycle for adding image URL to list
            items.forEachIndexed { index, item ->
                item.downloadUrl.addOnSuccessListener {
                    imageList.add( SlideModel(it.toString(), "Recent equipment",
                        ScaleTypes.CENTER_CROP))
                }.addOnCompleteListener{
                    //add to Image Slider

                    apply {

                       imageSlider.setImageList(imageList)
                        equipmentsProgress.visibility = View.GONE


                    }


                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun attachEquipments() {
//        equipmentsProgress.visibility = View.GONE


        val recyclerView = root.findViewById<RecyclerView>(R.id.recyclergroupchat)
        val gmanager= GridLayoutManager(root.context,2)

        recyclerView.layoutManager = gmanager

        val options = FirebaseRecyclerOptions.Builder<Equipment>()
            .setQuery(equipmentDb.limitToLast(200), Equipment::class.java).build()
        equipmentAdapter = EquipmentAdapter(options)
        recyclerView.adapter = equipmentAdapter
        equipmentAdapter.startListening()








    }
}