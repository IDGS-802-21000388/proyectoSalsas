package com.example.proyecto

import ProduccionFragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sharedPref = getSharedPreferences("miAppPref", Context.MODE_PRIVATE)
        val rol = sharedPref.getString("rol", "")
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)

        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val sharedPref = getSharedPreferences("miAppPref", Context.MODE_PRIVATE)
        val rol = sharedPref.getString("rol", "")

        when (item.itemId) {
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
            }
            R.id.nav_assigment -> {
                if (rol == "admin" || rol == "empleado") {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AssigmentFragment()).commit()
                } else {
                    Toast.makeText(this, "No tienes acceso a esta sección", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.nav_shipping -> {
                if (rol == "admin" || rol == "repartidor") {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ShippingFragment()).commit()
                } else {
                    Toast.makeText(this, "No tienes acceso a esta sección", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.nav_production -> {
                if (rol == "admin" || rol == "empleado") {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ProduccionFragment()).commit()
                } else {
                    Toast.makeText(this, "No tienes acceso a esta sección", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.nav_logout -> {
                logout()
            }
            else -> {
                // Otras acciones
            }
        }
        // Cierra el drawer después de la selección
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun logout() {
        // Eliminar los datos del usuario almacenados localmente
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        // Redirigir a la pantalla de login
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
