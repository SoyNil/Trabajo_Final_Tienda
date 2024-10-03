package com.example.trabajo_final

data class Producto(
    val nombre: String = "",
    val precio: String = "",
    val descripcion: String = "",
    val imageUrl: String = "",
    val nombreUsuario: String = ""
) {
    // Constructor sin argumentos
    constructor() : this("", "", "", "", "")
}
