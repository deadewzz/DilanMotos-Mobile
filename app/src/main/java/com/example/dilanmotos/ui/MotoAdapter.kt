package com.example.dilanmotos.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dilanmotos.R
import com.example.dilanmotos.model.Moto

class MotoAdapter(
    private var motos: List<Moto>,
    private val onEditClick: (Moto) -> Unit,
    private val onDeleteClick: (Moto) -> Unit
) : RecyclerView.Adapter<MotoAdapter.MotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MotoViewHolder {
        // Inflamos el diseño correspondiente a una sola Moto
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_moto, parent, false)
        return MotoViewHolder(vista)
    }

    override fun onBindViewHolder(holder: MotoViewHolder, position: Int) {
        val moto = motos[position]

        // Asignamos los datos de la moto a la vista
        holder.txtModelo.text = moto.modelo
        holder.txtAnio.text = "CC: ${moto.cilindraje ?: 0.0}"

        // Mostramos el nombre de la marca asociada si el objeto marca no es nulo
        holder.txtMarca.text = moto.marca?.nombre

        // Eventos de clic para editar y eliminar pasándole el objeto completo
        holder.btnEditar.setOnClickListener { onEditClick(moto) }
        holder.btnEliminar.setOnClickListener { onDeleteClick(moto) }
    }

    override fun getItemCount(): Int = motos.size

    // Función para refrescar la lista cuando cambien los datos en la base de datos
    fun actualizarLista(nuevaLista: List<Moto>) {
        this.motos = nuevaLista
        notifyDataSetChanged()
    }

    // Contenedor de las vistas asignadas a cada elemento en el XML item_moto
    class MotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtModelo: TextView = itemView.findViewById(R.id.txtModeloMoto)
        val txtMarca: TextView = itemView.findViewById(R.id.txtMarcaMoto)
        val txtAnio: TextView = itemView.findViewById(R.id.txtAnioMoto)
        val btnEditar: ImageView = itemView.findViewById(R.id.btnEditarMoto)
        val btnEliminar: ImageView = itemView.findViewById(R.id.btnEliminarMoto)
    }
}