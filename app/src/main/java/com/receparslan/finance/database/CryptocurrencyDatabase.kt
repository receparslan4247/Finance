package com.receparslan.finance.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.receparslan.finance.model.Cryptocurrency

@Database(entities = [Cryptocurrency::class], version = 1, exportSchema = false)
abstract class CryptocurrencyDatabase : RoomDatabase() {
    abstract fun cryptocurrencyDao(): CryptocurrencyDao

    companion object {
        @Volatile
        private var Instance: CryptocurrencyDatabase? = null

        // Singleton pattern to ensure only one instance of the database is created
        fun getDatabase(context: Context): CryptocurrencyDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, CryptocurrencyDatabase::class.java, name = "cryptocurrency_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}