package com.receparslan.finance.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.receparslan.finance.model.Cryptocurrency

@Dao
interface CryptocurrencyDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCryptocurrency(cryptocurrency: Cryptocurrency)

    @Delete
    suspend fun deleteCryptocurrency(cryptocurrency: Cryptocurrency)

    @Query("SELECT * from cryptocurrency ORDER BY name ASC")
    fun getAllCryptocurrencies(): List<Cryptocurrency>
}