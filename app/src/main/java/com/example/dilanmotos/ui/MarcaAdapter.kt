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
    private val esAdmin: Boolean,
    private val onEditClick: (Marca) -> Unit,
    private val onDeleteClick: (Marca) -> Unit
) : RecyclerView.Adapter<MarcaAdapter.MarcaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarcaViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_marca, parent, false)
        return MarcaViewHolder(vista)
    }

    override fun onBindViewHolder(holder: MarcaViewHolder, position: Int) {
        val marca = marcas[position]

        holder.txtNombre.text = marca.nombre

        if (esAdmin) {
            holder.btnEditar.visibility = View.VISIBLE
            holder.btnEliminar.visibility = View.VISIBLE
            holder.btnEditar.setOnClickListener { onEditClick(marca) }
            holder.btnEliminar.setOnClickListener { onDeleteClick(marca) }
        } else {
            holder.btnEditar.visibility = View.GONE
            holder.btnEliminar.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = marcas.size

    fun actualizarLista(nuevaLista: List<Marca>) {
        this.marcas = nuevaLista
        notifyDataSetChanged()
    }

    class MarcaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombreMarca)
        val btnEditar: ImageView = itemView.findViewById(R.id.btnEditarMarca)
        val btnEliminar: ImageView = itemView.findViewById(R.id.btnEliminarMarca)
    }
}