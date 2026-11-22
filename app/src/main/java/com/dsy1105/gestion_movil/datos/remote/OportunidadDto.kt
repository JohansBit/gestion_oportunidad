package com.dsy1105.gestion_movil.datos.remote

import com.dsy1105.gestion_movil.datos.local.OportunidadEntity

data class OportunidadDto(
    val id: Long? = null,
    val nombre: String,
    val tipo: String,
    val descripcion: String? = null,
    val fuente: String = "",
    val fechaDeteccion: String? = null,
    val responsableNombre: String? = null,
    val prioridad: String = "Media",
    val potencial: String = "Medio",
    val estado: String = "Evento identificado",
    val fechaActualizacion: String? = null,
    val observaciones: String? = null,
    val creadoporId: Long? = null,
    val responsableId: Long? = null
) {
    fun toEntity(): OportunidadEntity {
        return OportunidadEntity(
            id = this.id ?: 0L,
            nombre = this.nombre,
            tipo = this.tipo,
            descripcion = this.descripcion ?: this.observaciones ?: "",
            fuente = this.fuente,
            fechaDeteccion = this.fechaDeteccion?.take(10) ?: "",
            responsableInterno = this.responsableNombre ?: "",
            prioridad = this.prioridad,
            potencial = this.potencial,
            estado = this.estado,
            fechaActualizacion = this.fechaActualizacion?.take(10) ?: "",
            observaciones = this.observaciones ?: ""
        )
    }
}

fun OportunidadEntity.toDto(creadoporId: Long? = null, responsableId: Long? = null): OportunidadDto {
    return OportunidadDto(
        id = if (this.id == 0L) null else this.id,
        nombre = this.nombre,
        tipo = this.tipo,
        descripcion = this.descripcion,
        fuente = this.fuente.ifBlank { "App Movil" },
        fechaDeteccion = null,
        responsableNombre = this.responsableInterno,
        prioridad = this.prioridad,
        potencial = this.potencial,
        estado = this.estado,
        fechaActualizacion = null,
        observaciones = this.observaciones.ifBlank { this.descripcion },
        creadoporId = creadoporId,
        responsableId = responsableId
    )
}