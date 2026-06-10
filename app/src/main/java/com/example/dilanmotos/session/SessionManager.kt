package com.example.dilanmotos.session

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("DilanMotosSession", Context.MODE_PRIVATE)

    companion object {
        const val ROL_ADMIN = "ADMIN"
        const val ROL_USUARIO = "USUARIO"
    }

    fun guardarSesion(
        idUsuario: Int,
        nombre: String,
        rol: String,
        token: String
    ) {
        prefs.edit().apply {
            putInt("idUsuario", idUsuario)
            putString("nombre", nombre)
            putString("rol", rol)
            putString("token", token)
            putBoolean("sesionActiva", true)
            apply()
        }
    }

    fun getNombre(): String = prefs.getString("nombre", "") ?: ""
    fun getCorreo(): String = prefs.getString("correo", "") ?: ""
    fun getRol(): String = prefs.getString("rol", "") ?: ""
    fun getToken(): String = prefs.getString("token", "") ?: ""
    fun getIdUsuario(): Int = prefs.getInt("idUsuario", -1)
    fun isSesionActiva(): Boolean = prefs.getBoolean("sesionActiva", false)
    fun isAdmin(): Boolean = getRol().uppercase() == ROL_ADMIN

    fun cerrarSesion() {
        prefs.edit().clear().apply()
    }
}