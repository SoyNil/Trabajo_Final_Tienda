package com.example.trabajo_final

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import android.widget.Button
import android.content.Intent

class PaginaPrincipal : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pagina_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()

        // Configurar el botón de cerrar sesión
        val botonCerrarSesion = findViewById<Button>(R.id.botonCerrarSesion)
        botonCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
    }
    private fun cerrarSesion() {
        // Cerrar sesión del usuario
        auth.signOut()

        // Redirigir a la actividad de inicio de sesión
        val intent = Intent(this, MainActivity::class.java) // Cambia LoginActivity al nombre de tu actividad de inicio de sesión
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Finaliza la actividad actual
    }
}