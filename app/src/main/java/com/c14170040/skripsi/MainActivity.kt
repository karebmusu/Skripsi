package com.c14170040.skripsi

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
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

class MainActivity : AppCompatActivity() {

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
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,R.id.nav_maps,R.id.nav_lapor,R.id.nav_blank,R.id.nav_route), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        if(intent.getStringExtra("typeuser").toString()!="admin"){
            val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
            val nav_Menu : Menu = navigationView.menu
            val ina : MenuItem = nav_Menu.findItem(R.id.nav_maps)
            val inb : MenuItem = nav_Menu.findItem(R.id.nav_gallery)
            val inc : MenuItem = nav_Menu.findItem(R.id.nav_home)
            val ind : MenuItem = nav_Menu.findItem(R.id.nav_slideshow)
            val ine : MenuItem = nav_Menu.findItem(R.id.nav_blank)
            Log.d("cektype", intent.getStringExtra("typeuser").toString())
            ina.setVisible(false)
            inb.setVisible(false)
            inc.setVisible(false)
            ind.setVisible(false)
            ine.setVisible(false)
        }
        else if(intent.getStringExtra("typeuser").toString()=="admin"){
            val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
            val nav_Menu : Menu = navigationView.menu
            val ina : MenuItem = nav_Menu.findItem(R.id.nav_route)
            val inb : MenuItem = nav_Menu.findItem(R.id.nav_lapor)
            val inc : MenuItem = nav_Menu.findItem(R.id.nav_blank)
            ina.setVisible(false)
            inb.setVisible(false)
            inc.setVisible(false)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        val unama = findViewById<TextView>(R.id.uname)
        val uemail = findViewById<TextView>(R.id.uemail)
        unama.setText(intent.getStringExtra("namauser"))
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