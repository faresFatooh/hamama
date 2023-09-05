package com.p1.uberfares.SQL

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "my_database.db"
        private const val DATABASE_VERSION = 3
        private const val TABLE_NAME = "my_table"
        private const val COLUMN_ID = "id"
        private const val COLUMN_LAT = "lat"
        private const val COLUMN_LNG = "lng"
    }

    private val CREATE_TABLE_QUERY = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_LAT REAL, $COLUMN_LNG REAL);"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_QUERY)
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Perform upgrade operations if needed
    }

    fun insertData(latitude: Double, longitude: Double) {
        val db = writableDatabase
        val insertQuery = "INSERT INTO $TABLE_NAME ($COLUMN_LAT, $COLUMN_LNG) VALUES ('$latitude', '$longitude')"
        db.execSQL(insertQuery)
        //db.close()
    }


    fun getData(): List<Double> {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)
        val dataList = mutableListOf<Double>()

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val name = cursor.getDouble(cursor.getColumnIndex(COLUMN_LAT))
                dataList.add(name)
            } while (cursor.moveToNext())
            cursor.close()
        }

       // db.close()
        return dataList
    }
    fun getData2(): List<Double> {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)
        val dataList = mutableListOf<Double>()

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val name = cursor.getDouble(cursor.getColumnIndex(COLUMN_LNG))
                dataList.add(name)
            } while (cursor.moveToNext())
            cursor.close()
        }

       // db.close()
        return dataList
    }

    fun deleteAllData() {
        val db = writableDatabase
        db.delete(TABLE_NAME, null, null)
        // db.close()
    }
}


