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

        // Verificar que el usuario siga siendo admin (seguridad extra)
        if (!sessionManager.isAdmin()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        findViewById<TextView>(R.id.tvAdminBienvenida).text =
            "Panel Admin — ${sessionManager.getNombre()}"

        // Acceso a todos los módulos
        findViewById<Button>(R.id.btnAdminUsuarios).setOnClickListener {
            startActivity(Intent(this, UsuarioActivity::class.java))
        }

        findViewById<Button>(R.id.btnAdminMotos).setOnClickListener {
            startActivity(Intent(this, MotoActivity::class.java))
        }

        findViewById<Button>(R.id.btnAdminProductos).setOnClickListener {
            startActivity(Intent(this, ProductoActivity::class.java))
        }

        findViewById<Button>(R.id.btnAdminMarcas).setOnClickListener {
            startActivity(Intent(this, MarcaActivity::class.java))
        }

        findViewById<Button>(R.id.btnAdminIA).setOnClickListener {
            val intent = Intent(this, ChatIaActivity::class.java).apply {
                putExtra("MODELO_MOTO", "Moto General")
            }
            startActivity(intent)
        }

        // Cerrar sesión
        findViewById<Button>(R.id.btnAdminCerrarSesion).setOnClickListener {
            sessionManager.cerrarSesion()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        // Bloquear retroceso igual que en HomeActivity
    }
}