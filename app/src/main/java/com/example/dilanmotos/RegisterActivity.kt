package com.example.dilanmotos.ui

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.dilanmotos.LoginActivity
import com.example.dilanmotos.R
import com.example.dilanmotos.api.ApiClient
import com.example.dilanmotos.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText
    private lateinit var spinnerMarca: Spinner
    private lateinit var spinnerModelo: Spinner
    private lateinit var btnRegistrar: Button
    private lateinit var tvIniciarSesion: TextView

    private var listaMarcas: List<Marca> = emptyList()
    private var todasLasReferencias: List<Referencia> = emptyList()
    private var referenciasFiltradas: List<Referencia> = emptyList()
    private var marcaSeleccionada: Marca? = null
    private var referenciaSeleccionada: Referencia? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        inicializarVistas()
        configurarLinkLogin()
        cargarDatos()
        configurarBotonRegistrar()
    }

    private fun inicializarVistas() {
        etNombre        = findViewById(R.id.etNombre)
        etCorreo        = findViewById(R.id.etCorreo)
        etContrasena    = findViewById(R.id.etContrasena)
        spinnerMarca    = findViewById(R.id.spinnerMarca)
        spinnerModelo   = findViewById(R.id.spinnerModelo)
        btnRegistrar    = findViewById(R.id.btnRegistrar)
        tvIniciarSesion = findViewById(R.id.tvIniciarSesion)

        spinnerModelo.isEnabled = false
        configurarSpinnerModeloVacio()
    }

    private fun configurarLinkLogin() {
        val texto = "¿Ya tienes cuenta? Inicia sesión"
        val spannable = SpannableString(texto)
        val inicio = texto.indexOf("Inicia sesión")
        spannable.setSpan(
            ForegroundColorSpan(0xFF3B3BD6.toInt()),
            inicio, texto.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvIniciarSesion.text = spannable
        tvIniciarSesion.setOnClickListener { irALogin() }
    }

    // ─────────────────────────────────────────────
    // CARGA PARALELA: marcas + referencias
    // ─────────────────────────────────────────────
    private fun cargarDatos() {
        // Primero cargamos referencias, luego marcas
        ApiClient.apiService.obtenerReferencias().enqueue(object : Callback<List<Referencia>> {
            override fun onResponse(call: Call<List<Referencia>>, response: Response<List<Referencia>>) {
                if (response.isSuccessful) {
                    todasLasReferencias = response.body() ?: emptyList()
                }
                cargarMarcas()
            }
            override fun onFailure(call: Call<List<Referencia>>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Error cargando catálogo: ${t.message}", Toast.LENGTH_SHORT).show()
                cargarMarcas()
            }
        })
    }

    private fun cargarMarcas() {
        ApiClient.apiService.obtenerMarca().enqueue(object : Callback<List<Marca>> {
            override fun onResponse(call: Call<List<Marca>>, response: Response<List<Marca>>) {
                if (response.isSuccessful) {
                    listaMarcas = response.body() ?: emptyList()
                    configurarSpinnerMarcas()
                } else {
                    Toast.makeText(this@RegisterActivity, "Error al cargar marcas", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Marca>>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Sin conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ─────────────────────────────────────────────
    // SPINNERS
    // ─────────────────────────────────────────────
    private fun configurarSpinnerMarcas() {
        val opciones = mutableListOf("-- Selecciona una marca --")
        opciones.addAll(listaMarcas.map { it.nombre ?: "Sin nombre" })

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMarca.adapter = adapter

        spinnerMarca.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    marcaSeleccionada = null
                    spinnerModelo.isEnabled = false
                    configurarSpinnerModeloVacio()
                } else {
                    marcaSeleccionada = listaMarcas[position - 1]
                    filtrarReferenciasPorMarca(marcaSeleccionada!!.idMarca!!)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun filtrarReferenciasPorMarca(idMarca: Int) {
        referenciasFiltradas = todasLasReferencias.filter { it.idMarca == idMarca }
        configurarSpinnerModelos()
    }

    private fun configurarSpinnerModeloVacio() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Primero elige una marca"))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerModelo.adapter = adapter
        referenciaSeleccionada = null
    }

    private fun configurarSpinnerModelos() {
        if (referenciasFiltradas.isEmpty()) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("No hay modelos disponibles"))
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerModelo.adapter = adapter
            spinnerModelo.isEnabled = false
            referenciaSeleccionada = null
            return
        }

        val opciones = mutableListOf("-- Selecciona un modelo --")
        opciones.addAll(referenciasFiltradas.map { it.nombre ?: "Sin nombre" })

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerModelo.adapter = adapter
        spinnerModelo.isEnabled = true

        spinnerModelo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                referenciaSeleccionada = if (position == 0) null else referenciasFiltradas[position - 1]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    // ─────────────────────────────────────────────
    // REGISTRO: USUARIO → MOTO
    // ─────────────────────────────────────────────
    private fun configurarBotonRegistrar() {
        btnRegistrar.setOnClickListener {
            if (!validarCampos()) return@setOnClickListener

            val nombre     = etNombre.text.toString().trim()
            val correo     = etCorreo.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            btnRegistrar.isEnabled = false
            btnRegistrar.text = "Registrando..."

            val usuarioNuevo = Usuario(
                idUsuario  = null,
                nombre     = nombre,
                correo     = correo,
                contrasena = contrasena,
                rol        = "USUARIO",
                habilitado = 1
            )

            ApiClient.apiService.crearUsuario(usuarioNuevo).enqueue(object : Callback<Usuario> {
                override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                    if (response.isSuccessful) {
                        val usuarioCreado = response.body()
                        if (usuarioCreado?.idUsuario != null) {
                            registrarMotoParaUsuario(usuarioCreado.idUsuario)
                        } else {
                            Toast.makeText(this@RegisterActivity, "Cuenta creada. Inicia sesión.", Toast.LENGTH_LONG).show()
                            irALogin()
                        }
                    } else {
                        val errorMsg = when (response.code()) {
                            409  -> "Este correo ya está registrado"
                            400  -> "Datos inválidos, revisa el formulario"
                            else -> "Error al crear la cuenta (${response.code()})"
                        }
                        Toast.makeText(this@RegisterActivity, errorMsg, Toast.LENGTH_LONG).show()
                        resetearBoton()
                    }
                }
                override fun onFailure(call: Call<Usuario>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Sin conexión: ${t.message}", Toast.LENGTH_LONG).show()
                    resetearBoton()
                }
            })
        }
    }

    private fun registrarMotoParaUsuario(idUsuario: Int) {
        val referencia = referenciaSeleccionada!!
        val nuevaMoto = Moto(
            idMoto     = null,
            idUsuario  = idUsuario,
            idMarca    = marcaSeleccionada!!.idMarca,
            modelo     = referencia.nombre,
            cilindraje = referencia.cilindraje
        )

        ApiClient.apiService.crearMoto(nuevaMoto).enqueue(object : Callback<Moto> {
            override fun onResponse(call: Call<Moto>, response: Response<Moto>) {
                val msg = if (response.isSuccessful)
                    "¡Registro completado! Ya puedes iniciar sesión."
                else
                    "Cuenta creada, pero error al guardar la moto."
                Toast.makeText(this@RegisterActivity, msg, Toast.LENGTH_LONG).show()
                irALogin()
            }
            override fun onFailure(call: Call<Moto>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Cuenta creada, pero sin conexión para guardar la moto.", Toast.LENGTH_LONG).show()
                irALogin()
            }
        })
    }

    // ─────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────
    private fun validarCampos(): Boolean {
        val nombre     = etNombre.text.toString().trim()
        val correo     = etCorreo.text.toString().trim()
        val contrasena = etContrasena.text.toString().trim()

        if (nombre.isEmpty()) {
            etNombre.error = "Ingresa tu nombre"; etNombre.requestFocus(); return false
        }
        if (correo.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.error = "Ingresa un correo válido"; etCorreo.requestFocus(); return false
        }
        if (contrasena.length < 6) {
            etContrasena.error = "Mínimo 6 caracteres"; etContrasena.requestFocus(); return false
        }
        if (marcaSeleccionada == null) {
            Toast.makeText(this, "Selecciona la marca de tu moto", Toast.LENGTH_SHORT).show(); return false
        }
        if (referenciaSeleccionada == null) {
            Toast.makeText(this, "Selecciona el modelo de tu moto", Toast.LENGTH_SHORT).show(); return false
        }
        return true
    }

    private fun resetearBoton() {
        btnRegistrar.isEnabled = true
        btnRegistrar.text = "Completar Registro"
    }

    private fun irALogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}