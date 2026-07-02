package com.example.dilanmotos.ui

import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dilanmotos.R
import com.example.dilanmotos.api.ApiClient
import com.example.dilanmotos.model.ConsultaRequest
import com.example.dilanmotos.model.IaResponse
import com.example.dilanmotos.model.MensajeChat
import com.example.dilanmotos.session.SessionManager // Importación del manager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatIaActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter
    private lateinit var edtFalla: EditText
    private lateinit var btnEnviar: ImageButton
    private lateinit var sessionManager: SessionManager // Instancia agregada

    private val historialMensajes = ArrayList<MensajeChat>()
    private var modeloMoto: String = "Moto General"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_ia)

        // Inicializar el SessionManager para poder leer el idUsuario de la sesión activa
        sessionManager = SessionManager(this)

        // Intentamos capturar el modelo de la moto si viene de otra pantalla
        modeloMoto = intent.getStringExtra("MODELO_MOTO") ?: "Gixxer 250"

        // 1. Inicializar los componentes de la interfaz
        recyclerView = findViewById(R.id.recyclerViewChat)
        edtFalla = findViewById(R.id.edtFalla)
        btnEnviar = findViewById(R.id.btnEnviarChat)

        // 2. Configurar el RecyclerView para el chat (los mensajes se apilan desde abajo)
        adapter = ChatAdapter(historialMensajes)
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        recyclerView.adapter = adapter

        // 3. Agregar mensaje de bienvenida automático de la IA
        if (historialMensajes.isEmpty()) {
            historialMensajes.add(MensajeChat("¡Hola! Soy el asistente técnico con IA de Dilan Motos. ¿Qué falla o duda tienes sobre tu $modeloMoto?", false))
            adapter.notifyItemInserted(0)
        }

        // 4. Configurar la acción del botón de enviar
        btnEnviar.setOnClickListener {
            val textoFalla = edtFalla.text.toString().trim()
            if (textoFalla.isNotEmpty()) {
                enviarConsultaIa(textoFalla)
            }
        }
    }

    private fun enviarConsultaIa(falla: String) {
        // Añadir el mensaje que escribió el usuario inmediatamente al RecyclerView
        historialMensajes.add(MensajeChat(falla, true))
        adapter.notifyItemInserted(historialMensajes.size - 1)
        recyclerView.scrollToPosition(historialMensajes.size - 1)

        // Limpiar el campo de texto para el próximo mensaje
        edtFalla.text.clear()

        // 5. CORRECCIÓN: Extraer el ID real de la sesión (ej. el id 6 que vimos en tus logs)
        val idUsuarioLogueado = sessionManager.getIdUsuario()

        // 6. CORRECCIÓN: Crear el request con los 3 campos serializados que requiere el Backend
        val request = ConsultaRequest(
            idUsuario = idUsuarioLogueado,
            motor = modeloMoto,
            falla = falla
        )

        // Deshabilitar temporalmente el botón para evitar spam de clics mientras procesa la IA
        btnEnviar.isEnabled = false

        // Consumir el endpoint /api/ia/consultar a través de Retrofit
        ApiClient.apiService.consultarIA(request).enqueue(object : Callback<IaResponse> {
            override fun onResponse(call: Call<IaResponse>, response: Response<IaResponse>) {
                btnEnviar.isEnabled = true // Reactivar el botón

                if (response.isSuccessful && response.body() != null) {
                    // Extraer el texto del campo 'content' del JSON de respuesta
                    val respuestaIa = response.body()!!.content

                    // Añadir la respuesta de la Inteligencia Artificial al chat
                    historialMensajes.add(MensajeChat(respuestaIa, false))
                    adapter.notifyItemInserted(historialMensajes.size - 1)
                    recyclerView.scrollToPosition(historialMensajes.size - 1)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    Toast.makeText(this@ChatIaActivity, "Servidor: $errorBody", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<IaResponse>, t: Throwable) {
                btnEnviar.isEnabled = true // Reactivar el botón ante caídas de red
                Toast.makeText(this@ChatIaActivity, "Fallo de conexión: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}