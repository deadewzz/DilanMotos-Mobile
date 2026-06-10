package com.example.dilanmotos.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dilanmotos.R
import com.example.dilanmotos.model.Producto

class ProductoAdapter(
    private var productos: List<Producto>,
    private val esAdmin: Boolean,
    private val onEditClick: (Producto) -> Unit,
    private val onDeleteClick: (Producto) -> Unit
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]

        holder.txtNombre.text = producto.nombre
        holder.txtPrecio.text = "Precio: $${String.format("%.2f", producto.precio)}"
        holder.txtDescripcion.text = producto.descripcion ?: "Sin descripción"

        if (esAdmin) {
            holder.btnEditar.visibility = View.VISIBLE
            holder.btnEliminar.visibility = View.VISIBLE
            holder.btnEditar.setOnClickListener { onEditClick(producto) }
            holder.btnEliminar.setOnClickListener { onDeleteClick(producto) }
        } else {
            holder.btnEditar.visibility = View.GONE
            holder.btnEliminar.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = productos.size

    fun actualizarLista(nuevaLista: List<Producto>) {
        this.productos = nuevaLista
        notifyDataSetChanged()
    }

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombreProducto)
        val txtPrecio: TextView = itemView.findViewById(R.id.txtPrecioProducto)
        val txtDescripcion: TextView = itemView.findViewById(R.id.txtDescripcionProducto)
        val btnEditar: ImageView = itemView.findViewById(R.id.btnEditarProducto)
        val btnEliminar: ImageView = itemView.findViewById(R.id.btnEliminarProducto)
    }
}