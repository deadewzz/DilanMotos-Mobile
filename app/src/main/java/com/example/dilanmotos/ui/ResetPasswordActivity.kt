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
import com.example.dilanmotos.model.ResetPasswordRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var etCodigo: EditText
    private lateinit var etNuevaContrasena: EditText
    private lateinit var etConfirmarContrasena: EditText
    private lateinit var btnCambiar: Button
    private lateinit var tvVolver: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        etCodigo             = findViewById(R.id.etCodigo)
        etNuevaContrasena    = findViewById(R.id.etNuevaContrasena)
        etConfirmarContrasena= findViewById(R.id.etConfirmarContrasena)
        btnCambiar           = findViewById(R.id.btnCambiarContrasena)
        tvVolver             = findViewById(R.id.tvVolverLoginReset)

        tvVolver.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnCambiar.setOnClickListener {
            val codigo    = etCodigo.text.toString().trim().uppercase()
            val nueva     = etNuevaContrasena.text.toString().trim()
            val confirmar = etConfirmarContrasena.text.toString().trim()

            if (codigo.length != 6) {
                etCodigo.error = "El código debe tener 6 caracteres"
                etCodigo.requestFocus()
                return@setOnClickListener
            }
            if (nueva.length < 6) {
                etNuevaContrasena.error = "Mínimo 6 caracteres"
                etNuevaContrasena.requestFocus()
                return@setOnClickListener
            }
            if (nueva != confirmar) {
                etConfirmarContrasena.error = "Las contraseñas no coinciden"
                etConfirmarContrasena.requestFocus()
                return@setOnClickListener
            }

            btnCambiar.isEnabled = false
            btnCambiar.text = "Cambiando..."

            ApiClient.apiService.resetearContrasena(ResetPasswordRequest(codigo, nueva))
                .enqueue(object : Callback<Map<String, String>> {
                    override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                        btnCambiar.isEnabled = true
                        btnCambiar.text = "Cambiar Contraseña"

                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@ResetPasswordActivity,
                                "¡Contraseña cambiada! Ya puedes iniciar sesión.",
                                Toast.LENGTH_LONG
                            ).show()
                            startActivity(Intent(this@ResetPasswordActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            val msg = when (response.code()) {
                                400 -> "Código inválido o expirado"
                                else -> "Error al cambiar la contraseña (${response.code()})"
                            }
                            Toast.makeText(this@ResetPasswordActivity, msg, Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                        btnCambiar.isEnabled = true
                        btnCambiar.text = "Cambiar Contraseña"
                        Toast.makeText(this@ResetPasswordActivity, "Sin conexión: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }
}