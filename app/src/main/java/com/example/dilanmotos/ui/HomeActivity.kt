package com.example.dilanmotos.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dilanmotos.LoginActivity
import com.example.dilanmotos.R
import com.example.dilanmotos.session.SessionManager

class HomeActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sessionManager = SessionManager(this)

        // Saludo personalizado
        findViewById<TextView>(R.id.tvBienvenida).text =
            "Hola, ${sessionManager.getNombre()} 👋"

        // Botón Gestión de Sistema — solo visible para ADMIN
        val btnGestion = findViewById<Button>(R.id.btnGestionSistema)
        if (sessionManager.isAdmin()) {
            btnGestion.visibility = View.VISIBLE
            btnGestion.setOnClickListener {
                startActivity(Intent(this, AdminActivity::class.java))
            }
        } else {
            btnGestion.visibility = View.GONE
        }

        // Acciones disponibles para todos
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

        // Cerrar sesión
        findViewById<Button>(R.id.btnCerrarSesion).setOnClickListener {
            sessionManager.cerrarSesion()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        // Bloquear retroceso
    }
}