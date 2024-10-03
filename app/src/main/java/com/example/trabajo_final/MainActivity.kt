package com.example.trabajo_final

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent // Para usar la clase Intent
import android.widget.Button // Para usar la clase Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    // Referencia a la base de datos
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referencia al botón de registro
        val btnRegistrar = findViewById<Button>(R.id.Registrar_Usuario)

        // Agregar el listener para redirigir a RegistroActivity al presionar el botón
        btnRegistrar.setOnClickListener {
            // Crear un intent para iniciar la nueva actividad
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent) // Iniciar la nueva actividad
        }
        // Inicializa la base de datos de Firebase
        database = FirebaseDatabase.getInstance().reference.child("usuarios")

        // Referencias a los EditText
        val correoEditText = findViewById<EditText>(R.id.correo)
        val contrasenaEditText = findViewById<EditText>(R.id.contrasena)

        // Referencia al botón de inicio de sesión
        val btnIniciarSesion = findViewById<Button>(R.id.Iniciar_Sesión)

        // Listener del botón de inicio de sesión
        btnIniciarSesion.setOnClickListener {
            val correo = correoEditText.text.toString()
            val contrasena = contrasenaEditText.text.toString()

            // Verifica si los campos están completos
            if (correo.isNotEmpty() && contrasena.isNotEmpty()) {
                // Llama a la función para verificar el correo y la contraseña
                verificarCredenciales(correo, contrasena)
            } else {
                Toast.makeText(this, "Por favor, complete ambos campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // Función para verificar las credenciales en Firebase
    private fun verificarCredenciales(correo: String, contrasena: String) {
        val query = database.orderByChild("correo").equalTo(correo)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Recorre los resultados para verificar la contraseña
                    for (usuarioSnapshot in dataSnapshot.children) {
                        val usuarioContrasena = usuarioSnapshot.child("contrasena").getValue(String::class.java)
                        val nombre = usuarioSnapshot.child("nombre").getValue(String::class.java)
                        val apellido = usuarioSnapshot.child("apellido").getValue(String::class.java)

                        // Verifica si la contraseña coincide
                        if (usuarioContrasena == contrasena) {
                            // Si coincide, redirige a la actividad PaginaPrincipal
                            val intent = Intent(this@MainActivity, PaginaPrincipal::class.java).apply {
                                putExtra("nombre", nombre)
                                putExtra("apellido", apellido)
                            }
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@MainActivity, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Correo no registrado", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error en la consulta: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}