package com.dsy1105.gestion_movil.datos.repository

import com.dsy1105.gestion_movil.datos.local.OportunidadDao
import com.dsy1105.gestion_movil.datos.local.OportunidadEntity
import kotlinx.coroutines.flow.Flow

class OportunidadRepository (private val dao: OportunidadDao){
    val todasLasOportunidades : Flow<List<OportunidadEntity>> = dao.obtenerTodas()

    suspend fun guardar(oportunidad: OportunidadEntity){
        if(oportunidad.id==0L){
            dao.insertar(oportunidad)
        } else {
            dao.actualizar(oportunidad)
        }
    }

    suspend fun obtenerPorId(id: Long): OportunidadEntity?{
        return dao.obtenerPorId(id)
    }
}