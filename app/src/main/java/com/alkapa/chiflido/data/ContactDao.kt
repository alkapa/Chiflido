package com.alkapa.chiflido.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM watched_contacts ORDER BY name COLLATE NOCASE ASC")
    fun getAll(): Flow<List<WatchedContact>>

    /**
     * Snapshot caliente que el NotificationListener consulta en cada
     * `onNotificationPosted`. La frecuencia es baja, así que está bien
     * leerla cada vez en lugar de cachearla en memoria.
     */
    @Query("SELECT * FROM watched_contacts WHERE enabled = 1")
    suspend fun getEnabled(): List<WatchedContact>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: WatchedContact): Long

    @Update
    suspend fun update(contact: WatchedContact)

    @Delete
    suspend fun delete(contact: WatchedContact)
}
