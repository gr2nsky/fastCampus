package com.greensky0526.calculator

import androidx.room.Database
import androidx.room.RoomDatabase
import com.greensky0526.calculator.dao.HistoryDao
import com.greensky0526.calculator.model.History

@Database(entities = [History::class], version = 1)
abstract class AppDatabase: RoomDatabase(){
    abstract fun historyDao(): HistoryDao
}