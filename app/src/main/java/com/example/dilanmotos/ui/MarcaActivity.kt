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
import com.example.dilanmotos.model.Marca
import com.example.dilanmotos.api.ApiClient
import com.example.dilanmotos.session.SessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MarcaActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MarcaAdapter
    private lateinit var btnNuevaMarca: FloatingActionButton
    private lateinit var sessionManager: SessionManager
    private var listaMarcas: List<Marca> = ArrayList()
    private var esAdmin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_marca)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sessionManager = SessionManager(this)
        esAdmin = sessionManager.isAdmin()

        recyclerView = findViewById(R.id.recyclerViewMarcas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        btnNuevaMarca = findViewById(R.id.btnNuevaMarca)

        btnNuevaMarca.visibility = if (esAdmin) View.VISIBLE else View.GONE

        btnNuevaMarca.setOnClickListener {
            startActivity(Intent(this, FormMarcaActivity::class.java))
        }

        adapter = MarcaAdapter(
            listaMarcas,
            esAdmin = esAdmin,
            onEditClick = { marcaSeleccionada ->
                val intent = Intent(this, FormMarcaActivity::class.java).apply {
                    putExtra("id_marca", marcaSeleccionada.idMarca)
                    putExtra("nombre", marcaSeleccionada.nombre)
                }
                startActivity(intent)
            },
            onDeleteClick = { marcaSeleccionada ->
                marcaSeleccionada.idMarca?.let { id ->
                    eliminarMarca(id)
                } ?: Toast.makeText(this, "No se puede eliminar una marca sin ID", Toast.LENGTH_SHORT).show()
            }
        )
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        cargarMarca()
    }

    private fun cargarMarca() {
        ApiClient.apiService.obtenerMarca().enqueue(object : Callback<List<Marca>> {
            override fun onResponse(call: Call<List<Marca>>, response: Response<List<Marca>>) {
                if (response.isSuccessful && response.body() != null) {
                    listaMarcas = response.body()!!
                    adapter.actualizarLista(listaMarcas)
                } else {
                    Toast.makeText(this@MarcaActivity, "Error de servidor: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Marca>>, t: Throwable) {
                Toast.makeText(this@MarcaActivity, "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun eliminarMarca(idMarca: Int) {
        ApiClient.apiService.eliminarMarca(idMarca).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MarcaActivity, "Marca eliminada correctamente", Toast.LENGTH_SHORT).show()
                    cargarMarca()
                } else {
                    Toast.makeText(this@MarcaActivity, "No se pudo eliminar: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@MarcaActivity, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
            }
        })
    }
}