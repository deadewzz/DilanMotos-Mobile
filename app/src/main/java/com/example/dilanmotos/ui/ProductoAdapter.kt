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
    private val onEditClick: (Producto) -> Unit,
    private val onDeleteClick: (Producto) -> Unit
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        // Inflamos el diseño correspondiente a un solo producto
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]

        // Asignamos los datos del producto a la vista
        holder.txtNombre.text = producto.nombre
        holder.txtPrecio.text = "Precio: $${String.format("%.2f", producto.precio)}"
        holder.txtDescripcion.text = producto.descripcion ?: "Sin descripción"

        // Eventos de clic para editar y eliminar
        holder.btnEditar.setOnClickListener { onEditClick(producto) }
        holder.btnEliminar.setOnClickListener { onDeleteClick(producto) }
    }

    override fun getItemCount(): Int = productos.size

    // Función para refrescar la lista cuando cambien los datos en la base de datos
    fun actualizarLista(nuevaLista: List<Producto>) {
        this.productos = nuevaLista
        notifyDataSetChanged()
    }

    // Contenedor de las vistas asignadas a cada elemento
    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombreProducto)
        val txtPrecio: TextView = itemView.findViewById(R.id.txtPrecioProducto)
        val txtDescripcion: TextView = itemView.findViewById(R.id.txtDescripcionProducto)
        val btnEditar: ImageView = itemView.findViewById(R.id.btnEditarProducto)
        val btnEliminar: ImageView = itemView.findViewById(R.id.btnEliminarProducto)
    }
}