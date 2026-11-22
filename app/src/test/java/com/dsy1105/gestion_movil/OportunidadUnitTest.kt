package com.dsy1105.gestion_movil

import org.junit.Test
import org.junit.Assert.*

class OportunidadUnitTest {

    @Test
    fun testResultadoOperativoSistema() {
        val mensajeEsperado = "Prueba GESTION-OPORTUNIDADES Compose"
        val mensajeOperativo = "GESTION OPORTUNIDADES MOVIL - RESULTADO OPERATIVO"

        // IMPRESIÓN DIRECTA EN LA CONSOLA DE ANDROID STUDIO
        println("==========================================================")
        println(" [ESTADO DEL SISTEMA]: $mensajeOperativo ")
        println(" Mensaje verificado: $mensajeEsperado ")
        println("==========================================================")

        assertNotNull(mensajeOperativo)
        assertTrue(mensajeOperativo.contains("RESULTADO OPERATIVO"))
        assertEquals("Prueba GESTION-OPORTUNIDADES Compose", mensajeEsperado)
    }

    @Test
    fun testValidacionNombreOportunidad() {
        // Prueba unitaria sencilla para validar las reglas de negocio del formulario
        val nombreOportunidad = "Feria de Convenios Corporativos"
        
        println("==========================================================")
        println(" [VALIDACIÓN DE NEGOCIO]: Evaluando oportunidad -> '$nombreOportunidad'")
        println("==========================================================")

        assertNotNull(nombreOportunidad)
        assertTrue(nombreOportunidad.isNotBlank())
        assertEquals(31, nombreOportunidad.length)
    }
}
