package com.example.trabajo_final

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import android.widget.Button
import android.content.Intent
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class PaginaPrincipal : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var nombre: String
    private lateinit var apellido: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var imagenAdapter: ImagenAdapter
    private lateinit var productos: MutableList<Producto> // Cambia esto a una lista de productos


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Debe estar al principio
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_principal)

        // Inicializar la lista de productos
        productos = mutableListOf() // Cambiar a lista de productos

        // Inicializar el RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Configurar el adaptador
        imagenAdapter = ImagenAdapter(productos, this) // Pasar la lista de productos al adaptador
        recyclerView.adapter = imagenAdapter

        // Configurar el Listener para obtener datos del Intent
        nombre = intent.getStringExtra("nombre") ?: "Nombre no disponible"
        apellido = intent.getStringExtra("apellido") ?: "Apellido no disponible"
        auth = FirebaseAuth.getInstance()

        // Cargar las imágenes desde Firebase
        cargarImagenes()

        // Configurar el botón de cerrar sesión
        val botonCerrarSesion = findViewById<Button>(R.id.botonCerrarSesion)
        botonCerrarSesion.setOnClickListener {
            cerrarSesion()
        }

        // Configurar el botón de agregar producto
        val btnAgregaProducto = findViewById<Button>(R.id.btnAgregarProducto)
        btnAgregaProducto.setOnClickListener {
            // Crear un intent para iniciar la nueva actividad y pasar los datos
            val intent = Intent(this, AgregarProducto::class.java).apply {
                putExtra("nombre", nombre)  // Almacena el nombre del usuario
                putExtra("apellido", apellido) // Si necesitas el apellido, también lo puedes pasar
            }
            startActivity(intent) // Iniciar la nueva actividad
        }

        // Configurar el padding para el layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun cargarImagenes() {
        val databaseRef = FirebaseDatabase.getInstance().getReference("productos")
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nuevosProductos = mutableListOf<Producto>() // Lista temporal para nuevos productos

                for (data in snapshot.children) {
                    val producto = data.getValue(Producto::class.java)
                    if (producto != null) {
                        nuevosProductos.add(producto) // Agregar a la lista temporal
                    }
                }

                // Compara con la lista actual y actualiza solo lo necesario
                val oldSize = productos.size
                productos.clear()
                productos.addAll(nuevosProductos)

                if (productos.size > oldSize) {
                    imagenAdapter.notifyItemRangeInserted(oldSize, productos.size - oldSize)
                } else {
                    imagenAdapter.notifyDataSetChanged() // Si hay menos productos, es más seguro volver a notificar todos
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PaginaPrincipal, "Error al cargar productos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun cerrarSesion() {
        // Cerrar sesión del usuario
        auth.signOut()

        // Redirigir a la actividad de inicio de sesión
        val intent = Intent(this, MainActivity::class.java) // Cambia MainActivity al nombre de tu actividad de inicio de sesión
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Finaliza la actividad actual
    }
}
