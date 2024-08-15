package com.example.proyecto.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.PedidoDetalles
import com.example.proyecto.R


class NavBarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_bar)
    }

    fun openPedidoDetallesActivity(view: android.view.View) {
        // Recuperar el rol del usuario desde SharedPreferences
        val sharedPref = getSharedPreferences("miAppPref", Context.MODE_PRIVATE)
        val rol = sharedPref.getString("rol", "")
        if (rol == "admin" || rol == "empleado") {
            val intent = Intent(this, PedidoDetalles::class.java)
            startActivity(intent)
        }else {
            Toast.makeText(this, "No tienes acceso a esta sección", Toast.LENGTH_SHORT).show()
        }
    }

    fun openProductionActivity(view: android.view.View) {
        // Recuperar el rol del usuario desde SharedPreferences
        val sharedPref = getSharedPreferences("miAppPref", Context.MODE_PRIVATE)
        val rol = sharedPref.getString("rol", "")

        if (rol == "admin" || rol == "empleado") {
            val intent = Intent(this, ProductionWearActivity::class.java)
            startActivity(intent)
        }else {
            Toast.makeText(this, "No tienes acceso a esta sección", Toast.LENGTH_SHORT).show()
        }

    }

    fun openShippingActivity(view: android.view.View) {
        // Recuperar el rol del usuario desde SharedPreferences
        val sharedPref = getSharedPreferences("miAppPref", Context.MODE_PRIVATE)
        val rol = sharedPref.getString("rol", "")

        if (rol == "admin" || rol == "repartidor") {
            val intent = Intent(this, ShippingActivity::class.java)
            startActivity(intent)
        }else {
            Toast.makeText(this, "No tienes acceso a esta sección", Toast.LENGTH_SHORT).show()
        }
    }

    fun openAssignacionPersonal(view: android.view.View) {
        val sharedPref = getSharedPreferences("miAppPref", Context.MODE_PRIVATE)
        val rol = sharedPref.getString("rol", "")

        if (rol == "admin" || rol == "repartidor") {
            val intent = Intent(this, AssignacionPersonal::class.java)
            startActivity(intent)
        }else {
            Toast.makeText(this, "No tienes acceso a esta sección", Toast.LENGTH_SHORT).show()
        }
    }
}