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
import com.example.dilanmotos.model.Producto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FormProductoActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etPrecio: EditText
    private lateinit var etIdCategoria: EditText
    private lateinit var etIdMarca: EditText
    private lateinit var etImagenUrl: EditText
    private lateinit var btnGuardar: Button
    private lateinit var txtTitulo: TextView
    private var idProductoEditar: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_form_producto)

        // 1. Inicializar componentes mapeando el layout XML de producto
        etNombre = findViewById(R.id.etNombreProducto)
        etDescripcion = findViewById(R.id.etDescripcionProducto)
        etPrecio = findViewById(R.id.etPrecioProducto)
        etIdCategoria = findViewById(R.id.etIdCategoria)
        etIdMarca = findViewById(R.id.etIdMarca)
        etImagenUrl = findViewById(R.id.etImagenUrl)
        btnGuardar = findViewById(R.id.btnGuardarProducto)
        txtTitulo = findViewById(R.id.txtTituloFormulario)

        // 2. Evaluar si viene desde la acción "Editar" (Sincronizado con ProductoActivity)
        if (intent.hasExtra("id_producto")) {
            idProductoEditar = intent.getIntExtra("id_producto", -1)
            if (idProductoEditar == -1) idProductoEditar = null

            etNombre.setText(intent.getStringExtra("nombre"))
            etDescripcion.setText(intent.getStringExtra("descripcion"))

            // Sincronización estricta de llaves numéricas en snake_case
            val precio = intent.getDoubleExtra("precio", 0.0)
            val idCategoria = intent.getIntExtra("id_categoria", 0)
            val idMarca = intent.getIntExtra("id_marca", 0)

            etPrecio.setText(precio.toString())
            etIdCategoria.setText(idCategoria.toString())
            etIdMarca.setText(idMarca.toString())
            etImagenUrl.setText(intent.getStringExtra("imagenUrl"))

            // Cambiar textos de la interfaz para indicar Modo Edición
            txtTitulo.text = "Editar Producto"
            btnGuardar.text = "Actualizar Cambios"
        }

        btnGuardar.setOnClickListener { guardarDatos() }
    }

    private fun guardarDatos() {
        val nombre = etNombre.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()
        val precioStr = etPrecio.text.toString().trim()
        val idCategoriaStr = etIdCategoria.text.toString().trim()
        val idMarcaStr = etIdMarca.text.toString().trim()
        val imagenUrl = etImagenUrl.text.toString().trim()

        // Validar que los campos obligatorios del negocio no estén vacíos
        if (nombre.isEmpty() || precioStr.isEmpty() || idCategoriaStr.isEmpty() || idMarcaStr.isEmpty()) {
            Toast.makeText(this, "Llena los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // Convertir tipos de datos de forma segura
        val precio = precioStr.toDoubleOrNull() ?: 0.0
        val idCategoria = idCategoriaStr.toIntOrNull() ?: 0
        val idMarca = idMarcaStr.toIntOrNull() ?: 0

        // 3. Determinar si se envía a Crear o a Actualizar en la API
        if (idProductoEditar == null) {
            val productoNuevo = Producto(
                idProducto = null,
                idCategoria = idCategoria,
                idMarca = idMarca,
                nombre = nombre,
                descripcion = if (descripcion.isEmpty()) null else descripcion,
                precio = precio,
                imagenUrl = if (imagenUrl.isEmpty()) null else imagenUrl
            )

            // Acción: Crear nuevo producto (api/productos)
            ApiClient.apiService.crearProducto(productoNuevo).enqueue(object : Callback<Producto> {
                override fun onResponse(call: Call<Producto>, response: Response<Producto>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@FormProductoActivity, "Producto creado con éxito", Toast.LENGTH_SHORT).show()
                        finish() // Cierra la pantalla y regresa al listado
                    } else {
                        // Agregamos el código de error para diagnosticar fallas de lógica en el DTO (Ej: 400)
                        Toast.makeText(this@FormProductoActivity, "Error en el servidor al crear: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Producto>, t: Throwable) {
                    Toast.makeText(this@FormProductoActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            val productoEditar = Producto(
                idProducto = idProductoEditar,
                idCategoria = idCategoria,
                idMarca = idMarca,
                nombre = nombre,
                descripcion = if (descripcion.isEmpty()) null else descripcion,
                precio = precio,
                imagenUrl = if (imagenUrl.isEmpty()) null else imagenUrl
            )

            // Acción: Actualizar producto existente pasándole su ID numérico
            ApiClient.apiService.actualizarProducto(idProductoEditar!!, productoEditar).enqueue(object : Callback<Producto> {
                override fun onResponse(call: Call<Producto>, response: Response<Producto>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@FormProductoActivity, "Producto actualizado con éxito", Toast.LENGTH_SHORT).show()
                        finish() // Regresa al listado
                    } else {
                        Toast.makeText(this@FormProductoActivity, "Error en el servidor al actualizar: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Producto>, t: Throwable) {
                    Toast.makeText(this@FormProductoActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}