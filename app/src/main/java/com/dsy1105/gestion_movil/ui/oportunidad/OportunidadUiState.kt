package com.dsy1105.gestion_movil.ui.oportunidad

data class OportunidadFormState(
    val id: Long = 0L,
    val nombre: String = "",
    val tipo: String = "",
    val descripcion: String = "",
    val fuente: String = "",
    val fechaDeteccion: String = "",
    val responsableInterno: String = "",
    val prioridad: String = "Media",
    val potencial: String = "Medio",
    val estado: String = "Evento identificado",
    val fechaActualizacion: String = "",
    val observaciones: String = "",

    // Mensajes de error de validación
    val nombreError: String? = null,
    val tipoError: String? = null,
    val descripcionError: String? = null,
    val responsableInternoError: String? = null,

    // Estado del formulario
    val isGuardando: Boolean = false,
    val isGuardadoExitoso: Boolean = false,
    val mensajeExito: String? = null,
    val mensajeError: String? = null
)

object OportunidadOpciones {
    val tipos = listOf(
        "Evento corporativo",
        "Feria empresarial",
        "Actividad municipal",
        "Jornada de beneficios",
        "Feria institucional",
        "Actividad de trabajadores",
        "Activación comercial",
        "Otro"
    )

    val prioridades = listOf("Alta", "Media", "Baja")

    val potenciales = listOf("Alto", "Medio", "Bajo")

    val estados = listOf(
        "Evento identificado",
        "Contacto pendiente",
        "Contacto realizado",
        "Propuesta enviada",
        "En espera de respuesta",
        "Participación confirmada",
        "Oportunidad descartada",
        "En evaluación",
        "Información incompleta",
        "Requiere seguimiento",
        "Activación realizada",
        "Activación cancelada"
    )
}
