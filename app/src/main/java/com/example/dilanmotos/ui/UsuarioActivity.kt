package com.example.dilanmotos.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dilanmotos.R
import com.example.dilanmotos.model.Usuario
import com.example.dilanmotos.api.ApiClient
import com.example.dilanmotos.session.SessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsuarioActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UsuarioAdapter
    private lateinit var btnNuevoUsuario: FloatingActionButton
    private lateinit var sessionManager: SessionManager
    private var listaUsuarios: List<Usuario> = ArrayList()
    private var esAdmin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_usuarios)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sessionManager = SessionManager(this)
        esAdmin = sessionManager.isAdmin()

        recyclerView = findViewById(R.id.recyclerViewUsuarios)
        recyclerView.layoutManager = LinearLayoutManager(this)
        btnNuevoUsuario = findViewById(R.id.btnNuevoUsuario)

        // Usuarios solo accesible desde AdminActivity, pero por seguridad igual se oculta el FAB
        btnNuevoUsuario.visibility = if (esAdmin) View.VISIBLE else View.GONE

        btnNuevoUsuario.setOnClickListener {
            startActivity(Intent(this, FormUsuarioActivity::class.java))
        }

        adapter = UsuarioAdapter(
            listaUsuarios,
            esAdmin = esAdmin,
            onEditClick = { usuarioSeleccionado ->
                val intent = Intent(this, FormUsuarioActivity::class.java).apply {
                    putExtra("id_usuario", usuarioSeleccionado.idUsuario)
                    putExtra("nombre", usuarioSeleccionado.nombre)
                    putExtra("correo", usuarioSeleccionado.correo)
                    putExtra("rol", usuarioSeleccionado.rol)
                }
                startActivity(intent)
            },
            onDeleteClick = { usuarioSeleccionado ->
                usuarioSeleccionado.idUsuario?.let { id ->
                    eliminarUsuario(id)
                } ?: Toast.makeText(this, "No se puede eliminar un usuario sin ID", Toast.LENGTH_SHORT).show()
            }
        )
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        cargarUsuarios()
    }

    private fun cargarUsuarios() {
        ApiClient.apiService.obtenerUsuarios().enqueue(object : Callback<List<Usuario>> {
            override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                if (response.isSuccessful && response.body() != null) {
                    listaUsuarios = response.body()!!
                    adapter.actualizarLista(listaUsuarios)
                } else {
                    Toast.makeText(this@UsuarioActivity, "Error al obtener usuarios", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                Toast.makeText(this@UsuarioActivity, "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun eliminarUsuario(idUsuario: Int) {
        ApiClient.apiService.eliminarUsuario(idUsuario).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@UsuarioActivity, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show()
                    cargarUsuarios()
                } else {
                    Toast.makeText(this@UsuarioActivity, "No se pudo eliminar el usuario", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@UsuarioActivity, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
            }
        })
    }
}