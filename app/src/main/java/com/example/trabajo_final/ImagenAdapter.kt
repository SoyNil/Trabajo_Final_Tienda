package com.example.trabajo_final

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImagenAdapter(private val productos: List<Producto>, private val context: Context) :
    RecyclerView.Adapter<ImagenAdapter.ImagenViewHolder>() {

    class ImagenViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagenViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_imagen, parent, false)
        return ImagenViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImagenViewHolder, position: Int) {
        val producto = productos[position]
        Glide.with(holder.imageView.context).load(producto.imageUrl).into(holder.imageView)

        // Configurar el clic en la imagen
        holder.imageView.setOnClickListener {
            val context = holder.imageView.context
            // Crear un Intent para abrir DetalleProducto
            val intent = Intent(context, DetalleProducto::class.java).apply {
                putExtra("imageUrl", producto.imageUrl)
                putExtra("nombre", producto.nombre)
                putExtra("precio", producto.precio)
                putExtra("descripcion", producto.descripcion)
                putExtra("nombreUsuario", producto.nombreUsuario)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = productos.size
}

