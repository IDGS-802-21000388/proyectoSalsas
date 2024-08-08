package com.example.proyecto.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.PedidoDetalles
import com.example.proyecto.R


class NavBarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_bar)
    }

    fun openPedidoDetallesActivity(view: android.view.View) {
        val intent = Intent(this, PedidoDetalles::class.java)
        startActivity(intent)
    }

    fun openProductionActivity(view: android.view.View) {
        val intent = Intent(this, ProductionActivity::class.java)
        startActivity(intent)
    }

    fun openShippingActivity(view: android.view.View) {
        val intent = Intent(this, ShippingActivity::class.java)
        startActivity(intent)
    }
}