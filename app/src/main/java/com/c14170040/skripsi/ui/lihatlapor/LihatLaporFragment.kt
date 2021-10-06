package com.c14170040.skripsi.ui.lihatlapor

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.c14170040.skripsi.*
import com.c14170040.skripsi.ui.adapterLapor
import com.c14170040.skripsi.ui.laporan
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.dialog_filter.*
import kotlinx.android.synthetic.main.dialog_filter_area.*
import kotlinx.android.synthetic.main.dialog_filter_tanggal.*
import kotlinx.android.synthetic.main.fragment_lihat_lapor.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LihatLaporFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LihatLaporFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var tglawal = ""
    var tglakhir = ""
    lateinit var show : ArrayList<laporan>
    var vGlobal = varGlobal()
    var db = FirebaseFirestore.getInstance()
    lateinit var adapter : adapterLapor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        db= FirebaseFirestore.getInstance()
        db.collection("lapor")
                .get().addOnSuccessListener {
                    document ->
                    vGlobal.clearlapor()
                    for(doc in document)
                    {
                        var datal = doc.data as MutableMap<String,String>
                        val data = laporan(
                            datal.getValue("Nama Sales"),
                            datal.getValue("Email Sales"),
                            datal.getValue("Nama Mesin"),
                            datal.getValue("Jenis Mesin"),
                            datal.getValue("Catatan"),
                            datal.getValue("Tanggal"),
                            datal.getValue("Foto"),
                            datal.getValue("Pemilik")
                        )
                        vGlobal.addlapor(data)
                    }
                    show = vGlobal.getlapor()
                    Showdata()
                }
        return inflater.inflate(R.layout.fragment_lihat_lapor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fabfilter.setOnClickListener{
            val placeFormViewTanggal = LayoutInflater.from(context).inflate(R.layout.dialog_filter_tanggal,null)
            val placeFormViewFilter = LayoutInflater.from(context).inflate(R.layout.dialog_filter,null)
            val placeFormViewSales = LayoutInflater.from(context).inflate(R.layout.dialog_filter_area,null)

            val dialogFilter = AlertDialog.Builder(context).setTitle("Filter")
                    .setView(placeFormViewFilter)
                    .setPositiveButton("Ok",null)
                    .show()
            dialogFilter.bt__Area.setOnClickListener {
                dialogFilter.dismiss()
                val dialogFilterArea = AlertDialog.Builder(context).setTitle("Filter by Area")
                        .setView(placeFormViewSales)
                        .setPositiveButton("Ok",null)
                        .show()
                dialogFilterArea.spFilter.adapter = ArrayAdapter(activity?.applicationContext!!,R.layout.support_simple_spinner_dropdown_item, listarea)
                dialogFilterArea.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener{
                    Log.d("areafilter",dialogFilterArea.spFilter.selectedItem.toString())
                    db.collection("lapor")
                            .get().addOnSuccessListener {
                                document ->
                                vGlobal.clearlapor()
                                for(doc in document)
                                {
                                    var datal = doc.data as MutableMap<String,String>
                                    val data = laporan(
                                            datal.getValue("Nama Sales"),
                                            datal.getValue("Email Sales"),
                                            datal.getValue("Nama Mesin"),
                                            datal.getValue("Jenis Mesin"),
                                            datal.getValue("Catatan"),
                                            datal.getValue("Tanggal"),
                                            datal.getValue("Foto"),
                                            datal.getValue("Pemilik")
                                    )

                                    if(datal.getValue("Area").toString()==dialogFilterArea.spFilter.selectedItem.toString())
                                    {
                                        vGlobal.addlapor(data)
                                    }
                                }
                                show = vGlobal.getlapor()
                                Showdata()
                            }
                    dialogFilterArea.dismiss()
                }
            }
            dialogFilter.bt__Sales.setOnClickListener {
                dialogFilter.dismiss()
                val dialogFilterSales = AlertDialog.Builder(context).setTitle("Filter by Sales")
                        .setView(placeFormViewSales)
                        .setPositiveButton("Ok",null)
                        .show()
                dialogFilterSales.spFilter.adapter = ArrayAdapter(activity?.applicationContext!!,R.layout.support_simple_spinner_dropdown_item, listStringuser)
                dialogFilterSales.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                    db.collection("lapor")
                            .get().addOnSuccessListener {
                                document ->
                                vGlobal.clearlapor()
                                for(doc in document)
                                {
                                    var datal = doc.data as MutableMap<String,String>
                                    val data = laporan(
                                            datal.getValue("Nama Sales"),
                                            datal.getValue("Email Sales"),
                                            datal.getValue("Nama Mesin"),
                                            datal.getValue("Jenis Mesin"),
                                            datal.getValue("Catatan"),
                                            datal.getValue("Tanggal"),
                                            datal.getValue("Foto"),
                                            datal.getValue("Pemilik")
                                    )
                                    if(datal.getValue("Nama Sales")==dialogFilterSales.spFilter.selectedItem.toString())
                                    {
                                        vGlobal.addlapor(data)
                                    }
                                }
                                show = vGlobal.getlapor()
                                Showdata()
                            }
                    dialogFilterSales.dismiss()
                }
            }
            dialogFilter.btTgl.setOnClickListener {
                dialogFilter.dismiss()
                val dialogfilterTanggal = AlertDialog.Builder(context).setTitle("Filter by Date")
                        .setView(placeFormViewTanggal)
                        .setNegativeButton("Cancel",null)
                        .setPositiveButton("OK",null)
                        .show()
                val newCalender = Calendar.getInstance()
                dialogfilterTanggal.btTglAwal.setOnClickListener {
                    val dialogtime = DatePickerDialog(
                            requireContext(),
                            { view, year, month, dayOfMonth ->
                                val newDate = Calendar.getInstance()
                                var hourOfDay = 0
                                var minute= 0
                                newDate[year, month, dayOfMonth, hourOfDay, minute] = 0
                                val tem = Calendar.getInstance()
                                Log.w("TIMEE", System.currentTimeMillis().toString() + "")
                                if (newDate.timeInMillis - tem.timeInMillis < 0){
                                    val myFormat = "EEE MMM dd HH:mm:ss zzz yyyy"
                                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                                    var datehutang = sdf.parse(newDate.time.toString())
                                    val cal = Calendar.getInstance()
                                    cal.time = datehutang

                                    val sdf1 = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                                    val date1 = sdf1.format(cal.time)
                                    dialogfilterTanggal.btTglAwal.setText(date1.toString())
                                } else Toast.makeText(context, "Invalid time", Toast.LENGTH_SHORT)
                                        .show()
                            }, newCalender.get(Calendar.YEAR), newCalender.get(Calendar.MONTH), newCalender.get(
                            Calendar.DAY_OF_MONTH
                    ))
                    dialogtime.datePicker.maxDate = System.currentTimeMillis()
                    dialogtime.show()
                }
                dialogfilterTanggal.btTglAkhir.setOnClickListener {
                    val dialogtime = DatePickerDialog(
                            requireContext(),
                            { view, year, month, dayOfMonth ->
                                val newDate = Calendar.getInstance()
                                var hourOfDay = 0
                                var minute= 0
                                newDate[year, month, dayOfMonth, hourOfDay, minute] = 0
                                val tem = Calendar.getInstance()
                                Log.w("TIMEE", System.currentTimeMillis().toString() + "")
                                if (newDate.timeInMillis - tem.timeInMillis < 0){
                                    val myFormat = "EEE MMM dd HH:mm:ss zzz yyyy"
                                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                                    var datehutang = sdf.parse(newDate.time.toString())
                                    val cal = Calendar.getInstance()
                                    cal.time = datehutang

                                    val sdf1 = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                                    val date2 = sdf1.format(cal.time)
                                    dialogfilterTanggal.btTglAkhir.setText(date2.toString())
                                } else Toast.makeText(context, "Invalid time", Toast.LENGTH_SHORT)
                                        .show()
                            }, newCalender.get(Calendar.YEAR), newCalender.get(Calendar.MONTH), newCalender.get(
                            Calendar.DAY_OF_MONTH
                    ))
                    dialogtime.datePicker.maxDate = System.currentTimeMillis()
                    dialogtime.show()
                }
                dialogfilterTanggal.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener{
                    tglawal=dialogfilterTanggal.btTglAwal.text.toString()
                    tglakhir=dialogfilterTanggal.btTglAkhir.text.toString()
                    db= FirebaseFirestore.getInstance()
                    show.clear()
                    val myFormat = "dd-MM-yyy"
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    var dateAwal = sdf.parse(tglawal)
                    var dateAkhir = sdf.parse(tglakhir)
                    val calAwal = Calendar.getInstance()
                    val calAkhir = Calendar.getInstance()
                    calAwal.time=dateAwal
                    calAkhir.time=dateAkhir
                    db.collection("lapor")
                            .get().addOnSuccessListener {
                                document ->
                                vGlobal.clearlapor()
                                for(doc in document)
                                {
                                    var datal = doc.data as MutableMap<String,String>
                                    val data = laporan(
                                            datal.getValue("Nama Sales"),
                                            datal.getValue("Email Sales"),
                                            datal.getValue("Nama Mesin"),
                                            datal.getValue("Jenis Mesin"),
                                            datal.getValue("Catatan"),
                                            datal.getValue("Tanggal"),
                                            datal.getValue("Foto"),
                                            datal.getValue("Pemilik")
                                    )

                                    var dateisi = sdf.parse(data.tanggal)
                                    val cal = Calendar.getInstance()
                                    cal.time = dateisi
                                    if(cal.timeInMillis >= calAwal.timeInMillis && cal.timeInMillis<=calAkhir.timeInMillis)
                                    {
                                        vGlobal.addlapor(data)
                                    }
                                }
                                show = vGlobal.getlapor()
                                Showdata()
                            }
                    dialogfilterTanggal.dismiss()
                }
                dialogFilter.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener{
                    dialogFilter.dismiss()
                }
            }
        }
    }
    private fun Showdata() {
        rvLihatLapor.layoutManager = LinearLayoutManager(this.context)
        rvLihatLapor.addItemDecoration(
                DividerItemDecoration(
                        activity,
                        DividerItemDecoration.VERTICAL
                )
        )
        adapter = adapterLapor(show)
        rvLihatLapor.adapter = adapter
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                LihatLaporFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}