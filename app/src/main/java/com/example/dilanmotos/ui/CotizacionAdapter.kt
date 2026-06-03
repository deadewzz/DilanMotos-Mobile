package com.example.dilanmotos.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dilanmotos.R
import com.example.dilanmotos.model.Cotizacion

class CotizacionAdapter(
    private var cotizaciones: List<Cotizacion>,
    private val onEditClick: (Cotizacion) -> Unit,
    private val onDeleteClick: (Cotizacion) -> Unit
) : RecyclerView.Adapter<CotizacionAdapter.CotizacionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CotizacionViewHolder {
        // Inflamos el diseño correspondiente a una sola cotización
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cotizacion, parent, false)
        return CotizacionViewHolder(vista)
    }

    override fun onBindViewHolder(holder: CotizacionViewHolder, position: Int) {
        val cotizacion = cotizaciones[position]

        // Asignamos los datos de la cotización a la vista
        holder.txtProducto.text = cotizacion.producto
        holder.txtCantidad.text = "Cantidad: ${cotizacion.cantidad}"
        holder.txtPrecioUnitario.text = "Precio Unitario: $${String.format("%.2f", cotizacion.precioUnitario)}"
        holder.txtFecha.text = "Fecha: ${cotizacion.fecha}"

        // Eventos de clic para editar y eliminar
        holder.btnEditar.setOnClickListener { onEditClick(cotizacion) }
        holder.btnEliminar.setOnClickListener { onDeleteClick(cotizacion) }
    }

    override fun getItemCount(): Int = cotizaciones.size

    // Función para refrescar la lista cuando cambien los datos en la base de datos
    fun actualizarLista(nuevaLista: List<Cotizacion>) {
        this.cotizaciones = nuevaLista
        notifyDataSetChanged()
    }

    // Contenedor de las vistas asignadas a cada elemento
    class CotizacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtProducto: TextView = itemView.findViewById(R.id.txtProductoCotizacion)
        val txtCantidad: TextView = itemView.findViewById(R.id.txtCantidadCotizacion)
        val txtPrecioUnitario: TextView = itemView.findViewById(R.id.txtPrecioUnitarioCotizacion)
        val txtFecha: TextView = itemView.findViewById(R.id.txtFechaCotizacion)
        val btnEditar: ImageView = itemView.findViewById(R.id.btnEditarCotizacion)
        val btnEliminar: ImageView = itemView.findViewById(R.id.btnEliminarCotizacion)
    }
}