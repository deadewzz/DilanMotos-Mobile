package com.example.dilanmotos.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dilanmotos.R
import com.example.dilanmotos.model.Moto
import com.example.dilanmotos.api.ApiClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MotoActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MotoAdapter
    private lateinit var btnNuevaMoto: FloatingActionButton
    private var listaMotos: List<Moto> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_moto)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Inicializar componentes usando los IDs del XML de Motos
        recyclerView = findViewById(R.id.recyclerViewMotos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        btnNuevaMoto = findViewById(R.id.btnNuevaMoto)

        // 2. Evento para abrir el formulario (Crear)
        btnNuevaMoto.setOnClickListener {
            val intent = Intent(this, FormMotoActivity::class.java)
            startActivity(intent)
        }

        // 3. Configurar adaptador con acciones (Editar y Eliminar)
        adapter = MotoAdapter(
            listaMotos,
            onEditClick = { motoSeleccionada ->
                // Mandar datos actuales del objeto al formulario en modo edición
                val intent = Intent(this, FormMotoActivity::class.java).apply {
                    putExtra("ID_MOTO", motoSeleccionada.idMoto) // Usando idMoto real
                    putExtra("MODELO_MOTO", motoSeleccionada.modelo)
                    putExtra("CILINDRAJE_MOTO", motoSeleccionada.cilindraje ?: 0.0) // ✅
                    // Controlamos la nulidad de idMarca de forma segura usando ?: -1
                    putExtra("ID_MARCA_MOTO", motoSeleccionada.marca?.idMarca ?: -1)
                }
                startActivity(intent)
            },
            onDeleteClick = { motoSeleccionada ->

                val id = motoSeleccionada.idMoto
                if (id != null && id > 0) {
                    eliminarMoto(id)
                } else {
                    Toast.makeText(this, "No se puede eliminar una moto sin ID", Toast.LENGTH_SHORT).show()
                }
            }
        )
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        cargarMotos() // Refresca automáticamente la lista al volver del formulario
    }

    // --- Obtener motos desde la API ---
    private fun cargarMotos() {
        ApiClient.apiService.obtenerMotos().enqueue(object : Callback<List<Moto>> {
            override fun onResponse(call: Call<List<Moto>>, response: Response<List<Moto>>) {
                if (response.isSuccessful && response.body() != null) {
                    listaMotos = response.body()!!

                    // Verificación de seguridad por ciclo de vida
                    if (::adapter.isInitialized) {
                        adapter.actualizarLista(listaMotos)
                    }
                } else {
                    Toast.makeText(this@MotoActivity, "Error de servidor: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Moto>>, t: Throwable) {
                Toast.makeText(this@MotoActivity, "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    // --- Eliminar motos desde la API ---
    private fun eliminarMoto(idMoto: Int) {
        ApiClient.apiService.eliminarMoto(idMoto).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MotoActivity, "Moto eliminada correctamente", Toast.LENGTH_SHORT).show()
                    cargarMotos() // Recarga la lista para aplicar el borrado en tiempo real
                } else {
                    Toast.makeText(this@MotoActivity, "No se pudo eliminar: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@MotoActivity, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
            }
        })
    }
}