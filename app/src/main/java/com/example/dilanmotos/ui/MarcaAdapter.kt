package com.example.dilanmotos.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dilanmotos.R
import com.example.dilanmotos.model.Marca

class MarcaAdapter(
    private var marcas: List<Marca>,
    private val onEditClick: (Marca) -> Unit,
    private val onDeleteClick: (Marca) -> Unit
) : RecyclerView.Adapter<MarcaAdapter.MarcaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarcaViewHolder {
        // Inflamos el diseño correspondiente a una sola Marca
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_marca, parent, false)
        return MarcaViewHolder(vista)
    }

    override fun onBindViewHolder(holder: MarcaViewHolder, position: Int) {
        val marca = marcas[position]

        // Asignamos los datos del producto a la vista
        holder.txtNombre.text = marca.nombre

        // Eventos de clic para editar y eliminar
        holder.btnEditar.setOnClickListener { onEditClick(marca) }
        holder.btnEliminar.setOnClickListener { onDeleteClick(marca) }
    }

    override fun getItemCount(): Int = marcas.size

    // Función para refrescar la lista cuando cambien los datos en la base de datos
    fun actualizarLista(nuevaLista: List<Marca>) {
        this.marcas = nuevaLista
        notifyDataSetChanged()
    }

    // Contenedor de las vistas asignadas a cada elemento
    class MarcaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombreMarca)
        val btnEditar: ImageView = itemView.findViewById(R.id.btnEditarMarca)
        val btnEliminar: ImageView = itemView.findViewById(R.id.btnEliminarMarca)
    }
}