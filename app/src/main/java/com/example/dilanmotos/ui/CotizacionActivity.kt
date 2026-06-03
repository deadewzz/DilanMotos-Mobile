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
import com.example.dilanmotos.model.Cotizacion
import com.example.dilanmotos.api.ApiClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CotizacionActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CotizacionAdapter
    private lateinit var btnNuevaCotizacion: FloatingActionButton
    private var listaCotizaciones: List<Cotizacion> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cotizacion)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Inicializar componentes usando los IDs del XML de cotizaciones
        recyclerView = findViewById(R.id.recyclerViewCotizaciones)
        recyclerView.layoutManager = LinearLayoutManager(this)
        btnNuevaCotizacion = findViewById(R.id.btnNuevaCotizacion)

        // 2. Evento para abrir el formulario (Crear)
        btnNuevaCotizacion.setOnClickListener {
            val intent = Intent(this, FormCotizacionActivity::class.java)
            startActivity(intent)
        }

        // 3. Configurar adaptador con acciones (Editar y Eliminar)
        adapter = CotizacionAdapter(
            listaCotizaciones,
            onEditClick = { cotizacionSeleccionada ->
                // Mandar datos actuales al formulario en modo edición
                val intent = Intent(this, FormCotizacionActivity::class.java).apply {
                    putExtra("id_cotizacion", cotizacionSeleccionada.idCotizacion)
                    putExtra("id_usuario", cotizacionSeleccionada.idUsuario)
                    putExtra("producto", cotizacionSeleccionada.producto)
                    putExtra("cantidad", cotizacionSeleccionada.cantidad)
                    putExtra("precio_unitario", cotizacionSeleccionada.precioUnitario)
                    putExtra("fecha", cotizacionSeleccionada.fecha)
                    putExtra("producto_agregado", cotizacionSeleccionada.productoAgregado)
                }
                startActivity(intent)
            },
            onDeleteClick = { cotizacionSeleccionada ->
                cotizacionSeleccionada.idCotizacion?.let { id ->
                    eliminarCotizacion(id)
                } ?: Toast.makeText(this, "No se puede eliminar una cotización sin ID", Toast.LENGTH_SHORT).show()
            }
        )
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        cargarCotizaciones() // Refresca automáticamente la lista al volver del formulario
    }

    // --- Obtener cotizaciones desde la API ---
    private fun cargarCotizaciones() {
        ApiClient.apiService.obtenerCotizacion().enqueue(object : Callback<List<Cotizacion>> {
            override fun onResponse(call: Call<List<Cotizacion>>, response: Response<List<Cotizacion>>) {
                if (response.isSuccessful && response.body() != null) {
                    listaCotizaciones = response.body()!!
                    adapter.actualizarLista(listaCotizaciones)
                } else {
                    Toast.makeText(this@CotizacionActivity, "Error de servidor: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Cotizacion>>, t: Throwable) {
                Toast.makeText(this@CotizacionActivity, "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    // --- Eliminar cotización desde la API ---
    private fun eliminarCotizacion(idCotizacion: Int) {
        ApiClient.apiService.eliminarCotizacion(idCotizacion).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CotizacionActivity, "Cotización eliminada correctamente", Toast.LENGTH_SHORT).show()
                    cargarCotizaciones() // Recarga la lista para aplicar los cambios
                } else {
                    Toast.makeText(this@CotizacionActivity, "No se pudo eliminar: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@CotizacionActivity, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
            }
        })
    }
}