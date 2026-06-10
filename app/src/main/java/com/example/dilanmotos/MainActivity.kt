package com.example.dilanmotos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dilanmotos.ui.HomeActivity // Importación explícita del paquete ui

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Leer directamente del SharedPreferences global de DilanMotos
        val sharedPref = applicationContext.getSharedPreferences("DilanMotosPrefs", Context.MODE_PRIVATE)

        // 2. Si el id_usuario_sesion es mayor a 0, la sesión es válida
        val idUsuario = sharedPref.getInt("id_usuario_sesion", -1)
        val isSesionActiva = idUsuario != -1

        // 3. Determinar el destino correcto referenciando bien la clase HomeActivity
        val destino = if (isSesionActiva) {
            Intent(this, HomeActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }

        // 4. Navegar y destruir esta actividad de paso
        startActivity(destino)
        finish()
    }
}