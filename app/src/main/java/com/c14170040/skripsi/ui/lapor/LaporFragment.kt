package com.c14170040.skripsi.ui.lapor

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.c14170040.skripsi.R
import com.c14170040.skripsi.emailuser
import com.c14170040.skripsi.namauser
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_lapor.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LaporFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LaporFragment : Fragment() {
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private lateinit var storageReference: StorageReference

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lapor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var db = FirebaseFirestore.getInstance()
//        val current = LocalDateTime.now()
        val getTgl = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"))
        val sdf1 = SimpleDateFormat("dd-MM-yyyy", Locale.US)
//        val formatter = DateTimeFormatter.BASIC_ISO_DATE
        val formatted = sdf1.format(getTgl.time)



        storageReference = FirebaseStorage.getInstance().reference
        bt_upload.setOnClickListener {
            launchGallery()
        }
        bt_addMesin.setOnClickListener {
            Log.d("showTgl", formatted.toString())
            val data = hashMapOf(
                    "Nama Sales" to namauser,
                    "Email Sales" to emailuser,
                    "Tanggal" to formatted.toString(),
                    "Nama Mesin" to etNamaMesin.text.toString(),
                    "Jenis Mesin" to etLong.text.toString(),
                    "Catatan" to etProvinsi.text.toString(),
                    "Pemilik" to etLat.text.toString(),
                    "Foto" to formatted.toString()+etNamaMesin.text.toString()
            )

            Log.d("showall", data.toString())
            db.collection("lapor").document(formatted.toString() + "-" + etNamaMesin.text.toString() + "-" + emailuser)
                    .set(data).addOnSuccessListener {
                        uploadImg(formatted.toString()+etNamaMesin.text.toString(),formatted.toString() + "-" + etNamaMesin.text.toString() + "-" + emailuser)
                        Toast.makeText(context, "Lapor Success", Toast.LENGTH_SHORT).show()
                        etNamaMesin.text.clear()
                        etLat.text.clear()
                        etLong.text.clear()
                        etProvinsi.text.clear()
                    }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }
    private fun uploadRecordToDb(uri : String,docname: String)
    {
        val data = mapOf<String,String>("Foto" to uri)
        val db = FirebaseFirestore.getInstance()
        db.collection("lapor").document(docname).update(data)
    }
    private fun uploadImg(namagambar : String,docname : String) {
        if(filePath != null){
            val ref = storageReference?.child("images/" + namagambar)
            val uploadTask = ref?.putFile(filePath!!)
            Log.d("upload1",namagambar)
            val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                    Log.d("upload2","berhasil")
                }
                return@Continuation ref.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    uploadRecordToDb(downloadUri.toString(),docname)
                    Log.d("upload3","berhasil")
                } else {
                    // Handle failures
                }
            }?.addOnFailureListener{
            }
        }
        else{
            Toast.makeText(context, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, filePath)
                Log.d("upload",filePath.toString())

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LaporFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LaporFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}