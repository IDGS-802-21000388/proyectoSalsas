package com.example.proyecto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.apiservice.RetrofitClient
import com.example.proyecto.models.Direccion
import com.example.proyecto.models.RegistroModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistroActivity : AppCompatActivity() {

    lateinit var nombreInputLayout: TextInputLayout
    lateinit var nombreInput: TextInputEditText
    lateinit var nombreUsuarioInputLayout: TextInputLayout
    lateinit var nombreUsuarioInput: TextInputEditText
    lateinit var emailInputLayout: TextInputLayout
    lateinit var emailInput: TextInputEditText
    lateinit var contrasenaInputLayout: TextInputLayout
    lateinit var contrasenaInput: TextInputEditText
    lateinit var telefonoInputLayout: TextInputLayout
    lateinit var telefonoInput: TextInputEditText

    // Nuevos campos de dirección
    lateinit var estadoInputLayout: TextInputLayout
    lateinit var estadoInput: TextInputEditText
    lateinit var municipioInputLayout: TextInputLayout
    lateinit var municipioInput: TextInputEditText
    lateinit var codigoPostalInputLayout: TextInputLayout
    lateinit var codigoPostalInput: TextInputEditText
    lateinit var coloniaInputLayout: TextInputLayout
    lateinit var coloniaInput: TextInputEditText
    lateinit var calleInputLayout: TextInputLayout
    lateinit var calleInput: TextInputEditText
    lateinit var numExtInputLayout: TextInputLayout
    lateinit var numExtInput: TextInputEditText
    lateinit var numIntInputLayout: TextInputLayout
    lateinit var numIntInput: TextInputEditText
    lateinit var referenciaInputLayout: TextInputLayout
    lateinit var referenciaInput: TextInputEditText

    lateinit var contexto: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contexto = this
        setContentView(R.layout.activity_registro)

        // Inicializar campos
        nombreInputLayout = findViewById(R.id.nombreInputLayout)
        nombreInput = findViewById(R.id.nombreEditText)
        nombreUsuarioInputLayout = findViewById(R.id.nombreUsuarioInputLayout)
        nombreUsuarioInput = findViewById(R.id.nombreUsuarioEditText)
        emailInputLayout = findViewById(R.id.emailInputLayout)
        emailInput = findViewById(R.id.emailEditText)
        contrasenaInputLayout = findViewById(R.id.contrasenaInputLayout)
        contrasenaInput = findViewById(R.id.contrasenaEditText)
        telefonoInputLayout = findViewById(R.id.telefonoInputLayout)
        telefonoInput = findViewById(R.id.telefonoEditText)

        estadoInputLayout = findViewById(R.id.estadoInputLayout)
        estadoInput = findViewById(R.id.estadoEditText)
        municipioInputLayout = findViewById(R.id.municipioInputLayout)
        municipioInput = findViewById(R.id.municipioEditText)
        codigoPostalInputLayout = findViewById(R.id.codigoPostalInputLayout)
        codigoPostalInput = findViewById(R.id.codigoPostalEditText)
        coloniaInputLayout = findViewById(R.id.coloniaInputLayout)
        coloniaInput = findViewById(R.id.coloniaEditText)
        calleInputLayout = findViewById(R.id.calleInputLayout)
        calleInput = findViewById(R.id.calleEditText)
        numExtInputLayout = findViewById(R.id.numExtInputLayout)
        numExtInput = findViewById(R.id.numExtEditText)
        numIntInputLayout = findViewById(R.id.numIntInputLayout)
        numIntInput = findViewById(R.id.numIntEditText)
        referenciaInputLayout = findViewById(R.id.referenciaInputLayout)
        referenciaInput = findViewById(R.id.referenciaEditText)

        val registrarButton = findViewById<Button>(R.id.registrarButton)
        registrarButton.setOnClickListener {
            if (formValido()) {
                val direccion = Direccion(
                    estado = estadoInput.text.toString(),
                    municipio = municipioInput.text.toString(),
                    codigoPostal = codigoPostalInput.text.toString(),
                    colonia = coloniaInput.text.toString(),
                    calle = calleInput.text.toString(),
                    numExt = numExtInput.text.toString(),
                    numInt = numIntInput.text.toString(),
                    referencia = referenciaInput.text.toString()
                )
                val params = RegistroModel(
                    nombre = nombreInput.text.toString(),
                    nombreUsuario = nombreUsuarioInput.text.toString(),
                    correo = emailInput.text.toString(),
                    contrasenia = contrasenaInput.text.toString(),
                    telefono = telefonoInput.text.toString(),
                    direccion = direccion
                )
                registrarme(params)
            } else {
                Toast.makeText(this, "Existe información incorrecta", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun registrarme(parametros: RegistroModel) {
        RetrofitClient.instance.postRegistrar(parametros).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegistroActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegistroActivity, LoginActivity::class.java)
                    intent.putExtra("email", emailInput.text.toString())
                    startActivity(intent)
                } else {
                    Toast.makeText(contexto, "${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("RegistroActivity", "Failure: ${t.message}")
            }
        })
    }

    fun formValido(): Boolean {
        var valido = true
        if (nombreInput.text.toString().isEmpty()) {
            nombreInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            nombreInputLayout.error = ""
        }
        if (nombreUsuarioInput.text.toString().isEmpty()) {
            nombreUsuarioInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            nombreUsuarioInputLayout.error = ""
        }
        if (emailInput.text.toString().isEmpty()) {
            emailInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            emailInputLayout.error = ""
        }
        if (contrasenaInput.text.toString().isEmpty()) {
            contrasenaInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            contrasenaInputLayout.error = ""
        }
        if (telefonoInput.text.toString().isEmpty()) {
            telefonoInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            telefonoInputLayout.error = ""
        }
        if (estadoInput.text.toString().isEmpty()) {
            estadoInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            estadoInputLayout.error = ""
        }
        if (municipioInput.text.toString().isEmpty()) {
            municipioInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            municipioInputLayout.error = ""
        }
        if (codigoPostalInput.text.toString().isEmpty()) {
            codigoPostalInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            codigoPostalInputLayout.error = ""
        }
        if (coloniaInput.text.toString().isEmpty()) {
            coloniaInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            coloniaInputLayout.error = ""
        }
        if (calleInput.text.toString().isEmpty()) {
            calleInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            calleInputLayout.error = ""
        }
        if (numExtInput.text.toString().isEmpty()) {
            numExtInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            numExtInputLayout.error = ""
        }
        return valido
    }
}
