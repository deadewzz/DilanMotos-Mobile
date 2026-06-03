package com.example.dilanmotos.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.dilanmotos.R
import com.example.dilanmotos.api.ApiClient
import com.example.dilanmotos.model.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FormUsuarioActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText
    private lateinit var etRol: EditText
    private lateinit var btnGuardar: Button
    private lateinit var txtTitulo: TextView
    private var idUsuarioEditar: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_form_usuario)

        etNombre = findViewById(R.id.etNombre)
        etCorreo = findViewById(R.id.etCorreo)
        etContrasena = findViewById(R.id.etContrasena)
        etRol = findViewById(R.id.etRol)
        btnGuardar = findViewById(R.id.btnGuardarUsuario)
        txtTitulo = findViewById(R.id.txtTituloFormulario)

        if (intent.hasExtra("id_usuario")) {
            idUsuarioEditar = intent.getIntExtra("id_usuario", -1)
            etNombre.setText(intent.getStringExtra("nombre"))
            etCorreo.setText(intent.getStringExtra("correo"))
            etRol.setText(intent.getStringExtra("rol"))
            txtTitulo.text = "Editar Usuario"
            btnGuardar.text = "Actualizar Cambios"
            etContrasena.setHint("Nueva contraseña (opcional)")
        }

        btnGuardar.setOnClickListener { guardarDatos() }
    }

    private fun guardarDatos() {
        val nombre = etNombre.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val contrasena = etContrasena.text.toString().trim()
        val rol = etRol.text.toString().trim()

        if (nombre.isEmpty() || correo.isEmpty() || rol.isEmpty()) {
            Toast.makeText(this, "Llena los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val usuario = Usuario(
            idUsuario = idUsuarioEditar,
            nombre = nombre,
            correo = correo,
            contrasena = if (contrasena.isEmpty()) null else contrasena,
            rol = rol
        )

        if (idUsuarioEditar == null) {
            ApiClient.apiService.crearUsuario(usuario).enqueue(object : Callback<Usuario> {
                override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@FormUsuarioActivity, "Creado con éxito", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                override fun onFailure(call: Call<Usuario>, t: Throwable) {
                    Toast.makeText(this@FormUsuarioActivity, "Error al crear", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            ApiClient.apiService.actualizarUsuario(idUsuarioEditar!!, usuario).enqueue(object : Callback<Usuario> {
                override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@FormUsuarioActivity, "Actualizado con éxito", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                override fun onFailure(call: Call<Usuario>, t: Throwable) {
                    Toast.makeText(this@FormUsuarioActivity, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}