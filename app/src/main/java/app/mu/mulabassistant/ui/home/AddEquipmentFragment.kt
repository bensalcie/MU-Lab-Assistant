package app.mu.mulabassistant.ui.home

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import app.mu.mulabassistant.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import java.io.ByteArrayOutputStream


class AddEquipmentFragment : Fragment() ,AdapterView.OnItemSelectedListener{

    private lateinit var etName:TextInputEditText
    private lateinit var etCost:TextInputEditText
    private lateinit var etDescription:TextInputEditText
    private lateinit var ivOne:ImageView
    private lateinit var btnAddEquipment:ExtendedFloatingActionButton
    private lateinit var root:View
    private var GALLERY_REQUEST_ONE = 12345
    private  var imgOneUri: Uri? = null
    private var labSpinner:Spinner?=null

    private lateinit var rbBooked:RadioButton
    private lateinit var rbNotBooked:RadioButton
    private lateinit var mAuth:FirebaseAuth
    private lateinit var equipmentDb:DatabaseReference
    private lateinit var equipmentStorage:StorageReference
    private lateinit var pd:ProgressDialog
    var selectedLanguage: String?=null
    lateinit var languages : ArrayList<String>






    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {



        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_add_equipment, container, false)
        mAuth = FirebaseAuth.getInstance()
        pd = ProgressDialog(root.context)
        pd.setTitle("Uploading Equipment")
        pd.setMessage("Please hold up as we complete uploading equipment...")
        equipmentDb = FirebaseDatabase.getInstance().reference.child("MUAPP/EQUIPMENT")
        equipmentStorage = FirebaseStorage.getInstance().reference.child("MUAPP/EQUIPMENT")



        etName= root.findViewById(R.id.etName)
        etCost = root.findViewById(R.id.etCost)
        etDescription = root.findViewById(R.id.etDescription)
        ivOne = root.findViewById(R.id.ivOne)
        labSpinner = root.findViewById(R.id.labSpinner)


        btnAddEquipment =root.findViewById(R.id.btnAddEquipment)
        rbBooked = root.findViewById(R.id.rbBooked)
        rbNotBooked = root.findViewById(R.id.rbNotBooked)
         languages = resources.getStringArray(R.array.Languages).toList() as ArrayList<String>


        ivOne.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type ="image/*"
            startActivityForResult(intent,GALLERY_REQUEST_ONE)
        }

        btnAddEquipment.setOnClickListener {

            val name = etName.text.toString()
            val description = etDescription.text.toString()
            val cost = etCost.text.toString()

            val isBooked: Boolean

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(description) && !TextUtils.isEmpty(description) && !TextUtils.isEmpty(cost)){

                pd.show()
                isBooked = rbBooked.isChecked
                if(imgOneUri!=null) {
                     uploadToFirebase(name,description,cost,isBooked, imgOneUri!!)

                }else{
                    Toast.makeText(
                        root.context,
                        "Please add an image to your equipment",
                        Toast.LENGTH_SHORT
                    ).show()
                }





            }



        }

        if (labSpinner != null) {
            val adapter = ArrayAdapter(root.context,
                android.R.layout.simple_spinner_item, languages)
            labSpinner!!.adapter = adapter

            labSpinner!!.onItemSelectedListener = this

        }

        return root
    }

    private  fun uploadToFirebase(
        name: String,
        description: String,
        cost: String,
        booked: Boolean,
        imgOneUri: Uri
    ) {

// Get the data from an ImageView as bytes
        ivOne.isDrawingCacheEnabled = true
        ivOne.buildDrawingCache()
        val bitmap = (ivOne.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos)
        val imagedata = baos.toByteArray()


        val ref = equipmentStorage.child("${System.currentTimeMillis()}.jpg")
        val imageTask = ref.putBytes(imagedata)

        imageTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    pd.dismiss()
                    Toast.makeText(root.context, task.exception?.message, Toast.LENGTH_SHORT).show()

                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Uploading", "Upload successful: ")

                val downloadUri = task.result
                Log.d("Uploading", "Upload successful: url $downloadUri ")

                //At this point we all the images uploaded and grabbed url thur ready for upload.

                val key = equipmentDb.push().key.toString()
                val equipmentmap = HashMap<String,Any>()
                equipmentmap["id"] = key
                equipmentmap["name"] = name
                equipmentmap["description"] = description
                equipmentmap["cost"]= cost.toDouble()
                equipmentmap["isbooked"] = booked
                equipmentmap["imageone"]= downloadUri.toString()
                val todaydate = System.currentTimeMillis()
                equipmentmap["date"]= todaydate
                equipmentmap["labcategory"] = selectedLanguage!!

                val availabilitydate = System.currentTimeMillis()
                equipmentmap["avaibalilitydate"]= availabilitydate

                Log.d("Uploading", "Upload database: map $equipmentmap ")


                equipmentDb.child(key).updateChildren(equipmentmap).addOnCompleteListener { uploadRes->
                    if (uploadRes.isSuccessful){
                        Log.d("Uploading", "Upload database: successful ")

                        pd.dismiss()

                        findNavController().popBackStack(R.id.navigation_home,false)

                    } else{
                        Log.d("Uploading", "Upload database: successful ")

                        pd.dismiss()

                        Toast.makeText(root.context, uploadRes.exception?.message, Toast.LENGTH_SHORT).show()
                    }


                }


            } else {
               pd.dismiss()
                Toast.makeText(root.context, task.exception?.message, Toast.LENGTH_SHORT).show()

            }
        }.addOnFailureListener {
            Log.d("Uploading", "Upload failed: ${it.message} ")

        }






    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_ONE &&  resultCode == RESULT_OK){
            imgOneUri = data?.data
            ivOne.setImageURI(imgOneUri)

        }

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        selectedLanguage = languages[p2]

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        selectedLanguage = languages[0]


    }


}