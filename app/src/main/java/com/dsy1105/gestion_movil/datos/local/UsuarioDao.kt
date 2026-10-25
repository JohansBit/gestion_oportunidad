package com.dsy1105.gestion_movil.datos.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    // Guarda el usuario registrado en la base de datos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(usuario: UsuarioEntity): Long

    // Valida las credenciales para el inicio de sesión
    @Query("SELECT * FROM usuarios WHERE correo = :correo AND clave = :clave LIMIT 1")
    suspend fun autenticar(correo: String, clave: String): UsuarioEntity?

    // Evita duplicar correos al registrar
    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): UsuarioEntity?

    // Obtiene el último registrado (según la guía de clase)
    @Query("SELECT * FROM usuarios ORDER BY id DESC LIMIT 1")
    fun obtenerUltimo(): Flow<UsuarioEntity?>
}