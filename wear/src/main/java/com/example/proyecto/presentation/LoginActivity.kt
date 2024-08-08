package com.example.proyecto.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.PedidoDetalles
import com.example.proyecto.R
import com.example.proyecto.apiservice.RetrofitClient
import com.example.proyecto.models.LoginResponse
import com.example.proyecto.models.Usuario
import com.example.proyecto.presentation.models.LoginModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    lateinit var contexto: Context
    lateinit var emailInputLayout: TextInputLayout
    lateinit var emailInput: TextInputEditText
    lateinit var contrasenaInputLayout: TextInputLayout
    lateinit var contrasenaInput: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        contexto = this
        emailInputLayout = findViewById(R.id.emailInputLayout)
        emailInput = findViewById(R.id.emailEditText)
        contrasenaInputLayout = findViewById(R.id.contrasenaInputLayout)
        contrasenaInput = findViewById(R.id.contrasenaEditText)

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            if (formValido()) {
                val params = LoginModel(emailInput.text.toString(), contrasenaInput.text.toString())
                login(params)
            } else {
                Toast.makeText(this, "Existe información incorrecta", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun login(parametros: LoginModel) {
        Toast.makeText(this, "Iniciando sesión", Toast.LENGTH_SHORT).show()
        RetrofitClient.instance.postLogin(parametros).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val stringJson = response.body()?.string()
                    val gson = Gson()
                    val loginResponse = gson.fromJson(stringJson, LoginResponse::class.java)
                    val usuario = loginResponse.user

                    Toast.makeText(contexto, "Bienvenido ${usuario.nombreUsuario}", Toast.LENGTH_SHORT).show()

                    when (usuario.rol.toLowerCase()) {
                        "admin" -> navigateToAdminActivity()
                        "repartidor" -> navigateToRepartidorActivity()
                        "produccion" -> navigateToProduccionActivity()
                        "cliente" -> navigateToAssigmentFragment()
                        else -> Toast.makeText(contexto, "Rol desconocido", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(contexto, "${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("LoginActivity", "Failure: ${t.message}")
            }
        })
    }

    private fun navigateToAdminActivity() {
        val intent = Intent(this, PedidoDetalles::class.java)
        startActivity(intent)
    }

    private fun navigateToRepartidorActivity() {
        val intent = Intent(this, ShippingActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToProduccionActivity() {
        val intent = Intent(this, ProductionActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToAssigmentFragment(){
        val intent = Intent(this, AssigmentFragment::class.java)
        startActivity(intent)
    }

    fun formValido(): Boolean {
        var valido = true
        if (emailInput.text.toString().isEmpty()) {
            emailInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            emailInputLayout.error = null
        }
        if (contrasenaInput.text.toString().isEmpty()) {
            contrasenaInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            contrasenaInputLayout.error = null
        }
        return valido
    }
}
