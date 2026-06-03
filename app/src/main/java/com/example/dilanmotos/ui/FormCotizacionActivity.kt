package com.example.dilanmotos.ui

import android.os.Bundle
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
    private lateinit var etCantidad: EditText
    private lateinit var etPrecioUnitario: EditText
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
        etCantidad = findViewById(R.id.etCantidadCotizacion)
        etPrecioUnitario = findViewById(R.id.etPrecioUnitarioCotizacion)
        etFecha = findViewById(R.id.etFechaCotizacionForm)
        spEstado = findViewById(R.id.spEstadoCotizacion)
        btnGuardar = findViewById(R.id.btnGuardarCotizacion)
        txtTitulo = findViewById(R.id.txtTituloFormularioCotizacion)

        // 2. Configurar el Spinner de Estados (Agregado / Pendiente)
        val estadosArray = arrayOf("Agregado", "Pendiente")
        val estadoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estadosArray)
        estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spEstado.adapter = estadoAdapter

        // 3. Cargar los productos desde la API para el Spinner dinámico
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

                    // Extraer solo los nombres de los productos para mostrarlos en el Spinner
                    val nombresProductos = listaProductos.map { it.nombre }

                    val productoAdapter = ArrayAdapter(
                        this@FormCotizacionActivity,
                        android.R.layout.simple_spinner_item,
                        nombresProductos
                    )
                    productoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spProducto.adapter = productoAdapter

                    // Si estamos en modo EDICIÓN, sincronizamos los datos después de cargar el spinner
                    evaluarModoEdicion()
                }
            }

            override fun onFailure(call: Call<List<Producto>>, t: Throwable) {
                Toast.makeText(
                    this@FormCotizacionActivity,
                    "Error al cargar productos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun evaluarModoEdicion() {
        if (intent.hasExtra("id_cotizacion")) {
            idCotizacionEditar = intent.getIntExtra("id_cotizacion", -1)
            if (idCotizacionEditar == -1) idCotizacionEditar = null

            etFecha.setText(intent.getStringExtra("fecha"))
            etCantidad.setText(intent.getIntExtra("cantidad", 0).toString())
            etPrecioUnitario.setText(intent.getDoubleExtra("precio_unitario", 0.0).toString())

            // Seleccionar el producto correspondiente en el Spinner
            val productoNombre = intent.getStringExtra("producto")
            val posicionProducto = listaProductos.indexOfFirst { it.nombre == productoNombre }
            if (posicionProducto != -1) spProducto.setSelection(posicionProducto)

            // Seleccionar el estado correspondiente en el Spinner (true = Agregado, false = Pendiente)
            val productoAgregado = intent.getBooleanExtra("producto_agregado", true)
            if (productoAgregado) spEstado.setSelection(0) else spEstado.setSelection(1)

            txtTitulo.text = "Editar Cotización"
            btnGuardar.text = "Actualizar Cambios"
        }
    }

    private fun guardarDatos() {
        val cantidadStr = etCantidad.text.toString().trim()
        val precioUnitarioStr = etPrecioUnitario.text.toString().trim()
        val fecha = etFecha.text.toString().trim()

        // 1. Diagnóstico de campos vacíos o Spinner sin cargar
        if (spProducto.selectedItem == null) {
            Toast.makeText(this, "Error: No hay productos cargados en la lista", Toast.LENGTH_LONG)
                .show()
            return
        }
        if (cantidadStr.isEmpty() || precioUnitarioStr.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Extraer el Producto y su ID correspondiente basado en la posición del Spinner
        val posicionSeleccionada = spProducto.selectedItemPosition
        val productoObjeto = listaProductos[posicionSeleccionada]

        val idProductoSeleccionado = productoObjeto.idProducto ?: 0
        val nombreProductoSeleccionado = productoObjeto.nombre

        val cantidad = cantidadStr.toIntOrNull() ?: 0
        val precioUnitario = precioUnitarioStr.toDoubleOrNull() ?: 0.0

        // Mapeo: "Agregado" -> true, "Pendiente" -> false
        val productoAgregado = spEstado.selectedItem.toString() == "Agregado"

        // ID temporal del usuario (asegúrate de que exista el ID 1 en tu tabla usuario)
        val idUsuarioSesion = 1

        // 3. Construcción del Objeto sincronizado con tu Backend Hexagonal
        val cotizacion = Cotizacion(
            idCotizacion = idCotizacionEditar,
            idUsuario = idUsuarioSesion,
            idProducto = idProductoSeleccionado, // ID enviado correctamente
            producto = nombreProductoSeleccionado,
            cantidad = cantidad,
            precioUnitario = precioUnitario,
            fecha = fecha, // Formato esperado AAAA-MM-DD
            productoAgregado = productoAgregado
        )

        if (idCotizacionEditar == null) {
            ApiClient.apiService.crearCotizacion(cotizacion).enqueue(object : Callback<Cotizacion> {
                override fun onResponse(call: Call<Cotizacion>, response: Response<Cotizacion>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@FormCotizacionActivity,
                            "Cotización creada con éxito",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@FormCotizacionActivity,
                            "Error de servidor al guardar: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Cotizacion>, t: Throwable) {
                    Toast.makeText(
                        this@FormCotizacionActivity,
                        "Fallo de red: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        } else {
            ApiClient.apiService.actualizarCotizacion(idCotizacionEditar!!, cotizacion)
                .enqueue(object : Callback<Cotizacion> {
                    override fun onResponse(
                        call: Call<Cotizacion>,
                        response: Response<Cotizacion>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@FormCotizacionActivity,
                                "Cotización actualizada con éxito",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            Toast.makeText(
                                this@FormCotizacionActivity,
                                "Error de servidor al actualizar: ${response.code()}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Cotizacion>, t: Throwable) {
                        Toast.makeText(
                            this@FormCotizacionActivity,
                            "Fallo de red: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }
    }
}