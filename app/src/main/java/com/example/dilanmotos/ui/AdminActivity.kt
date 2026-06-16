package com.example.dilanmotos.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dilanmotos.LoginActivity
import com.example.dilanmotos.R
import com.example.dilanmotos.session.SessionManager

class AdminActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        sessionManager = SessionManager(this)

        if (!sessionManager.isAdmin()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        // Cambiar título de bienvenida
        findViewById<TextView>(R.id.tvAdminBienvenida)?.text = "Panel Admin — ${sessionManager.getNombre()}"

        // Módulos con navegación segura (usando ?.setOnClickListener en lugar de .setOnClickListener)
        findViewById<Button>(R.id.btnAdminUsuarios)?.setOnClickListener {
            startActivity(Intent(this, UsuarioActivity::class.java))
        }

        findViewById<Button>(R.id.btnAdminMotos)?.setOnClickListener {
            startActivity(Intent(this, MotoActivity::class.java))
        }

        findViewById<Button>(R.id.btnAdminProductos)?.setOnClickListener {
            startActivity(Intent(this, ProductoActivity::class.java))
        }

        findViewById<Button>(R.id.btnAdminMarcas)?.setOnClickListener {
            startActivity(Intent(this, MarcaActivity::class.java))
        }

        findViewById<Button>(R.id.btnAdminCotizaciones)?.setOnClickListener {
            startActivity(Intent(this, CotizacionActivity::class.java))
        }

        findViewById<Button>(R.id.btnAdminIA)?.setOnClickListener {
            val intent = Intent(this, ChatIaActivity::class.java).apply {
                putExtra("MODELO_MOTO", "Moto General")
            }
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnVolverHome)?.setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnAdminCerrarSesion)?.setOnClickListener {
            sessionManager.cerrarSesion()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        super.onBackPressed()
    }
}