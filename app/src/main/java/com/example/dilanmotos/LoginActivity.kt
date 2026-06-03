package com.example.dilanmotos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dilanmotos.api.ApiClient
import com.example.dilanmotos.model.LoginRequest
import com.example.dilanmotos.model.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnIngresar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Inicializar los componentes de la interfaz
        etCorreo = findViewById(R.id.etCorreoLogin)
        etContrasena = findViewById(R.id.etContrasenaLogin)
        btnIngresar = findViewById(R.id.btnIngresar)

        // 2. Configurar el evento de clic del botón de ingreso
        btnIngresar.setOnClickListener {
            ejecutarLogin()
        }
    }

    private fun ejecutarLogin() {
        val correo = etCorreo.text.toString().trim()
        val contrasena = etContrasena.text.toString().trim()

        // Validación simple de campos vacíos
        if (correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear el objeto con las credenciales requeridas por el Backend
        val request = LoginRequest(correo, contrasena)

        // Ejecutar la petición HTTP a través de Retrofit
        ApiClient.apiService.login(request).enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                if (response.isSuccessful && response.body() != null) {
                    val usuarioLogueado = response.body()!!

                    Toast.makeText(this@LoginActivity, "Bienvenido ${usuarioLogueado.nombre}", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Cierra el LoginActivity de forma segura
                } else {
                    Toast.makeText(this@LoginActivity, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error de conexión con el servidor", Toast.LENGTH_LONG).show()
            }
        })
    }
}