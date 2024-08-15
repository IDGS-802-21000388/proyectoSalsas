package com.example.proyecto.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.R

class ProductionWearActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_production_wear)

        // Cargar el fragmento que muestra la lista de pedidos
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProductionWearFragment())
                .commitNow()
        }
    }
}