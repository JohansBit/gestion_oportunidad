package com.dsy1105.gestion_movil.datos.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OportunidadDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(oportunidad: OportunidadEntity): Long

    @Update
    suspend fun actualizar(oportunidad: OportunidadEntity)

    @Query("SELECT * FROM oportunidades ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<OportunidadEntity>>

    @Query("SELECT * FROM oportunidades WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Long): OportunidadEntity
}