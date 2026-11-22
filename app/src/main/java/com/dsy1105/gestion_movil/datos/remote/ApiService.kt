package com.dsy1105.gestion_movil.datos.remote

import com.dsy1105.gestion_movil.datos.remote.OportunidadDto
import com.dsy1105.gestion_movil.datos.remote.UsuarioDto
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- USUARIOS ---
    @POST("api/usuarios/login")
    suspend fun login(@Body credenciales: Map<String, String>): Response<UsuarioDto>

    @POST("api/usuarios/registro")
    suspend fun registrar(@Body usuario: UsuarioDto): Response<UsuarioDto>

    @GET("api/usuarios")
    suspend fun listarUsuarios(): Response<List<UsuarioDto>>

    @PUT("api/usuarios/{id}/rol")
    suspend fun actualizarRol(
        @Path("id") usuarioObjetivoId: Long,
        @Query("nuevoRol") nuevoRol: String,
        @Query("adminId") adminId: Long
    ): Response<UsuarioDto>

    // --- OPORTUNIDADES ---
    @GET("api/oportunidades")
    suspend fun listarOportunidades(): Response<List<OportunidadDto>>

    @POST("api/oportunidades")
    suspend fun crearOportunidad(@Body oportunidad: OportunidadDto): Response<OportunidadDto>

    @PUT("api/oportunidades/{id}")
    suspend fun actualizarOportunidad(
        @Path("id") id: Long,
        @Body oportunidad: OportunidadDto,
        @Query("solicitanteId") solicitanteId: Long
    ): Response<OportunidadDto>

    @DELETE("api/oportunidades/{id}")
    suspend fun eliminarOportunidad(
        @Path("id") id: Long,
        @Query("solicitanteId") solicitanteId: Long
    ): Response<Unit>

}