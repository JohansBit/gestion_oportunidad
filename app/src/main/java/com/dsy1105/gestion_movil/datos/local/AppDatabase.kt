package com.dsy1105.gestion_movil.datos.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [OportunidadEntity::class], version = 1, exportSchema = false)
abstract class AppDataBase : RoomDatabase(){
    abstract fun oportunidadDao(): OportunidadDao

    companion object{
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase{
            return INSTANCE ?: synchronized((this)){
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "oportunidades_db"
                ).build().also { INSTANCE = it }
            }
        }

    }
}