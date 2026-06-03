package com.example.dilanmotos.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.dilanmotos.R
import com.example.dilanmotos.model.MensajeChat // 🟢 Usando tu modelo real en singular

class ChatAdapter(private val listaMensajes: List<MensajeChat>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // 🟢 IDs idénticos a los de tu archivo xml real
        val txtMensaje: TextView = view.findViewById(R.id.txtMensajeChat)
        val containerMensaje: LinearLayout = view.findViewById(R.id.containerMensajeChat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        // 🟢 Infla correctamente usando R.layout.item_mensaje_chat
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mensaje_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val mensaje = listaMensajes[position]
        holder.txtMensaje.text = mensaje.texto // 🟢 Ahora sí lo reconoce al corregir el modelo

        // 🟢 SOLUCIÓN AL CRASH (Opción A): Usamos los parámetros nativos del RecyclerView
        val params = holder.itemView.layoutParams as RecyclerView.LayoutParams

        val fondoBurbuja = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
        }

        if (mensaje.esUsuario) { // 🟢 Resuelto al corregir el modelo
            // Movemos el diseño interno a la derecha
            holder.containerMensaje.gravity = Gravity.END

            fondoBurbuja.setColor("#2196F3".toColorInt()) // Azul
            fondoBurbuja.cornerRadii = floatArrayOf(32f, 32f, 32f, 32f, 0f, 0f, 32f, 32f)
            holder.txtMensaje.setTextColor(Color.WHITE)
        } else {
            // Movemos el diseño interno a la izquierda
            holder.containerMensaje.gravity = Gravity.START

            fondoBurbuja.setColor("#E0E0E0".toColorInt()) // Gris
            fondoBurbuja.cornerRadii = floatArrayOf(32f, 32f, 32f, 32f, 32f, 32f, 0f, 0f)
            holder.txtMensaje.setTextColor(Color.BLACK)
        }

        holder.txtMensaje.background = fondoBurbuja
        holder.itemView.layoutParams = params
    }

    override fun getItemCount(): Int = listaMensajes.size
}