package com.c14170040.skripsi

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.c14170040.skripsi.ui.area
import com.google.android.gms.location.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.LatLng

var emailuser = ""
var namauser = ""
var typeuser = ""
var latuser : Double = 0.0
var lnguser : Double = 0.0
var listuser = arrayListOf<user>()
var listStringuser = arrayListOf<String>()
var listarea = arrayListOf<String>()
const val API_KeY = "AIzaSyDOi70He9zLBusrKm1j6RIhu50_cZ5yr00"
class MainActivity : AppCompatActivity() {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    private var PERMISSION_ID = 1010
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,R.id.nav_maps,R.id.nav_lihat_lapor,R.id.nav_lapor,R.id.nav_blank,R.id.nav_route,R.id.nav_add_mesin), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        var db = FirebaseFirestore.getInstance()
        db.collection("user")
            .get()
            .addOnSuccessListener {
                    documents->
                listuser.clear()
                for(doc in documents)
                {
                    var datau = doc.data as MutableMap<String,String>
                    val data = user(
                        datau.getValue("nama"),
                        datau.getValue("email"),
                        datau.getValue("nohp")
                    )
                    if(datau.getValue("type")!="admin")
                    {
                        if(datau.getValue("supervisor")== namauser)
                        {
                            listuser.add(data)
                            listStringuser.add(datau.getValue("nama"))
                        }
                    }
                }
            }
        db.collection("Provinsi")
                .get()
                .addOnSuccessListener { documents ->
                    listarea.clear()
                    for (doc in documents) {
                        var dataa = doc.data as MutableMap<String,String>
                        if(dataa.getValue("Supervisor")== namauser)
                        {
                            listarea.add(dataa.getValue("Nama"))
                        }
                        else
                        {
                            listarea.add(dataa.getValue("Nama"))
                        }
                    }
                }
        typeuser=intent.getStringExtra("typeuser").toString()
        Log.d("typeuser", typeuser)
        if(intent.getStringExtra("typeuser").toString()!="admin" &&intent.getStringExtra("typeuser").toString()!="superadmin"  ){
            val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
            val nav_Menu : Menu = navigationView.menu
            val ina : MenuItem = nav_Menu.findItem(R.id.nav_maps)
            val inb : MenuItem = nav_Menu.findItem(R.id.nav_gallery)
            val inc : MenuItem = nav_Menu.findItem(R.id.nav_home)
            val ind : MenuItem = nav_Menu.findItem(R.id.nav_slideshow)
            val ine : MenuItem = nav_Menu.findItem(R.id.nav_blank)
            val inf : MenuItem = nav_Menu.findItem(R.id.nav_lihat_lapor)
            val ing : MenuItem = nav_Menu.findItem(R.id.nav_lapor)
            val inh : MenuItem = nav_Menu.findItem(R.id.nav_add_mesin)
            Log.d("cektype", intent.getStringExtra("typeuser").toString())
            ina.setVisible(false)
            inb.setVisible(false)
            inc.setVisible(false)
            ind.setVisible(false)
            ine.setVisible(false)
            inf.setVisible(false)
            inf.setVisible(false)
            ing.setVisible(false)
            inh.setVisible(false)

        }
        else if(intent.getStringExtra("typeuser").toString()=="admin"){
            val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
            val nav_Menu : Menu = navigationView.menu
            val ina : MenuItem = nav_Menu.findItem(R.id.nav_route)
            val inb : MenuItem = nav_Menu.findItem(R.id.nav_lapor)
            val inc : MenuItem = nav_Menu.findItem(R.id.nav_blank)
            val ind : MenuItem = nav_Menu.findItem(R.id.nav_add_mesin)
            ina.setVisible(false)
            inb.setVisible(false)
            inc.setVisible(false)
            ind.setVisible(false)
        }
        else if(intent.getStringExtra("typeuser").toString() == "superadmin")
        {
            val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
            val nav_Menu : Menu = navigationView.menu
            val ina : MenuItem = nav_Menu.findItem(R.id.nav_route)
            val inb : MenuItem = nav_Menu.findItem(R.id.nav_lapor)
            val inc : MenuItem = nav_Menu.findItem(R.id.nav_blank)
            val ind : MenuItem = nav_Menu.findItem(R.id.nav_maps)
            val ine : MenuItem = nav_Menu.findItem(R.id.nav_lihat_lapor)
            val inf : MenuItem = nav_Menu.findItem(R.id.nav_gallery)
            inf.setTitle("Assign Supervisor")
            ina.setVisible(false)
            inb.setVisible(false)
            inc.setVisible(false)
            ind.setVisible(false)
            ine.setVisible(false)

        }
    }
    fun getLastLocation( ){

        if (CheckPermission()){
            if (isLocationEnabled()){
                if (ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener{ task ->
                    var location: Location? = task.result
                    if (location==null){
                        NewLocationData()
                    }else {
                        lnguser=location.longitude
                        latuser=location.latitude
                        Log.d("locuser" , latuser.toString()+","+ lnguser.toString())
                    }
                }
            }else {
                Toast.makeText(this,"Please Enable Your Location service", Toast.LENGTH_SHORT).show()
            }
        }else{
            RequestPermission()
        }
    }
    
    fun NewLocationData(){
        var locationRequest =  LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest,locationCallback, Looper.myLooper())
    }
    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location = locationResult.lastLocation
            latuser=lastLocation.latitude
            lnguser= lastLocation.longitude
            Log.d("locuser" , latuser.toString()+","+ lnguser.toString())
        }
    }
    private fun CheckPermission(): Boolean {
        if (
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }

        return false
    }


    private fun RequestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ), PERMISSION_ID
        )
    }


    private fun isLocationEnabled(): Boolean {

        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Debug:", "You have the Permission")
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        val unama = findViewById<TextView>(R.id.uname)
        val uemail = findViewById<TextView>(R.id.uemail)
        unama.setText(intent.getStringExtra("namauser"))
        emailuser+=intent.getStringExtra("emailuser").toString()
        namauser+=intent.getStringExtra("namauser").toString()
        uemail.setText(intent.getStringExtra("emailuser"))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings){
            val editor : SharedPreferences.Editor = sP.edit()
            editor.clear()
            editor.commit()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}