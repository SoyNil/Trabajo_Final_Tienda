package com.example.trabajo_final

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class DetalleProducto : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var textNombre: TextView
    private lateinit var textPrecio: TextView
    private lateinit var textDescripcion: TextView
    private lateinit var textNombreUsuario: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_producto)

        // Inicializar las vistas
        imageView = findViewById(R.id.imageView)
        textNombre = findViewById(R.id.textNombre)
        textPrecio = findViewById(R.id.textPrecio)
        textDescripcion = findViewById(R.id.textDescripcion)
        textNombreUsuario = findViewById(R.id.textNombreUsuario)

        // Obtener los datos del Intent
        val imageUrl = intent.getStringExtra("imageUrl")
        val nombre = intent.getStringExtra("nombre") // Nombre del producto
        val precio = intent.getStringExtra("precio") // Precio del producto
        val descripcion = intent.getStringExtra("descripcion") // Descripción del producto
        val nombreUsuario = intent.getStringExtra("nombreUsuario") // Nombre del usuario

        // Cargar la imagen usando Glide
        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(imageView)
        }

        // Establecer los textos en los TextView
        textNombre.text = nombre ?: "Nombre no disponible"
        textPrecio.text = precio ?: "Precio no disponible"
        textDescripcion.text = descripcion ?: "Descripción no disponible"
        textNombreUsuario.text = nombreUsuario ?: "Usuario no disponible"
    }
}
