package com.example.dilanmotos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dilanmotos.api.ApiClient
import com.example.dilanmotos.model.LoginRequest
import com.example.dilanmotos.model.LoginResponse
import com.example.dilanmotos.session.SessionManager
import com.example.dilanmotos.ui.ForgotPasswordActivity
import com.example.dilanmotos.ui.HomeActivity
import com.example.dilanmotos.ui.RegisterActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnIngresar: Button
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // 1. Registrar el contexto en el ApiClient para solucionar el desfase del token
        ApiClient.registrarContexto(this)

        sessionManager = SessionManager(this)

        if (sessionManager.isSesionActiva()) {
            irAlHome()
            return
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etCorreo     = findViewById(R.id.etCorreoLogin)
        etContrasena = findViewById(R.id.etContrasenaLogin)
        btnIngresar  = findViewById(R.id.btnIngresar)

        btnIngresar.setOnClickListener { ejecutarLogin() }

        findViewById<TextView>(R.id.tvRegister).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        findViewById<TextView>(R.id.tvForgotPassword).setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun ejecutarLogin() {
        val correo     = etCorreo.text.toString().trim()
        val contrasena = etContrasena.text.toString().trim()

        if (correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        btnIngresar.isEnabled = false
        btnIngresar.text = "Ingresando..."

        ApiClient.apiService.login(LoginRequest(correo, contrasena))
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    btnIngresar.isEnabled = true
                    btnIngresar.text = "Ingresar"

                    if (response.isSuccessful && response.body() != null) {
                        val loginData = response.body()!!

                        // El SessionManager guarda bajo "token_sesion" en el archivo "DilanMotosPrefs"
                        sessionManager.guardarSesion(
                            idUsuario = loginData.idUsuario ?: -1,
                            nombre    = loginData.nombre ?: "",
                            rol       = loginData.rol ?: "",
                            token     = loginData.token ?: ""
                        )

                        Toast.makeText(this@LoginActivity, "Bienvenido ${loginData.nombre}", Toast.LENGTH_SHORT).show()
                        irAlHome()
                    } else {
                        Toast.makeText(this@LoginActivity, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    btnIngresar.isEnabled = true
                    btnIngresar.text = "Ingresar"
                    Toast.makeText(this@LoginActivity, "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun irAlHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}