package app.mu.mulabassistant.ui.home

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import app.mu.mulabassistant.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import kotlin.collections.ArrayList


class AddEquipmentFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var etName:TextInputEditText
    private lateinit var etCost:TextInputEditText
    private lateinit var etDescription:TextInputEditText
    private lateinit var ivOne:ImageView
    private lateinit var ivTwo:ImageView
    private lateinit var ivThree:ImageView
    private lateinit var btnAddEquipment:ExtendedFloatingActionButton
    private lateinit var root:View
    private var GALLERY_REQUEST_ONE = 12345
    private var GALLERY_REQUEST_TWO= 95403
    private  var GALLERY_REQUEST_THREE= 23456

    private  var imgOneUri: Uri? = null
    private var  imgTwoUri:Uri?  =null
    private var imgThreeUri:Uri? = null
    private lateinit var rbBooked:RadioButton
    private lateinit var rbNotBooked:RadioButton
    private lateinit var mAuth:FirebaseAuth
    private lateinit var equipmentDb:DatabaseReference
    private lateinit var equipmentStorage:StorageReference
    private lateinit var pd:ProgressDialog



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
        ivTwo = root.findViewById(R.id.ivTwo)
        ivThree = root.findViewById(R.id.ivThree)

        btnAddEquipment =root.findViewById(R.id.btnAddEquipment)
        rbBooked = root.findViewById(R.id.rbBooked)
        rbNotBooked = root.findViewById(R.id.rbNotBooked)

        ivOne.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type ="image/*"
            startActivityForResult(intent,GALLERY_REQUEST_ONE)
        }
        ivTwo.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type ="image/*"
            startActivityForResult(intent,GALLERY_REQUEST_TWO)
        }
        ivThree.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type ="image/*"
            startActivityForResult(intent,GALLERY_REQUEST_THREE)
        }
        btnAddEquipment.setOnClickListener {
            val images= arrayListOf<ImageView?>()

            val name = etName.text.toString()
            val description = etDescription.text.toString()
            val cost = etCost.text.toString()

            val isBooked: Boolean

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(description) && !TextUtils.isEmpty(description) && !TextUtils.isEmpty(cost)){

                pd.show()
                isBooked = rbBooked.isChecked
                if(imgOneUri!=null && imgTwoUri!=null && imgThreeUri!=null) {
                    images.add(ivOne)
                    images.add(ivTwo)
                    images.add(ivThree)
                    uploadToFirebase(images,name,description,cost,isBooked)

                }else{
                    Toast.makeText(
                        root.context,
                        "Please upload atleast three images for the equipment",
                        Toast.LENGTH_SHORT
                    ).show()
                }









            }



        }

        return root
    }

    private fun uploadToFirebase(
        images: ArrayList<ImageView?>,
        name: String,
        description: String,
        cost: String,
        booked: Boolean
    ) {



        //upload imageone if available
        // Get the data from an ImageView as bytes
        images[0]!!.isDrawingCacheEnabled = true
        images[0]!!.buildDrawingCache()
        val bitmap = ( images[0]!!.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        val data = baos.toByteArray()

        val uploadTask = equipmentStorage.child("${System.currentTimeMillis()}.jpg").putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //image one uri
               task.result.storage.downloadUrl.addOnSuccessListener {uri->
                   val imageoneurl = uri.toString()
                   //go to second image


                   //upload imageone if available
                   // Get the data from an ImageView as bytes
                   images[1]!!.isDrawingCacheEnabled = true
                   images[1]!!.buildDrawingCache()
                   val bitmap1 = ( images[1]!!.drawable as BitmapDrawable).bitmap
                   val baos1 = ByteArrayOutputStream()
                   bitmap1.compress(Bitmap.CompressFormat.JPEG, 50, baos)
                   val data1 = baos1.toByteArray()

                   val uploadTask1 = equipmentStorage.child("${System.currentTimeMillis()}.jpg").putBytes(data1)
                   uploadTask1.addOnFailureListener {
                       // Handle unsuccessful uploads
                   }.addOnCompleteListener { task1 ->
                       if (task1.isSuccessful) {
                           task1.result.storage.downloadUrl.addOnSuccessListener { uri2->
                               //image one uri
                               val imageotwourl = uri2.toString()
                               //go to third image


                               // Get the data from an ImageView as bytes
                               images[2]!!.isDrawingCacheEnabled = true
                               images[2]!!.buildDrawingCache()
                               val bitmap2 = ( images[2]!!.drawable as BitmapDrawable).bitmap
                               val baos2 = ByteArrayOutputStream()
                               bitmap2.compress(Bitmap.CompressFormat.JPEG, 50, baos2)
                               val data2 = baos2.toByteArray()

                               val uploadTask2 = equipmentStorage.child("${System.currentTimeMillis()}.jpg").putBytes(data2)
                               uploadTask2.addOnFailureListener {
                                   // Handle unsuccessful uploads
                               }.addOnCompleteListener { task2 ->
                                   if (task2.isSuccessful) {
                                       task2.result.storage.downloadUrl.addOnSuccessListener { uri3->
                                           //image one uri
                                           val imagethreeurl =uri3.toString()
                                           //go to second image


                                           //At this point we all the images uploaded and grabbed url thur ready for upload.

                                           val key = equipmentDb.push().key.toString()
                                           val equipmentmap = HashMap<String,Any>()
                                           equipmentmap["id"] = key
                                           equipmentmap["name"] = name
                                           equipmentmap["description"] = description
                                           equipmentmap["cost"]= cost.toDouble()
                                           equipmentmap["isbooked"] = booked
                                           equipmentmap["imageone"]= imageoneurl
                                           equipmentmap["imagetwo"] = imageotwourl
                                           equipmentmap["imagethree"] = imagethreeurl
                                           val todaydate = System.currentTimeMillis()
                                           equipmentmap["date"]= todaydate

                                           val availabilitydate = System.currentTimeMillis()
                                           equipmentmap["avaibalilitydate"]= availabilitydate

                                           equipmentDb.child(key).updateChildren(equipmentmap).addOnCompleteListener { uploadRes->
                                               pd.dismiss()
                                               if (uploadRes.isSuccessful){
                                                   findNavController().popBackStack(R.id.navigation_home,false)

                                               }
                                               else{
                                                   Toast.makeText(root.context, uploadRes.exception?.message, Toast.LENGTH_SHORT).show()
                                               }


                                           }





                                       }




                                   } else {
                                       Toast.makeText(root.context, task2.exception?.message, Toast.LENGTH_SHORT).show()


                                   }
                               }










                           }



                       } else {
                           Toast.makeText(root.context, task1.exception?.message, Toast.LENGTH_SHORT).show()


                       }
                   }








               }


            } else {
                Toast.makeText(root.context, task.exception?.message, Toast.LENGTH_SHORT).show()


            }
        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_ONE &&  resultCode == RESULT_OK){
            imgOneUri = data?.data
            ivOne.setImageURI(imgOneUri)

        }else if(requestCode == GALLERY_REQUEST_TWO &&  resultCode == RESULT_OK){
            imgTwoUri = data?.data
            ivTwo.setImageURI(imgTwoUri)


        }else if(requestCode == GALLERY_REQUEST_THREE &&  resultCode == RESULT_OK){
            imgThreeUri = data?.data
            ivThree.setImageURI(imgThreeUri)

        }


    }




}