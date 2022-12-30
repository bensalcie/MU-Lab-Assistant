package com.mu.labassistant.studentapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
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
import com.mu.labassistant.studentapp.databinding.FragmentHomeBinding
import com.mu.labassistant.studentapp.ui.home.adapters.EquipmentAdapter
import com.mu.labassistant.studentapp.ui.home.models.Equipment

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var equipmentDb: DatabaseReference
    private lateinit var equipmentAdapter: EquipmentAdapter
    private lateinit var equipmentStorage: StorageReference


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        equipmentDb = FirebaseDatabase.getInstance().reference.child("MUAPP/EQUIPMENT")
        equipmentStorage = FirebaseStorage.getInstance().reference.child("MUAPP/EQUIPMENT")





        binding.equipmentsProgress.visibility = View.VISIBLE

        getRecentImages()
        attachEquipments()


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
                        ScaleTypes.CENTER_CROP)
                    )
                }.addOnCompleteListener{
                    //add to Image Slider

                    apply {

                        binding.imageSlider.setImageList(imageList)
                       binding. equipmentsProgress.visibility = View.GONE


                    }


                }
            }
        }
    }




    private fun attachEquipments() {


        val gmanager= GridLayoutManager(binding.root.context,2)

        binding.recyclerequipments.layoutManager = gmanager

        val options = FirebaseRecyclerOptions.Builder<Equipment>()
            .setQuery(equipmentDb.limitToLast(200), Equipment::class.java).build()
        equipmentAdapter = EquipmentAdapter(options)
        binding.recyclerequipments.adapter = equipmentAdapter
        equipmentAdapter.startListening()








    }
}