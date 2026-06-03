package com.example.dilanmotos.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dilanmotos.R
import com.example.dilanmotos.model.Usuario

class UsuarioAdapter(
    private var usuarios: List<Usuario>,
    private val onEditClick: (Usuario) -> Unit,
    private val onDeleteClick: (Usuario) -> Unit
) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(vista)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = usuarios[position]

        holder.txtNombre.text = usuario.nombre
        holder.txtCorreo.text = usuario.correo
        holder.txtRol.text = "Rol: ${usuario.rol}"

        holder.btnEditar.setOnClickListener { onEditClick(usuario) }
        holder.btnEliminar.setOnClickListener { onDeleteClick(usuario) }
    }

    override fun getItemCount(): Int = usuarios.size

    fun actualizarLista(nuevaLista: List<Usuario>) {
        this.usuarios = nuevaLista
        notifyDataSetChanged()
    }

    class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombreUsuario)
        val txtCorreo: TextView = itemView.findViewById(R.id.txtCorreoUsuario)
        val txtRol: TextView = itemView.findViewById(R.id.txtRolUsuario)
        val btnEditar: ImageView = itemView.findViewById(R.id.btnEditarUsuario)
        val btnEliminar: ImageView = itemView.findViewById(R.id.btnEliminarUsuario)
    }
}