package com.dsy1105.gestion_movil

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dsy1105.gestion_movil.datos.local.AppDataBase
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*


@RunWith(AndroidJUnit4::class)
class OportunidadInstrumentedTest {

    //Test 1: Valida el paquete de la aplicación en el dispositivo/emulador.

    @Test
    fun testAppPackageInDevice() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        
        println("==========================================================")
        println(" [ANDROID TEST 1] Paquete verificado: ${appContext.packageName}")
        println("==========================================================")

        assertTrue(appContext.packageName.contains("gestion_movil", ignoreCase = true))
    }

    //Test 2: Valida la creación e inicialización de la base de datos local Room.

    @Test
    fun testRoomDatabaseInstance() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        
        // Obtenemos la instancia de la base de datos de Room en el emulador
        val database = AppDataBase.getDatabase(context)
        val dao = database.oportunidadDao()

        println("==========================================================")
        println(" [ANDROID TEST 2] Base de datos Room inicializada con éxito")
        println("==========================================================")

        assertNotNull("La base de datos no debe ser nula", database)
        assertNotNull("El DAO de oportunidades no debe ser nulo", dao)
        assertTrue("La base de datos debe estar abierta", database.isOpen)
    }
}
