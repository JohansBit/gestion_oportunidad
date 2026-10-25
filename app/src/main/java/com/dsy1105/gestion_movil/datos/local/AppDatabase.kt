package com.dsy1105.gestion_movil.datos.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        OportunidadEntity::class,
        UsuarioEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun oportunidadDao(): OportunidadDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "oportunidades_db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true) // Recrea tablas sin crashear si cambian los campos
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}