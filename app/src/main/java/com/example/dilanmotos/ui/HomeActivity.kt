package com.example.dilanmotos.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.example.dilanmotos.LoginActivity
import com.example.dilanmotos.R
import com.example.dilanmotos.session.SessionManager

class HomeActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sessionManager = SessionManager(this)

        // 1. Configurar Saludo personalizado con flecha
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        tvBienvenida.text = "Hola, ${sessionManager.getNombre()} ▼"

        // Asignar el escuchador para desplegar el menú al hacer clic en el nombre
        tvBienvenida.setOnClickListener { vista ->
            mostrarMenuDesplegable(vista)
        }

        // 2. Mantener la visibilidad de la Card trasera por si acaso
        val btnGestion = findViewById<Button>(R.id.btnGestionSistema)
        if (sessionManager.isAdmin()) {
            btnGestion.visibility = View.VISIBLE
            btnGestion.setOnClickListener {
                startActivity(Intent(this, AdminActivity::class.java))
            }
        } else {
            btnGestion.visibility = View.GONE
        }

        // 3. Acciones de los catálogos y botones principales
        findViewById<Button>(R.id.btnVerMotos).setOnClickListener {
            startActivity(Intent(this, MotoActivity::class.java))
        }

        findViewById<Button>(R.id.btnVerProductos).setOnClickListener {
            startActivity(Intent(this, ProductoActivity::class.java))
        }

        findViewById<Button>(R.id.btnRecomendacionesIA).setOnClickListener {
            val intent = Intent(this, ChatIaActivity::class.java).apply {
                putExtra("MODELO_MOTO", "Moto General")
            }
            startActivity(intent)
        }

        // 4. Botón físico de cerrar sesión en la parte inferior
        findViewById<Button>(R.id.btnCerrarSesion).setOnClickListener {
            ejecutarCerrarSesion()
        }
    }

    /**
     * Crea e infla el menú flotante al lado del nombre de usuario
     */
    private fun mostrarMenuDesplegable(anchor: View) {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.menu_home_dropdown, popup.menu)

        // CONTROL DE SEGURIDAD: Ocultar "Gestión de Sistema" del menú si NO es administrador
        if (!sessionManager.isAdmin()) {
            popup.menu.findItem(R.id.menu_gestion_sistema)?.isVisible = false
        }

        // Escuchador de las opciones del desplegable
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_mi_cuenta -> {
                    val intent = Intent(this, MiCuentaActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_asistente_ia -> {
                    val intent = Intent(this, ChatIaActivity::class.java).apply {
                        putExtra("MODELO_MOTO", "Moto General")
                    }
                    startActivity(intent)
                    true
                }
                R.id.menu_mi_historial -> {
                    Toast.makeText(this, "Abriendo Historial", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_radicar_pqrs -> {
                    Toast.makeText(this, "Módulo PQRS", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_hacer_cotizacion -> {
                    // Abre tu pantalla de cotizaciones directo para el cliente
                    startActivity(Intent(this, CotizacionActivity::class.java))
                    true
                }
                R.id.menu_gestion_sistema -> {
                    // Solo accesible si pasó el filtro de arriba
                    startActivity(Intent(this, AdminActivity::class.java))
                    true
                }
                R.id.menu_cerrar_sesion -> {
                    ejecutarCerrarSesion()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun ejecutarCerrarSesion() {
        sessionManager.cerrarSesion()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        super.onBackPressed()
    }
}