package com.example.dilanmotos.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dilanmotos.LoginActivity
import com.example.dilanmotos.R
import com.example.dilanmotos.api.ApiClient
import com.example.dilanmotos.model.ForgotPasswordRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var etCorreo: EditText
    private lateinit var btnEnviar: Button
    private lateinit var tvVolver: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        etCorreo  = findViewById(R.id.etCorreoRecuperacion)
        btnEnviar = findViewById(R.id.btnEnviarCodigo)
        tvVolver  = findViewById(R.id.tvVolverLogin)

        tvVolver.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnEnviar.setOnClickListener {
            val correo = etCorreo.text.toString().trim()

            if (correo.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                etCorreo.error = "Ingresa un correo válido"
                etCorreo.requestFocus()
                return@setOnClickListener
            }

            btnEnviar.isEnabled = false
            btnEnviar.text = "Enviando..."

            ApiClient.apiService.recuperarContrasena(ForgotPasswordRequest(correo))
                .enqueue(object : Callback<Map<String, String>> {
                    override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                        btnEnviar.isEnabled = true
                        btnEnviar.text = "Enviar Código"

                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "Código enviado a $correo",
                                Toast.LENGTH_LONG
                            ).show()
                            val intent = Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // Leer el mensaje de error que devuelve el backend
                            val errorBody = response.errorBody()?.string()
                            val msg = when {
                                errorBody?.contains("no registrado", ignoreCase = true) == true ->
                                    "Este correo no está registrado en el sistema"
                                errorBody?.contains("Correo", ignoreCase = true) == true ->
                                    "Este correo no está registrado en el sistema"
                                response.code() == 400 ->
                                    "Este correo no está registrado en el sistema"
                                else ->
                                    "Error al enviar el código (${response.code()})"
                            }
                            Toast.makeText(this@ForgotPasswordActivity, msg, Toast.LENGTH_LONG).show()
                            // NO navega a la siguiente pantalla, se queda aquí
                        }
                    }

                    override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                        btnEnviar.isEnabled = true
                        btnEnviar.text = "Enviar Código"
                        Toast.makeText(this@ForgotPasswordActivity, "Sin conexión: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }
}