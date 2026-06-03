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
import com.example.dilanmotos.model.Marca
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FormMarcaActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var btnGuardar: Button
    private lateinit var txtTitulo: TextView
    private var idMarcaEditar: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_form_marca)

        // 1. Inicializar componentes mapeando el layout XML de marca
        etNombre = findViewById(R.id.etNombreMarca)


        btnGuardar = findViewById(R.id.btnGuardarMarca)
        txtTitulo = findViewById(R.id.txtTituloFormulario)

        // 2. Evaluar si viene desde la acción "Editar" (Sincronizado con MarcaActivity)
        if (intent.hasExtra("id_marca")) {
            idMarcaEditar = intent.getIntExtra("id_marca", -1)
            if (idMarcaEditar == -1) idMarcaEditar = null

            etNombre.setText(intent.getStringExtra("nombre"))

            // Cambiar textos de la interfaz para indicar Modo Edición
            txtTitulo.text = "Editar Marca"
            btnGuardar.text = "Actualizar Cambios"
        }

        btnGuardar.setOnClickListener { guardarDatos() }
    }

    private fun guardarDatos() {
        val nombre = etNombre.text.toString().trim()

        // Validar que los campos obligatorios del negocio no estén vacíos
        if (nombre.isEmpty()) {
            Toast.makeText(this, "Llena los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // 3. Determinar si se envía a Crear o a Actualizar en la API
        if (idMarcaEditar == null) {
            val marcaNueva = Marca(
                idMarca = null,
                nombre = nombre
            )

            // Acción: Crear nueva marca (api/marcas)
            ApiClient.apiService.crearMarca(marcaNueva).enqueue(object : Callback<Marca> {
                override fun onResponse(call: Call<Marca>, response: Response<Marca>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@FormMarcaActivity, "Marca creada con éxito", Toast.LENGTH_SHORT).show()
                        finish() // Cierra la pantalla y regresa al listado
                    } else {
                        // Agregamos el código de error para diagnosticar fallas de lógica en el DTO (Ej: 400)
                        Toast.makeText(this@FormMarcaActivity, "Error en el servidor al crear: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Marca>, t: Throwable) {
                    Toast.makeText(this@FormMarcaActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            val marcaEditar = Marca(
                idMarca = idMarcaEditar,
                nombre = nombre
            )

            // Acción: Actualizar marcas existente pasándole su ID numérico
            ApiClient.apiService.actualizarMarca(idMarcaEditar!!, marcaEditar).enqueue(object : Callback<Marca> {
                override fun onResponse(call: Call<Marca>, response: Response<Marca>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@FormMarcaActivity, "Marca actualizada con éxito", Toast.LENGTH_SHORT).show()
                        finish() // Regresa al listado
                    } else {
                        Toast.makeText(this@FormMarcaActivity, "Error en el servidor al actualizar: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Marca>, t: Throwable) {
                    Toast.makeText(this@FormMarcaActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}