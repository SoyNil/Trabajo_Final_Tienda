package com.example.trabajo_final

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class RegistroActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference  // Propiedad para la referencia de Firebase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar la referencia de Firebase Realtime Database
        database = FirebaseDatabase.getInstance().reference

        // Referencia al botón de registro
        val btnIniciarSesion = findViewById<Button>(R.id.Iniciar_Sesion1)

        // Agregar el listener para redirigir a RegistroActivity al presionar el botón
        btnIniciarSesion.setOnClickListener {
            // Crear un intent para iniciar la nueva actividad
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) // Iniciar la nueva actividad
        }

        // Obtener las referencias de los EditText
        val nombreEditText = findViewById<EditText>(R.id.nombre_usu)
        val apellidoEditText = findViewById<EditText>(R.id.apellido_usu)
        val correoEditText = findViewById<EditText>(R.id.correo_regis)
        val contrasenaEditText = findViewById<EditText>(R.id.contrasena_regis)

        // Botón para registrar
        val btnRegistrar = findViewById<Button>(R.id.Registrar_Usuario1)

        // Listener del botón de registro
        btnRegistrar.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val apellido = apellidoEditText.text.toString()
            val correo = correoEditText.text.toString()
            val contrasena = contrasenaEditText.text.toString()

            // Verificar si los campos están completos
            if (nombre.isNotEmpty() && apellido.isNotEmpty() && correo.isNotEmpty() && contrasena.isNotEmpty()) {
                // Verificar si ya existe un usuario con el mismo nombre, apellido o correo
                verificarUsuarioExistente(nombre, apellido, correo) { usuarioExiste ->
                    if (!usuarioExiste) {
                        // Si no existe, registrar al usuario
                        // Contar cuántos usuarios ya existen
                        database.child("usuarios").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val usuarioCount = dataSnapshot.childrenCount
                                val nuevoNombreUsuario = "usuario${usuarioCount + 1}" // Crear un nombre secuencial

                                val usuario = mapOf(
                                    "nombre" to nombre,
                                    "apellido" to apellido,
                                    "correo" to correo,
                                    "contrasena" to contrasena
                                )

                                database.child("usuarios").child(nuevoNombreUsuario).setValue(usuario)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this@RegistroActivity, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                                            // Redirigir a la actividad principal o inicio de sesión
                                            startActivity(Intent(this@RegistroActivity, MainActivity::class.java))
                                            finish() // Finaliza la actividad de registro
                                        } else {
                                            Toast.makeText(this@RegistroActivity, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Toast.makeText(this@RegistroActivity, "Error al contar usuarios: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    } else {
                        // Si ya existe, mostrar un mensaje de error
                        Toast.makeText(this, "Usuario con el mismo nombre, apellido o correo ya existe", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para verificar si un usuario con el mismo nombre, apellido o correo ya existe en la base de datos
    private fun verificarUsuarioExistente(nombre: String, apellido: String, correo: String, callback: (Boolean) -> Unit) {
        val query = database.child("usuarios").orderByChild("nombre").equalTo(nombre)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var usuarioExiste = false

                for (snapshot in dataSnapshot.children) {
                    val usuarioApellido = snapshot.child("apellido").getValue(String::class.java)
                    val usuarioCorreo = snapshot.child("correo").getValue(String::class.java)

                    // Verificar si tanto el nombre, apellido o correo coinciden
                    if (usuarioApellido == apellido || usuarioCorreo == correo) {
                        usuarioExiste = true
                        break
                    }
                }

                callback(usuarioExiste)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@RegistroActivity, "Error en la consulta: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                callback(false)
            }
        })
    }
}