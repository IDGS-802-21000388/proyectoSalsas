package com.example.proyecto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto.models.LoginModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.example.proyecto.apiservice.RetrofitClient
import com.example.proyecto.models.Usuario
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
        contrasenaInputLayout = findViewById<TextInputLayout>(R.id.contrasenaInputLayout)
        contrasenaInput = findViewById<TextInputEditText>(R.id.contrasenaEditText)
        emailInputLayout = findViewById<TextInputLayout>(R.id.emailInputLayout)
        emailInput = findViewById<TextInputEditText>(R.id.emailEditText)
        val emailParam = intent.getStringExtra("email")
        if (emailParam != null) {
            emailInput.setText(emailParam.toString())
        }
        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            if (formValido()) {
                val params = LoginModel(emailInput.text.toString(), contrasenaInput.text.toString())
                login(params)
            } else {
                Toast.makeText(this, "Existe información incorrecta", Toast.LENGTH_SHORT).show()
            }
        }
        val registrarButton = findViewById<Button>(R.id.registrarButton)
        registrarButton.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }

    fun login(parametros: LoginModel){
        Toast.makeText(this, "Iniciando sesión", Toast.LENGTH_SHORT)
        RetrofitClient.instance.postLogin(parametros).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    var stringJson = response.body()?.string();
                    val gson = Gson()
                    val usuario = gson.fromJson(stringJson, Usuario::class.java)
                    Toast.makeText(contexto, "Bienvenido ${usuario.nombre}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(contexto, MainActivity::class.java)
                    startActivity(intent)
                } else {
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
        if(contrasenaInput.text.toString().isEmpty()){
            contrasenaInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            contrasenaInputLayout.error = ""
        }
        if(emailInput.text.toString().isEmpty()){
            emailInputLayout.error = "Campo obligatorio"
            valido = false
        } else {
            emailInputLayout.error = ""
        }
        return valido
    }
}