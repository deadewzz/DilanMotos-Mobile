package com.example.dilanmotos.session

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    // 1. CORRECCIÓN: Apuntar al mismo archivo global que lee el ApiClient y MainActivity
    private val prefs: SharedPreferences =
        context.getSharedPreferences("DilanMotosPrefs", Context.MODE_PRIVATE)

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
            // 2. CORRECCIÓN: Usar las llaves estandarizadas para todo el ecosistema de la app
            putInt("id_usuario_sesion", idUsuario) // Para MainActivity
            putString("nombre", nombre)
            putString("rol", rol)
            putString("token_sesion", token)       // Para ApiClient (Bearer Token)
            putBoolean("sesionActiva", true)
            apply()
        }
    }

    // 3. CORRECCIÓN: Ajustar los getters para que lean las llaves correctas
    fun getNombre(): String = prefs.getString("nombre", "") ?: ""
    fun getCorreo(): String = prefs.getString("correo", "") ?: "" // Nota: Recuerda guardar el correo si lo necesitas usar
    fun getRol(): String = prefs.getString("rol", "") ?: ""
    fun getToken(): String = prefs.getString("token_sesion", "") ?: ""
    fun getIdUsuario(): Int = prefs.getInt("id_usuario_sesion", -1)
    fun isSesionActiva(): Boolean = prefs.getInt("id_usuario_sesion", -1) != -1
    fun isAdmin(): Boolean = getRol().uppercase() == ROL_ADMIN

    fun cerrarSesion() {
        prefs.edit().clear().apply()
    }
}