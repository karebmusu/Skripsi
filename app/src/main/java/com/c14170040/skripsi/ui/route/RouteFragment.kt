package com.c14170040.skripsi.ui.route

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context.LOCATION_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.c14170040.skripsi.*
import com.c14170040.skripsi.ui.Direction
import com.c14170040.skripsi.ui.DistanceAPI
import com.c14170040.skripsi.ui.MapsAPI
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.maps.android.PolyUtil
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.dialog_assign.*
import kotlinx.android.synthetic.main.fragment_route.*
import kotlinx.coroutines.*
import kotlin.system.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

@Parcelize
data class Mesin(
    val latLng: LatLng,
    val namaMesin : String,
    val provinsi : String
) : Parcelable
var jarak = 0
var besar = 0
var mesin = arrayListOf<Mesin>()
var mesin2 = arrayListOf<Mesin>()
var mesinhasil = arrayListOf<ArrayList<Mesin>>()
private var locationManager : LocationManager? = null
class RouteFragment : Fragment() {
    private var markers = mutableListOf<Marker>()
    private lateinit var mMap: GoogleMap
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private lateinit var storageReference: StorageReference
    var bol = true
    var arrJ = arrayListOf<ArrayList<Int>>()
    var arrS = arrayListOf<ArrayList<Int>>()
    var sh = 0
    override fun onResume() {
        super.onResume()
        bt_rute.visibility=View.INVISIBLE
        jarak=0
        besar=0
        mesinhasil.clear()
        mesin.clear()
        mesin.clear()
        arrJ.clear()
        arrS.clear()
        bt_rute.setOnClickListener {
            val retrofit : Retrofit = Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            if(sh<=mesinhasil.size-1)
            {
                for(i in 0..mesinhasil[sh].size-2)
                {
                    Retrof(mesinhasil[sh][i].latLng, mesinhasil[sh][i+1].latLng,retrofit)
                }
            }
            sh++
        }
    }
    private val callback = OnMapReadyCallback { googleMap ->
        mMap=googleMap
        val retrofit : Retrofit = Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        var db = FirebaseFirestore.getInstance()
        var area = arrayListOf<String>()

//        locationManager = activity?.getSystemService(LOCATION_SERVICE) as LocationManager?
//        val hasGps = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)
//        val hasNetwork = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        var locSales = LatLng(latuser, lnguser)
        var Sales = Mesin(locSales,"Sales","Sales sekarang")
        mesin.add(Sales)
        mesin2.add(Sales)
        db.collection("Provinsi").get().addOnSuccessListener {
            document ->
            for(doc in document){
                var dataf = doc.data as MutableMap<String,String>
                Log.d("area email", dataf.getValue("Email Sales").toString())
                if(dataf.getValue("Email Sales").toString() == emailuser)
                {
                    area.add(dataf.getValue("Nama").toString())
                    Log.d("area", dataf.getValue("Nama").toString())
                }
            }

            Log.d("area", area.toString())
            db.collection("mesin").get().addOnSuccessListener { docu ->
                val boundsBuilder = LatLngBounds.Builder()
                for (ar in area) {
                    Log.d("areas", ar)
                    for (docs in docu) {
                        var datae = docs.data as MutableMap<String, String>
                        if (datae.getValue("Provinsi").toString() == ar) {
                            if(datae.getValue("Status").toString()=="belum")
                            {
                                val latLng = LatLng(datae.getValue("Latitude").toDouble(), datae.getValue("Longitude").toDouble())
                                var m = Mesin(latLng, datae.getValue("Nama Mesin").toString(), datae.getValue("Provinsi").toString())
                                mesin.add(m)
                                mesin2.add(m)
                                boundsBuilder.include(latLng)
                            }
                            //googleMap.addMarker(MarkerOptions().position(latLng).title(datae.getValue("Nama").toString()).snippet("Lokasi : "+datae.getValue("Provinsi")))
                        }
                    }
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 1000, 1000, 0))
                }
                besar=mesin.size
//                val latLng = LatLng(mesin[0].latLng.latitude,mesin[0].latLng.longitude)
//                val des = LatLng(mesin[1].latLng.latitude,mesin[1].latLng.longitude)

                for ((i, mesinn) in mesin.withIndex()) {
                    var ar = arrayListOf<Int>()
                    arrJ.add(ar)
                    for ((j, mesinn2) in mesin2.withIndex()) {
                        arrJ[i].add(0)
                    }
                }
                for(i in 0..mesin.size-2)
                {
                    var ar = arrayListOf<Int>()
                    arrS.add(ar)
                    for(j in 0..mesin.size-2)
                    {
                        arrS[i].add(0)
                    }
                }
                Log.d("SaveM", mesin.toString())
                for ((i,mesinn) in mesin.withIndex())
                {
                    for((j,mesinn2) in mesin2.withIndex())
                    {
                        val ori = LatLng(mesin[i].latLng.latitude,mesin[i].latLng.longitude)
                        val des = LatLng(mesin2[j].latLng.latitude,mesin2[j].latLng.longitude)
                        Log.d("JarakLoop","I = "+i.toString()+" J = "+j.toString())
                        if(j==mesin2.size-1)
                        {
                            if(i==mesin.size-1){
                                bol=false
                            }
                        }
                        RetrofJarak(ori,des,retrofit,bol,i,j)
                    }
                    val latLng = LatLng(mesinn.latLng.latitude,mesinn.latLng.longitude)
//                    if(i!=mesin.size-1){
//                        val des = LatLng(mesin[i+1].latLng.latitude,mesin[i+1].latLng.longitude)
//                        //Retrof(latLng,des,retrofit)
//                    }
                    if(mesinn.namaMesin=="Sales")
                    {
                        val marker = googleMap.addMarker(MarkerOptions().position(latLng).title(mesinn.namaMesin).snippet(mesinn.provinsi).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)))
                        markers.add(marker)
                    }
                    else
                    {
                        val marker = googleMap.addMarker(MarkerOptions().position(latLng).title(mesinn.namaMesin).snippet(mesinn.provinsi))
                        markers.add(marker)
                    }

                }
            }
        }
        storageReference = FirebaseStorage.getInstance().reference
        googleMap.setOnInfoWindowClickListener { markerToDelete ->
            val placeFormView = LayoutInflater.from(this.context).inflate(R.layout.dialog_assign,null)
            val dialog = AlertDialog.Builder(this.context).setTitle("Mark as done")
                    .setView(placeFormView)
                    .setNegativeButton("Cancel",null)
                    .setPositiveButton("Ok",null)
                    .show()
            dialog.tv_NamaMesin.setText(markerToDelete.title)
            dialog.tv_NamaArea.setText(markerToDelete.snippet)
            dialog.bt_uploadfoto.setOnClickListener {
                launchGallery()
            }
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                Log.d("marker", markerToDelete.title.toString())
                val getTgl = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"))
                val sdf1 = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                val formatted = sdf1.format(getTgl.time)
                if(markerToDelete.title.toString() != "Sales")
                {
                    val datalapor = hashMapOf(
                            "Nama Sales" to namauser,
                            "Email Sales" to emailuser,
                            "Tanggal" to formatted.toString(),
                            "Nama Mesin" to markerToDelete.title,
                            "Area" to markerToDelete.snippet,
                            "Jenis Mesin" to dialog.et_JenisMesin.text.toString(),
                            "Pemilik" to dialog.et_Pemilik.text.toString(),
                            "Catatan" to dialog.et_Catatan.text.toString(),
                            "Foto" to formatted.toString()+dialog.tv_NamaMesin.text.toString()+"-"+ emailuser
                    )
                    db.collection("lapor").document(formatted.toString()+"-"+markerToDelete.title+ "-" +emailuser)
                            .set(datalapor).addOnSuccessListener {
                                uploadImg(formatted.toString()+markerToDelete.title,formatted.toString()+"-"+markerToDelete.title+"-"+ emailuser)
                            }
                    val data = mapOf<String,String>("Status" to "sudah",
                        "Kunjungan" to formatted.toString())
                    db.collection("mesin").document(markerToDelete.title.toString()).update(data)
                            .addOnSuccessListener {
                                markers.remove(markerToDelete)
                                markerToDelete.remove()
                                Toast.makeText(this.context,"Lapor sukses dan Berhasil ditandai menjadi selesai", Toast.LENGTH_SHORT).show()
                            }
                    dialog.dismiss()
                }
                else{
                    Toast.makeText(this.context,"tidak bisa merubah status sales", Toast.LENGTH_SHORT).show()
                }
            }
        }

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

    fun findroute(bool: Boolean, i : Int, j : Int)
    {
        Log.d("TesJarakT", jarak.toString())
        arrJ[i][j]= jarak
        var arrSM = arrayListOf<ArrayList<Int>>()
        if(!bool)
        {
            bol=true
            var sum1=0
            Log.d("TesJarakA", arrJ.toString())
            for(i in 0..besar-2)
            {
                for(j in 0..besar-2)
                {
                    if(i==j)
                    {
                        arrS[i][j]=0
                    }
                    arrS[i][j] = arrJ[0][i+1]+arrJ[0][j+1]-arrJ[i+1][j+1]
                }
            }
            Log.d("saving",arrS.toString())
            for(i in 0..besar-2)
            {
                for(j in 0..besar-2)
                {
                    var ar = arrayListOf<Int>(arrS[i][j],i,j)
                    if(i!=j)
                    {
                        arrSM.add(ar)
                    }
                }
            }
            Log.d("savingValue",arrSM.toString())
            val n = arrSM.size
            for (i in 0..n-2)
            {
                for (j in 0..n-i-2)
                {
                    if (arrSM[j][0] < arrSM[j + 1][0])
                    {
                        // swap arr[j+1] and arr[j]
                        val temp = arrSM[j][0]
                        val temp1 = arrSM[j][1]
                        val temp2 = arrSM[j][2]
                        val temp3 = arrSM[j + 1][0]
                        val temp4 = arrSM[j + 1][1]
                        val temp5 = arrSM[j + 1][2]
                        arrSM[j][0] = temp3
                        arrSM[j][1] = temp4
                        arrSM[j][2] = temp5
                        arrSM[j + 1][0] = temp
                        arrSM[j + 1][1] = temp1
                        arrSM[j + 1][2] = temp2
                    }
                }
            }
            Log.d("savingValue",arrSM.toString())
            var rute = arrayListOf<Int>(arrSM[0][1],arrSM[0][2])
            while (rute.size< besar-1)
            {
                for(i in 0..arrSM.size-1)
                {
                    if(arrSM[i][1]!=rute[rute.size-1]){
                        if(!rute.contains(arrSM[i][2]))
                        {
                            rute.add(arrSM[i][2])
                            break
                        }
                    }
                }
                Log.d("savingmatrix",rute.toString())
            }
            for(i in 0..rute.size-2)
            {
                sum1+= arrJ[i][i+1]
            }
            Log.d("savingmatrixHasil",sum1.toString())
            var sum=0
            var hmemory = arrayListOf<ArrayList<Int>>()
            var hmsave = arrayListOf<Int>()
            Log.d("harmony",hmsave.toString())
            //inisialisasi hm
            var temp = 1000000000
            for(hmrand in 1..300)
            {
                var hm = arrayListOf<Int>()

                while(hm.size!= mesin.size-1)
                {
                    var rn= (1..mesin.size-1).random()
                    if(!hm.contains(rn))
                    {
                        hm.add(rn)
                    }
                }
                hmemory.add(hm)
                Log.d("harmony",hm.toString())
                sum+=arrJ[0][hm[0]]
                for(i in 1..hm.size-1)
                {
                    sum+=arrJ[i][i+1]
                }
                Log.d("harmonyHasil",sum.toString())
                Log.d("harmonyHasil",temp.toString())
                if(temp>sum)
                {
                    temp=sum
                    sum=0
                    hmsave.clear()
                    for(isi in hm)
                    {
                        hmsave.add(isi)
                    }
                    Log.d("harmonyHasil",hmsave.toString())
                }
            }
            var pitch = (1..300).random()
            var ad = arrayListOf<Int>()
            for(i in 0..hmemory[pitch].size-1)
            {
                ad.add(hmemory[pitch][i])
            }
            for(hmrand in 1..300)
            {
                if((0..1).random()<countHMCR(HMCRmin,HMCRmax,300,hmrand))
                {
                    if((0..1).random()<countPAR(PARmin,PARmax,300,hmrand))
                    {
                        for(j in 0..mesin.size-2)
                        {
                            ad[j] = (ad[j]+(0..1).random()*countBW(BWmin,BWmax,300,hmrand)).toInt()
                        }

                    }
                }
                Log.d("Harmony2", ad.toString())
                sum=arrJ[0][ad[0]]
                for(i in 1..ad.size-1)
                {
                    sum+=arrJ[i][i+1]
                }
                Log.d("harmonyHasil2 sum",sum.toString())
                Log.d("harmonyHasil2 temp",temp.toString())
                if(temp>sum)
                {
                    temp=sum
                    sum=0
                    hmsave.clear()
                    for(isi in ad)
                    {
                        hmsave.add(isi)
                    }
                }
            }
            Log.d("SavingMatrixsum",sum1.toString())
            Log.d("harmonysum",temp.toString())
            if(sum1<temp)
            {
                Log.d("Final Sum sm", rute.toString())
                val retrofit : Retrofit = Retrofit.Builder()
                        .baseUrl("https://maps.googleapis.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                var arrTemp = arrayListOf<Mesin>()
                var c = mesin.size/3
                //Retrof(mesin[0].latLng, mesin[rute[0]+1].latLng,retrofit)
                arrTemp.add(mesin[0])
                var ctr =0
                for(i in 0..rute.size-1)
                {
                   arrTemp.add(mesin[rute[i]+1])
                    ctr++
                    if(ctr==1)
                    {
                        var arrt = arrayListOf<Mesin>()
                        for(j in 0..arrTemp.size-1)
                        {
                            arrt.add(arrTemp[j])
                        }
                        mesinhasil.add(arrt)
                        arrTemp.clear()
                        arrTemp.add(mesin[rute[i]+1])
                        ctr=0
                    }
                   // Retrof(mesin[rute[i]+1].latLng, mesin[rute[i+1]+1].latLng,retrofit)
                }
                Log.d("hasil", mesinhasil.toString())
                bt_rute.visibility=View.VISIBLE
            }
            else
            {
                Log.d("Final Sum hs", hmsave.toString())
                val retrofit : Retrofit = Retrofit.Builder()
                        .baseUrl("https://maps.googleapis.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                bt_rute.visibility=View.VISIBLE
                var c = mesin.size/3
                //Retrof(mesin[0].latLng, mesin[hmsave[0]].latLng,retrofit)
                var ctr =0
                var arrTemp = arrayListOf<Mesin>()
                arrTemp.add(mesin[0])
                for(i in 0..hmsave.size-2)
                {
                    arrTemp.add(mesin[hmsave[i]])
                    ctr++
                    if(ctr==1)
                    {
                        var arrt = arrayListOf<Mesin>()
                        for(j in 0..arrTemp.size-1)
                        {
                            arrt.add(arrTemp[j])
                        }
                        mesinhasil.add(arrt)
                        arrTemp.clear()
                        arrTemp.add(mesin[hmsave[i]])
                        ctr=0
                    }
                    //Retrof(mesin[hmsave[i]].latLng, mesin[hmsave[i+1]].latLng,retrofit)
                }
            }
            //Log.d("hasil", mesinhasil[0].toString())

        }
    }
    var HMCRmin = 0.7
    var HMCRmax = 0.99
    fun countHMCR(hmcrmin : Double, hmcrmax : Double, iterasi : Int, ind : Int) : Double
    {
        return hmcrmin+(((hmcrmax-hmcrmin)/iterasi)*ind)
    }
    var PARmin = 0.1
    var PARmax = 0.5
    fun countPAR (parMin : Double, parMax : Double, iterasi: Int,ind: Int):Double
    {
        return parMin+(((parMax-parMin)/iterasi)*ind)
    }
    var BWmin = 0.01
    var BWmax = 0.001
    fun countBW(bwMin : Double, bwMax:Double,iterasi: Int,ind: Int):Double{
        return bwMin+(((bwMax-bwMin)/iterasi)*ind)
    }

    fun findRouteHM(bool: Boolean,i : Int,j : Int) {
        Log.d("TesJarakT", jarak.toString())
        arrJ[i][j]= jarak

        if(!bool) {
            var sum=0
            var hmemory = arrayListOf<ArrayList<Int>>()
            var hmsave = arrayListOf<Int>()
            Log.d("harmony",hmsave.toString())
            //inisialisasi hm
            var temp = 1000000000
            for(hmrand in 1..300)
            {
                var hm = arrayListOf<Int>()

                while(hm.size!= mesin.size-1)
                {
                    var rn= (1..mesin.size-1).random()
                    if(!hm.contains(rn))
                    {
                        hm.add(rn)
                    }
                }
                hmemory.add(hm)
                Log.d("harmony",hm.toString())
                sum+=arrJ[0][hm[0]]
                for(i in 1..hm.size-1)
                {
                    sum+=arrJ[i][i+1]
                }
                Log.d("harmonyHasil",sum.toString())
                Log.d("harmonyHasil",temp.toString())
                if(temp>sum)
                {
                    temp=sum
                    sum=0
                    hmsave.clear()
                    for(isi in hm)
                    {
                        hmsave.add(isi)
                    }
                    Log.d("harmonyHasil",hmsave.toString())
                }
            }
            sum=0
            var pitch = (1..300).random()
            var ad = arrayListOf<Int>()
            for(i in 0..hmemory[pitch].size-1)
            {
                ad.add(hmemory[pitch][i])
            }
            for(hmrand in 1..300)
            {
                if((0..1).random()<countHMCR(HMCRmin,HMCRmax,300,hmrand))
                {
                    if((0..1).random()<countPAR(PARmin,PARmax,300,hmrand))
                    {
                        for(j in 0..mesin.size-2)
                        {
                            ad[j] = (ad[j]+(0..1).random()*countBW(BWmin,BWmax,300,hmrand)).toInt()
                        }

                    }
                }
                Log.d("Harmony2", ad.toString())
                sum=arrJ[0][ad[0]]
                for(i in 1..ad.size-1)
                {
                    sum+=arrJ[i][i+1]
                }
                Log.d("harmonyHasil2 sum",sum.toString())
                Log.d("harmonyHasil2 temp",temp.toString())
                if(temp>sum)
                {
                    temp=sum
                    sum=0
                    hmsave.clear()
                    for(isi in ad)
                    {
                        hmsave.add(isi)
                    }
                }
            }

            val retrofit : Retrofit = Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            Retrof(mesin[0].latLng, mesin[hmsave[0]].latLng,retrofit)
            for(i in 0..hmsave.size-2)
            {
                Retrof(mesin[hmsave[i]].latLng, mesin[hmsave[i+1]].latLng,retrofit)
            }
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_route, container, false)
    }

    fun Retrof(origin : LatLng,dest : LatLng, retrofit: Retrofit)
    {
        var dirApi : MapsAPI = retrofit.create(MapsAPI::class.java)
        var call : Call<Direction> = dirApi.getData(origin.latitude.toString()+","+origin.longitude.toString(),dest.latitude.toString()+","+dest.longitude.toString(), API_KeY)
        call.enqueue(object : Callback<Direction> {
            override fun onResponse(call: Call<Direction>, response: retrofit2.Response<Direction>) {
                if(response.code() != 200){
                    Log.d("status","failed")
                    return
                }
                var dir : Direction = response.body()!!
                Log.d("status",dir.status)
                addPolyline(dir,mMap)
            }
            override fun onFailure(call: Call<Direction>, t: Throwable) {
            }
        })
    }
    fun RetrofJarak(origin: LatLng, dest: LatLng,retrofit: Retrofit, bool : Boolean, i:Int,j:Int)
    {
        var jarAPI : DistanceAPI =retrofit.create(DistanceAPI::class.java)
        var call : Call<Jarak> = jarAPI.getData("metric",origin.latitude.toString()+","+origin.longitude.toString(),dest.latitude.toString()+","+dest.longitude.toString(), API_KeY)
        //true
        call.enqueue(object : Callback<Jarak> {
            override fun onResponse(call: Call<Jarak>, response: retrofit2.Response<Jarak>) {
                if(response.code() != 200){
                    Log.d("status","failed")
                }
                var jar : Jarak = response.body()!!
                Log.d("statusJ",jar.status.toString())
                Log.d("Jarak", jar.rows[0]!!.elements[0]!!.distance.text)
                jarak = jar.rows[0]!!.elements[0]!!.distance.value
                Log.d("JarakM",origin.toString()+" ke "+dest)
                findroute(bool,i,j)
                //findRouteHM(bool,i,j)
            }
            override fun onFailure(call: Call<Jarak>, t: Throwable) {
            }
        })
    }


    fun jeda() = runBlocking {
        launch {
            delay(5000L)
            Log.d("TesJarakJ", jarak.toString())
        }
    }
    private fun addPolyline(results:Direction, mMap:GoogleMap) {
        val decodedPath = PolyUtil.decode(results.routes[0]!!.overviewPolyline.points)
        mMap.addPolyline(PolylineOptions().addAll(decodedPath).color(R.color.black))
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}