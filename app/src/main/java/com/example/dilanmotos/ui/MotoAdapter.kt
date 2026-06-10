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
    private val esAdmin: Boolean,
    private val onEditClick: (Moto) -> Unit,
    private val onDeleteClick: (Moto) -> Unit
) : RecyclerView.Adapter<MotoAdapter.MotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MotoViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_moto, parent, false)
        return MotoViewHolder(vista)
    }

    override fun onBindViewHolder(holder: MotoViewHolder, position: Int) {
        val moto = motos[position]

        holder.txtModelo.text = moto.modelo
        holder.txtAnio.text = "CC: ${moto.cilindraje ?: 0.0}"
        holder.txtMarca.text = moto.marca?.nombre

        // Mostrar botones solo si es admin
        if (esAdmin) {
            holder.btnEditar.visibility = View.VISIBLE
            holder.btnEliminar.visibility = View.VISIBLE
            holder.btnEditar.setOnClickListener { onEditClick(moto) }
            holder.btnEliminar.setOnClickListener { onDeleteClick(moto) }
        } else {
            holder.btnEditar.visibility = View.GONE
            holder.btnEliminar.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = motos.size

    fun actualizarLista(nuevaLista: List<Moto>) {
        this.motos = nuevaLista
        notifyDataSetChanged()
    }

    class MotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtModelo: TextView = itemView.findViewById(R.id.txtModeloMoto)
        val txtMarca: TextView = itemView.findViewById(R.id.txtMarcaMoto)
        val txtAnio: TextView = itemView.findViewById(R.id.txtAnioMoto)
        val btnEditar: ImageView = itemView.findViewById(R.id.btnEditarMoto)
        val btnEliminar: ImageView = itemView.findViewById(R.id.btnEliminarMoto)
    }
}