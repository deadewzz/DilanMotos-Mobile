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
import com.example.dilanmotos.model.Marca
import com.example.dilanmotos.model.Moto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FormMotoActivity : AppCompatActivity() {

    private lateinit var etModelo: EditText
    private lateinit var etAnio: EditText
    private lateinit var etCilindraje: EditText  // ✅ nuevo campo
    private lateinit var spnMarcas: Spinner
    private lateinit var btnGuardar: Button
    private lateinit var txtTitulo: TextView

    private var idMotoEditar: Int? = null
    private var listaMarcas: List<Marca> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_form_moto)

        etModelo = findViewById(R.id.etModeloMoto)
        etAnio = findViewById(R.id.etAnioMoto)
        etCilindraje = findViewById(R.id.etCilindrajeMoto) // ✅ asegúrate de tener este ID en el XML
        spnMarcas = findViewById(R.id.spnMarcas)
        btnGuardar = findViewById(R.id.btnGuardar)
        txtTitulo = findViewById(R.id.txtTitulo)

        if (intent.hasExtra("ID_MOTO")) {
            idMotoEditar = intent.getIntExtra("ID_MOTO", -1)
            txtTitulo.text = "Editar Moto"
            btnGuardar.text = "Actualizar"
        } else {
            txtTitulo.text = "Nueva Moto"
            btnGuardar.text = "Guardar"
        }

        cargarMarcasYConfigurar()

        btnGuardar.setOnClickListener {
            guardarDatos()
        }

        findViewById<Button>(R.id.btnVolverAlMenuMoto)?.setOnClickListener {
            finish()
        }
    }

    private fun cargarMarcasYConfigurar() {
        ApiClient.apiService.obtenerMarca().enqueue(object : Callback<List<Marca>> {
            override fun onResponse(call: Call<List<Marca>>, response: Response<List<Marca>>) {
                if (response.isSuccessful && response.body() != null) {
                    listaMarcas = response.body()!!

                    val nombresMarcas = listaMarcas.map { it.nombre }
                    val adapter = ArrayAdapter(
                        this@FormMotoActivity,
                        android.R.layout.simple_spinner_item,
                        nombresMarcas
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spnMarcas.adapter = adapter

                    if (idMotoEditar != null) {
                        etModelo.setText(intent.getStringExtra("MODELO_MOTO"))
                        etAnio.setText(intent.getIntExtra("ANIO_MOTO", 2026).toString())

                        val idMarcaMoto = intent.getIntExtra("ID_MARCA_MOTO", -1)
                        val posicion = listaMarcas.indexOfFirst { (it.idMarca ?: -1) == idMarcaMoto }
                        if (posicion != -1) spnMarcas.setSelection(posicion)
                    }
                } else {
                    Toast.makeText(this@FormMotoActivity, "Error al cargar marcas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Marca>>, t: Throwable) {
                Toast.makeText(this@FormMotoActivity, "Fallo de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun guardarDatos() {
        val modelo = etModelo.text.toString().trim()
        val anioStr = etAnio.text.toString().trim()
        val cilindrajeStr = etCilindraje.text.toString().trim()

        if (modelo.isEmpty() || anioStr.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (listaMarcas.isEmpty()) {
            Toast.makeText(this, "No hay marcas disponibles", Toast.LENGTH_SHORT).show()
            return
        }

        val marcaSeleccionada = listaMarcas[spnMarcas.selectedItemPosition]
        val cilindraje = cilindrajeStr.toDoubleOrNull() ?: 0.0 // ✅ si está vacío usa 0.0

        val moto = Moto(
            idMoto = if (idMotoEditar != null && idMotoEditar!! > 0) idMotoEditar else null,
            idMarca = marcaSeleccionada.idMarca,  // ✅ solo el ID, no el objeto
            idUsuario = null,
            modelo = modelo,
            cilindraje = cilindraje               // ✅ campo real
        )

        if (idMotoEditar == null) {
            ApiClient.apiService.crearMoto(moto).enqueue(object : Callback<Moto> {
                override fun onResponse(call: Call<Moto>, response: Response<Moto>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@FormMotoActivity, "Moto guardada con éxito", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@FormMotoActivity, "Error al guardar: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Moto>, t: Throwable) {
                    Toast.makeText(this@FormMotoActivity, "Fallo de red: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            ApiClient.apiService.actualizarMoto(idMotoEditar!!, moto).enqueue(object : Callback<Moto> {
                override fun onResponse(call: Call<Moto>, response: Response<Moto>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@FormMotoActivity, "Moto actualizada con éxito", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@FormMotoActivity, "Error al actualizar: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Moto>, t: Throwable) {
                    Toast.makeText(this@FormMotoActivity, "Fallo de red: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}