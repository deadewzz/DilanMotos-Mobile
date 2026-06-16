package com.example.dilanmotos.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dilanmotos.R

class MiCuentaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mi_cuenta)

        // Vincular componentes
        val btnVolver = findViewById<Button>(R.id.btnCuentaVolver)
        val btnCerrarSesion = findViewById<Button>(R.id.btnCuentaCerrarSesion)
        val tvNombre = findViewById<TextView>(R.id.tvCuentaNombre)
        val tvEmail = findViewById<TextView>(R.id.tvCuentaEmail)

        // Configurar datos estáticos de prueba (Pronto los traerás de tu API/SharedPreferences)
        tvNombre.text = "an"
        tvEmail.text = "an@gmail.com"

        // Acción para regresar al menú anterior
        btnVolver.setOnClickListener {
            finish()
        }

        // Acción para cerrar sesión
        btnCerrarSesion.setOnClickListener {
            // Aquí agregarás la lógica para limpiar sesión (Token, SharedPreferences, etc.)
            finishAffinity() // Por ahora cierra la app de ejemplo
        }
    }
}