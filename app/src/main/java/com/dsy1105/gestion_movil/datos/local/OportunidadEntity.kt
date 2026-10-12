package com.dsy1105.gestion_movil.datos.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "oportunidades")
data class OportunidadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val tipo: String,
    val descripcion: String,
    val fuente: String= "",
    val prioridad:String,
    val potencial:String ="",
    val estado : String= "Evento Identificado",
    val observaciones: String = ""
)