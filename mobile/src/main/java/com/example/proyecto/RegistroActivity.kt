package com.example.proyecto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.apiservice.RetrofitClient
import com.example.proyecto.models.RegistroModel
import com.google.android.material.checkbox.MaterialCheckBox
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
    
    lateinit var contexto: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contexto = this
        setContentView(R.layout.activity_registro)
        nombreInputLayout = findViewById<TextInputLayout>(R.id.nombreInputLayout)
        nombreInput = findViewById<TextInputEditText>(R.id.nombreEditText)

        nombreUsuarioInput = findViewById<TextInputEditText>(R.id.nombreUsuarioEditText)
        nombreUsuarioInputLayout = findViewById<TextInputLayout>(R.id.nombreUsuarioInputLayout)

        emailInputLayout = findViewById<TextInputLayout>(R.id.emailInputLayout)
        emailInput = findViewById<TextInputEditText>(R.id.emailEditText)

        contrasenaInputLayout = findViewById<TextInputLayout>(R.id.contrasenaInputLayout)
        contrasenaInput = findViewById<TextInputEditText>(R.id.contrasenaEditText)

        telefonoInput = findViewById<TextInputEditText>(R.id.telefonoEditText)
        telefonoInputLayout = findViewById<TextInputLayout>(R.id.telefonoInputLayout)

        val registrarButton = findViewById<Button>(R.id.registrarButton)
        registrarButton.setOnClickListener{
            if(formValido()){
// registrar()
                val params = RegistroModel(nombreInput.text.toString(), nombreUsuarioInput.text.toString(),emailInput.text.toString(), contrasenaInput.text.toString(),telefonoInput.text.toString() )
                registrarme(params)
            } else {
                Toast.makeText(this, "Existe información incorrecta", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun registrarme(parametros: RegistroModel){
        RetrofitClient.instance.postRegistrar(parametros).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
// Handle successful response
                    Toast.makeText(this@RegistroActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegistroActivity, LoginActivity::class.java)
                    intent.putExtra("email", emailInput.text.toString())
                    startActivity(intent)
                } else {
// Handle unsuccessful response
                    Toast.makeText(contexto, "${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show();
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
// Handle failure
                Log.e("MainActivity", "Failure: ${t.message}")
            }
        })
    }
    fun formValido() : Boolean {
        var valido = true
        if(nombreInput.text.toString().isEmpty()){
            nombreInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            nombreInputLayout.error = ""
        }
        if(nombreUsuarioInput.text.toString().isEmpty()){
            nombreUsuarioInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            nombreUsuarioInputLayout.error = ""
        }
        if(emailInput.text.toString().isEmpty()){
            emailInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            emailInputLayout.error = ""
        }
        if(contrasenaInput.text.toString().isEmpty()){
            contrasenaInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            contrasenaInputLayout.error = ""
        }
        if(telefonoInput.text.toString().isEmpty()){
            telefonoInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            telefonoInputLayout.error = ""
        }
        return valido
    }
}
