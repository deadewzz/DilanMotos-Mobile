package com.example.dilanmotos.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.dilanmotos.R
import com.example.dilanmotos.api.ApiClient
import com.example.dilanmotos.model.Cotizacion
import com.example.dilanmotos.model.Producto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FormCotizacionActivity : AppCompatActivity() {

    private lateinit var spProducto: Spinner
    private lateinit var etPrecioUnitario: EditText // Agregado para la referencia visual
    private lateinit var etCantidad: EditText
    private lateinit var etFecha: EditText
    private lateinit var spEstado: Spinner
    private lateinit var btnGuardar: Button
    private lateinit var txtTitulo: TextView

    private var idCotizacionEditar: Int? = null
    private var listaProductos: List<Producto> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_form_cotizacion)

        // 1. Inicializar componentes
        spProducto = findViewById(R.id.spProductoCotizacion)
        etPrecioUnitario = findViewById(R.id.etPrecioUnitarioCotizacionForm) // Inicializado
        etCantidad = findViewById(R.id.etCantidadCotizacion)
        etFecha = findViewById(R.id.etFechaCotizacionForm)
        spEstado = findViewById(R.id.spEstadoCotizacion)
        btnGuardar = findViewById(R.id.btnGuardarCotizacion)
        txtTitulo = findViewById(R.id.txtTituloFormularioCotizacion)

        // 2. Configurar el Spinner de Estados (Agregado / Pendiente)
        val estadosArray = arrayOf("Agregado", "Pendiente")
        val estadoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estadosArray)
        estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spEstado.adapter = estadoAdapter

        // 3. Detectar cambios en la selección del Spinner para actualizar el precio referencial
        spProducto.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (listaProductos.isNotEmpty()) {
                    val productoSeleccionado = listaProductos[position]
                    etPrecioUnitario.setText(productoSeleccionado.precio?.toString() ?: "0.0")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 4. Cargar los productos desde la API
        cargarProductosParaSpinner()

        btnGuardar.setOnClickListener { guardarDatos() }
    }

    private fun cargarProductosParaSpinner() {
        ApiClient.apiService.obtenerProducto().enqueue(object : Callback<List<Producto>> {
            override fun onResponse(
                call: Call<List<Producto>>,
                response: Response<List<Producto>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    listaProductos = response.body()!!

                    val nombresProductos = listaProductos.map { it.nombre }

                    val productoAdapter = ArrayAdapter(
                        this@FormCotizacionActivity,
                        android.R.layout.simple_spinner_item,
                        nombresProductos
                    )
                    productoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spProducto.adapter = productoAdapter

                    // Sincronizar datos si estamos editando
                    evaluarModoEdicion()
                }
            }

            override fun onFailure(call: Call<List<Producto>>, t: Throwable) {
                Toast.makeText(this@FormCotizacionActivity, "Error al cargar productos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun evaluarModoEdicion() {
        if (intent.hasExtra("id_colizazion") || intent.hasExtra("id_cotizacion")) {
            idCotizacionEditar = intent.getIntExtra("id_cotizacion", -1)
            if (idCotizacionEditar == -1) idCotizacionEditar = intent.getIntExtra("id_colizazion", -1)
            if (idCotizacionEditar == -1) idCotizacionEditar = null

            etFecha.setText(intent.getStringExtra("fecha"))
            etCantidad.setText(intent.getIntExtra("cantidad", 0).toString())

            val productoNombre = intent.getStringExtra("producto")
            val posicionProducto = listaProductos.indexOfFirst { it.nombre == productoNombre }
            if (posicionProducto != -1) {
                spProducto.setSelection(posicionProducto)
                // Forzar la colocación del precio unitario en modo edición
                etPrecioUnitario.setText(listaProductos[posicionProducto].precio?.toString() ?: "0.0")
            }

            val productoAgregado = intent.getBooleanExtra("producto_agregado", true)
            if (productoAgregado) spEstado.setSelection(0) else spEstado.setSelection(1)

            txtTitulo.text = "Editar Cotización"
            btnGuardar.text = "Actualizar Cambios"
        }
    }

    private fun guardarDatos() {
        val cantidadStr = etCantidad.text.toString().trim()
        val fecha = etFecha.text.toString().trim()

        if (spProducto.selectedItem == null) {
            Toast.makeText(this, "Error: No hay productos cargados en la lista", Toast.LENGTH_LONG).show()
            return
        }
        if (cantidadStr.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val posicionSeleccionada = spProducto.selectedItemPosition
        val productoObjeto = listaProductos[posicionSeleccionada]

        val idProductoSeleccionado = productoObjeto.idProducto ?: 0
        val nombreProductoSeleccionado = productoObjeto.nombre
        val precioUnitarioAuto = productoObjeto.precio ?: 0.0

        val cantidad = cantidadStr.toIntOrNull() ?: 0
        val productoAgregado = spEstado.selectedItem.toString() == "Agregado"

        val sharedPreferences = applicationContext.getSharedPreferences("DilanMotosPrefs", Context.MODE_PRIVATE)
        val idUsuarioSesionReal = sharedPreferences.getInt("id_usuario_sesion", -1)

        if (idUsuarioSesionReal == -1) {
            Toast.makeText(this, "Error: Sesión inválida. Por favor vuelve a ingresar.", Toast.LENGTH_LONG).show()
            return
        }

        val cotizacion = Cotizacion(
            idCotizacion = idCotizacionEditar,
            idUsuario = idUsuarioSesionReal,
            idProducto = idProductoSeleccionado,
            producto = nombreProductoSeleccionado,
            cantidad = cantidad,
            precioUnitario = precioUnitarioAuto,
            fecha = fecha,
            productoAgregado = productoAgregado
        )

        if (idCotizacionEditar == null) {
            ApiClient.apiService.crearCotizacion(cotizacion).enqueue(object : Callback<Cotizacion> {
                override fun onResponse(call: Call<Cotizacion>, response: Response<Cotizacion>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@FormCotizacionActivity, "Cotización creada con éxito", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@FormCotizacionActivity, "Error de servidor al guardar: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Cotizacion>, t: Throwable) {
                    Toast.makeText(this@FormCotizacionActivity, "Fallo de red: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        } else {
            ApiClient.apiService.actualizarCotizacion(idCotizacionEditar!!, cotizacion)
                .enqueue(object : Callback<Cotizacion> {
                    override fun onResponse(call: Call<Cotizacion>, response: Response<Cotizacion>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@FormCotizacionActivity, "Cotización actualizada con éxito", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@FormCotizacionActivity, "Error de servidor al actualizar: ${response.code()}", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<Cotizacion>, t: Throwable) {
                        Toast.makeText(this@FormCotizacionActivity, "Fallo de red: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }
}