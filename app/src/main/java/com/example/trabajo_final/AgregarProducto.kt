package com.example.trabajo_final

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.auth.FirebaseAuth
import android.text.TextWatcher
import android.text.Editable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class AgregarProducto : AppCompatActivity() {
    private lateinit var imgProducto: ImageView
    private lateinit var imgUri: Uri
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var nombre: String // Declarar variable nombre
    private lateinit var apellido: String // Declarar variable apellido

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_producto)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Obtener el nombre del usuario desde el Intent
        nombre = intent.getStringExtra("nombre") ?: ""
        apellido = intent.getStringExtra("apellido") ?: ""
        val btnSeleccionarImagen = findViewById<Button>(R.id.prodIMG)
        imgProducto = findViewById(R.id.img_prod)
        val btnAgregarProducto = findViewById<Button>(R.id.btnAgregar)

        val nombreProd = findViewById<EditText>(R.id.prodNombre)
        val precioProd = findViewById<EditText>(R.id.prodPrecio)
        val descripcionProd = findViewById<EditText>(R.id.prodDescripcion)

        // Botón para seleccionar imagen
        btnSeleccionarImagen.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Botón para agregar producto
        btnAgregarProducto.setOnClickListener {
            val nombre = nombreProd.text.toString()
            val precio = precioProd.text.toString()
            val descripcion = descripcionProd.text.toString()

            if (nombre.isNotEmpty() && precio.isNotEmpty() && descripcion.isNotEmpty() && ::imgUri.isInitialized) {
                // Subir la imagen y los datos a Firebase
                subirProducto(nombre, precio, descripcion)
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
        precioProd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // Verificar que no haya más de dos decimales
                val text = s.toString()
                if (text.contains(".")) {
                    val parts = text.split(".")
                    if (parts.size > 2 || parts[1].length > 2) {
                        // Si hay más de un punto decimal o más de dos decimales, restablecer el texto
                        precioProd.setText(parts[0] + "." + (parts[1].take(2)))
                        precioProd.setSelection(precioProd.text.length) // Mover el cursor al final
                    }
                }
            }
        })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imgUri = data.data!!
            imgProducto.setImageURI(imgUri)
        }
    }
    private fun subirProducto(nombreProducto: String, precio: String, descripcion: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("productos")

        // Contar cuántos productos ya existen
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Obtener el conteo de productos
                val productoCount = snapshot.childrenCount

                // Crear el nombre secuencial
                val nuevoNombre = "producto${productoCount + 1}" // Se incrementa en 1 para el nuevo producto

                // Obtener el ID del usuario autenticado
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                val storageRef = FirebaseStorage.getInstance().reference.child("productos/${System.currentTimeMillis()}.jpg")
                val uploadTask = storageRef.putFile(imgUri)

                uploadTask.addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        // Crear el producto con el userId y el nombre del usuario que inició sesión
                        val producto = Producto(nombreProducto, precio, descripcion, uri.toString(), nombre) // Usa `nombre` aquí

                        // Guardar el producto en la base de datos usando el nuevo nombre
                        databaseRef.child(nuevoNombre).setValue(producto)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this@AgregarProducto, "Producto agregado correctamente", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this@AgregarProducto, "Error al agregar producto", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }.addOnFailureListener {
                    Toast.makeText(this@AgregarProducto, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AgregarProducto, "Error al contar productos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    data class Producto(
        val nombre: String = "", // Constructor sin argumentos
        val precio: String = "",  // Constructor sin argumentos
        val descripcion: String = "",  // Constructor sin argumentos
        val imageUrl: String = "",  // Constructor sin argumentos
        val nombreUsuario: String = ""  // Constructor sin argumentos
    )
}