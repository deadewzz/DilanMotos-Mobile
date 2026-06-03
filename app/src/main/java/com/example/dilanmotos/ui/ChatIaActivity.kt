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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatIaActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter
    private lateinit var edtFalla: EditText
    private lateinit var btnEnviar: ImageButton

    private val historialMensajes = ArrayList<MensajeChat>()
    private var modeloMoto: String = "Moto General"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_ia) // Infla el XML principal del chat

        // Intentamos capturar el modelo de la moto si viene de otra pantalla, si no, usa uno por defecto
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
        historialMensajes.add(MensajeChat("¡Hola! Soy el asistente técnico con IA de Dilan Motos. ¿Qué falla o duda tienes sobre tu $modeloMoto?", false))
        adapter.notifyItemInserted(0)

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

        // Crear el objeto Request estructurado exactamente como lo espera tu controlador de Spring Boot
        val request = ConsultaRequest(motor = modeloMoto, falla = falla)

        // Consumir el endpoint /api/ia/consultar a través de Retrofit
        ApiClient.apiService.consultarIA(request).enqueue(object : Callback<IaResponse> {
            override fun onResponse(call: Call<IaResponse>, response: Response<IaResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    // Extraer el texto del campo 'content' del JSON de respuesta
                    val respuestaIa = response.body()!!.content

                    // Añadir la respuesta de la Inteligencia Artificial al chat
                    historialMensajes.add(MensajeChat(respuestaIa, false))
                    adapter.notifyItemInserted(historialMensajes.size - 1)
                    recyclerView.scrollToPosition(historialMensajes.size - 1)
                } else {
                    Toast.makeText(this@ChatIaActivity, "Error en el servidor de IA", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<IaResponse>, t: Throwable) {
                Toast.makeText(this@ChatIaActivity, "Fallo de conexión: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}