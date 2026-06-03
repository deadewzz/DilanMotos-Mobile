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
import com.example.dilanmotos.model.Producto
import com.example.dilanmotos.api.ApiClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductoActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductoAdapter
    private lateinit var btnNuevoProducto: FloatingActionButton
    private var listaProductos: List<Producto> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_producto)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Inicializar componentes usando los IDs del XML de productos
        recyclerView = findViewById(R.id.recyclerViewProductos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        btnNuevoProducto = findViewById(R.id.btnNuevoProducto)

        // 2. Evento para abrir el formulario (Crear)
        btnNuevoProducto.setOnClickListener {
            val intent = Intent(this, FormProductoActivity::class.java)
            startActivity(intent)
        }

        // 3. Configurar adaptador con acciones (Editar y Eliminar)
        adapter = ProductoAdapter(
            listaProductos,
            onEditClick = { productoSeleccionado ->
                // Mandar datos actuales de la tabla al formulario en modo edición
                val intent = Intent(this, FormProductoActivity::class.java).apply {
                    putExtra("id_producto", productoSeleccionado.idProducto)
                    putExtra("id_categoria", productoSeleccionado.idCategoria)
                    putExtra("id_marca", productoSeleccionado.idMarca)
                    putExtra("nombre", productoSeleccionado.nombre)
                    putExtra("descripcion", productoSeleccionado.descripcion)
                    putExtra("precio", productoSeleccionado.precio)
                    putExtra("imagenUrl", productoSeleccionado.imagenUrl)
                }
                startActivity(intent)
            },
            onDeleteClick = { productoSeleccionado ->
                productoSeleccionado.idProducto?.let { id ->
                    eliminarProducto(id)
                } ?: Toast.makeText(this, "No se puede eliminar un producto sin ID", Toast.LENGTH_SHORT).show()
            }
        )
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        cargarProducto() // Refresca automáticamente la lista al volver del formulario
    }

    // --- Obtener productos desde la API ---
    private fun cargarProducto() {
        // Llama a obtenerProducto() en singular tal como lo tienes en tu ApiService.kt actual
        ApiClient.apiService.obtenerProducto().enqueue(object : Callback<List<Producto>> {
            override fun onResponse(call: Call<List<Producto>>, response: Response<List<Producto>>) {
                if (response.isSuccessful && response.body() != null) {
                    listaProductos = response.body()!! // Corregido espaciado
                    adapter.actualizarLista(listaProductos)
                } else {
                    // MEJORA EXTRA: Ahora te dirá qué código de error tira el servidor (ej: 404, 500, 400)
                    Toast.makeText(this@ProductoActivity, "Error de servidor: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Producto>>, t: Throwable) {
                // Muestra la excepción exacta de red (ej: Connection refused, Timeout, etc.)
                Toast.makeText(this@ProductoActivity, "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    // --- Eliminar producto desde la API ---
    private fun eliminarProducto(idProducto: Int) {
        ApiClient.apiService.eliminarProducto(idProducto).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProductoActivity, "Producto eliminado correctamente", Toast.LENGTH_SHORT).show()
                    cargarProducto() // Recarga la lista para que desaparezca el ítem borrado
                } else {
                    Toast.makeText(this@ProductoActivity, "No se pudo eliminar: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ProductoActivity, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
            }
        })
    }
}