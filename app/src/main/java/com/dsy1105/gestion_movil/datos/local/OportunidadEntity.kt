package com.dsy1105.gestion_movil.datos.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "oportunidades")
data class OportunidadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val tipo: String,
    val descripcion: String,
    val fuente: String = "",
    val fechaDeteccion: String = "",
    val responsableInterno: String = "",
    val prioridad: String = "Media",
    val potencial: String = "Medio",
    val estado: String = "Evento identificado",
    val fechaActualizacion: String = "",
    val observaciones: String = ""
)
