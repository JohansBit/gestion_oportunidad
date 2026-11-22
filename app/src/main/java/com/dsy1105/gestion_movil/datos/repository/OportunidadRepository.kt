package com.dsy1105.gestion_movil.datos.repository

import com.dsy1105.gestion_movil.datos.local.OportunidadDao
import com.dsy1105.gestion_movil.datos.local.OportunidadEntity
import com.dsy1105.gestion_movil.datos.local.SessionManager
import com.dsy1105.gestion_movil.datos.remote.RetrofitClient
import com.dsy1105.gestion_movil.datos.remote.toDto
import kotlinx.coroutines.flow.Flow

class OportunidadRepository (private val dao: OportunidadDao){
    val todasLasOportunidades : Flow<List<OportunidadEntity>> = dao.obtenerTodas()

    suspend fun sincronizarConServidor(){
        val response = RetrofitClient.apiService.listarOportunidades()
        if(response.isSuccessful){
            val remotas = response.body()?.map{it.toEntity()} ?: emptyList()
            remotas.forEach { dao.insertar(it) }

        }
    }
    suspend fun guardar(oportunidad: OportunidadEntity){
        val userId = SessionManager.getUserId()
        val dto = oportunidad.toDto(creadoporId = userId, responsableId = userId)
        val response = if(oportunidad.id==0L){
            RetrofitClient.apiService.crearOportunidad((dto))
        } else {
            RetrofitClient.apiService.actualizarOportunidad(
                id= oportunidad.id,
                oportunidad= dto,
                solicitanteId = userId
            )
        }
        if (response.isSuccessful && response.body() != null){
            dao.insertar(response.body()!!.toEntity())
        } else {
            throw Exception("Error del servidor (${response.code()})")
        }
    }

    suspend fun obtenerPorId(id: Long): OportunidadEntity?{
        return dao.obtenerPorId(id)
    }

    suspend fun eliminar(oportunidad: OportunidadEntity) {
        val response = RetrofitClient.apiService.eliminarOportunidad(
            id=oportunidad.id,
            solicitanteId = SessionManager.getUserId()
        )
        if(response.isSuccessful){
            dao.eliminar(oportunidad)
        } else{
            throw Exception("No autorizado o error al eliminar (${response.code()})")
        }
    }

    suspend fun eliminarPorId(id: Long){
        val response = RetrofitClient.apiService.eliminarOportunidad(id=id,
            solicitanteId = SessionManager.getUserId()
        )
        if(response.isSuccessful){
            dao.eliminarPorId(id)
        } else{
            throw Exception("No autorizado o error al eliminar (${response.code()})")
        }
    }
}