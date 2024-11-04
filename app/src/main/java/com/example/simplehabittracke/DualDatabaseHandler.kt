package com.example.simplehabittracke

import android.content.ClipData
import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Color
import android.util.Log
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.*
import java.text.SimpleDateFormat


class DualDatabaseHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val habitDataList = mutableListOf<HabitData>()
    private val habitCheckList = mutableListOf<HabitCheck>()

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    companion object {

        // All Static variables
        // Database Version
        private val DATABASE_VERSION = 1

        // Database Name
        private val DATABASE_NAME = "myDb"

        private val numOfTables = 2

        private val TABLES = arrayOf("HabitData", "HabitCheck")

        private val COL_FIELDS1 = arrayOf("id", "name", "color")

        private val COL_FIELDS2 = arrayOf("id", "date", "checked", "habit_id","FOREIGN KEY (habit_id)")

        private val COL_FIELDS_LIST = arrayOf(COL_FIELDS1, COL_FIELDS2)

        private val COL_KEYS1 = arrayOf("INTEGER PRIMARY KEY AUTOINCREMENT", "STRING", "INT")

        private val COL_KEYS2 = arrayOf("INTEGER PRIMARY KEY AUTOINCREMENT", "STRING", "BOOLEAN",
            "INTEGER", "REFERENCES HabitData (id)")

        private val COL_KEYS_LIST = arrayOf(COL_KEYS1, COL_KEYS2)
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("d_Tag", "SQL code is starting to write")

        for (i in 0 until numOfTables) {
            var CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLES[i] + "("

            for (j in 0 until COL_FIELDS_LIST[i].size) {
                CREATE_TABLE += COL_FIELDS_LIST[i][j] + " " + COL_KEYS_LIST[i][j]
                if (j != COL_FIELDS_LIST[i].size - 1)
                    CREATE_TABLE += ", "

            }
            CREATE_TABLE += ")"

            Log.d("d_Tag", "SQL code= $CREATE_TABLE")

            db.execSQL(CREATE_TABLE)
        }

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop older table if existed
        for (tableName in TABLES)
            db.execSQL("DROP TABLE IF EXISTS $tableName")

        // Create tables again
        onCreate(db)
    }


    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    internal fun dropAllTable(context: Context){
        context.deleteDatabase(DATABASE_NAME)
        onCreate(this.writableDatabase)
//        val db = this.writableDatabase
//
//        val c: Cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'",null)
//        val ts = mutableListOf<String>()
//
//        while (c.moveToNext())
//            ts.add(c.getString(0))
//
//        for (tableName in TABLES)
//            db.execSQL("DROP TABLE IF EXISTS $tableName")

    }

    internal fun generateDefaultValue() {
        val habit1 = HabitData(1, "drink six glass of H2O", Color.YELLOW)
        val habit2 = HabitData(2, "eat a fruit", Color.GREEN)

        addHabitData(habit1)
        addHabitData(habit2)

        Log.d("d_tag", "Successfully add habit1 and habit2. ")

        val check1 = HabitCheck(1, LocalDate.now(), true, 1)
        val check2 = HabitCheck(2, LocalDate.now(), false, 2)

        addHabitCheck(check1)
        addHabitCheck(check2)

        Log.d("d_tag", "Successfully add check1 and check2. ")
    }

    // add single
    private fun addHabitData(d: HabitData) {
        val tableId: Int = 0

        val db = this.writableDatabase

        val values = ContentValues()
//        values.put(COL_FIELDS_LIST[tableId][0], d.id)
        values.put(COL_FIELDS_LIST[tableId][1], d.name)
        values.put(COL_FIELDS_LIST[tableId][2], d.color)
        // Inserting Row
        db.insert(TABLES[tableId], null, values)
        db.close() // Closing database connection
    }

    internal fun addHabitCheck(d: HabitCheck) {
        val tableId: Int = 1

        val db = this.writableDatabase

        val values = ContentValues()
//        values.put(COL_FIELDS_LIST[tableId][0], d.id)
        values.put(COL_FIELDS_LIST[tableId][1], formatDate(d.date))
        values.put(COL_FIELDS_LIST[tableId][2], d.checked)
        values.put(COL_FIELDS_LIST[tableId][3], d.ref_key)
        // Inserting Row
        db.insert(TABLES[tableId], null, values)
        db.close() // Closing database connection
    }

    // update single
    internal fun updateHabitCheck(id: Int, c: Boolean) {
        val tableId: Int = 1

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_FIELDS_LIST[tableId][2], c)

        db.update(TABLES[tableId], values, "id=?", arrayOf(id.toString()))
    }

    // get join table by Date
    internal fun getJoinSingleByDate(d: LocalDate){
        val db = this.writableDatabase



        db.close()
    }

    // misc
    private fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat("yyyy-mm-dd")
        return formatter.format(date)
    }

    private fun formatDate(date: LocalDate):String = date.format(formatter)

    private fun formatDateOutDb(date: String):LocalDate = LocalDate.parse(date, formatter)

    // Get single
    internal fun getHabitCheck(id:Int):HabitCheck{
        val tableId:Int = 1
        val tableName = TABLES[tableId]
        lateinit var item:HabitCheck

        try {
            val db = this.writableDatabase

            val selectQuery = "SELECT  * FROM $tableName WHERE id = $id"
            val cursor = db.rawQuery(selectQuery, null)

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    item = HabitCheck(
                        cursor.getInt(0),
                        formatDateOutDb(cursor.getString(1)),
                        cursor.getInt(2) > 0,
                        cursor.getInt(3)
                    )
                } while (cursor.moveToNext())
            }

            cursor.close()
            db.close()
        }  catch (e: Exception) {
            // TODO: handle exception
            Log.e("e_tag", "DualDatabaseHandler exception: $e")
        }
        return item
    }

    // not yet well dev
    internal fun getJoin():MutableList<*> {
        val list = mutableListOf<Map<String, Int>>()

        try {
            val db = this.writableDatabase

            val selectQuery = "SELECT l.id, l.color, r.checked FROM ${TABLES[0]} l INNER JOIN ${TABLES[1]} r " +
                    "ON l.id = r.habit_id "
            val cursor = db.rawQuery(selectQuery, null)

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    val item = mapOf(
                        "id" to cursor.getInt(0),
                        "color" to cursor.getInt(1),
                        "checked" to cursor.getInt(2)
                    )
                    list.add(item)
                } while (cursor.moveToNext())
            }

            cursor.close()
            db.close()
        }  catch (e: Exception) {
            // TODO: handle exception
            Log.e("e_tag", "DualDatabaseHandler exception: $e")
        }
        return list
    }

    // get all for specific date
    internal fun getHabitCheck(date: LocalDate):List<HabitCheck> = getHabitCheck(formatDate(date))

    private fun getHabitCheck(date: String):List<HabitCheck>{
        val tableId:Int = 1
        val tableName = TABLES[tableId]
        val list = mutableListOf<HabitCheck>()

        try {
            val db = this.writableDatabase

            val selectQuery = "SELECT * FROM $tableName WHERE date = '$date'"
            val cursor = db.rawQuery(selectQuery, null)

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    val item = HabitCheck(
                        cursor.getInt(0),
                        formatDateOutDb(cursor.getString(1)),
                        cursor.getInt(2) > 0,
                        cursor.getInt(3)
                    )
                    list.add(item)
                } while (cursor.moveToNext())
            }

            cursor.close()
            db.close()
        }  catch (e: Exception) {
            // TODO: handle exception
            Log.e("e_tag", "DualDatabaseHandler exception: $e")
        }
        return list
    }

    // Getting all data
    internal fun getAllHabitData(): List<HabitData> {
        val tableName = TABLES[0]

        try {
            habitDataList.clear()

            // Select All Query
            val selectQuery = "SELECT  * FROM $tableName"

            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    val contact = HabitData(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2)
                    )
                    // Adding contact to list
                    habitDataList.add(contact)
                } while (cursor.moveToNext())
            }

            // return contact list
            cursor.close()
            db.close()
            return habitDataList
        } catch (e: Exception) {
            // TODO: handle exception
            Log.e("e_tag", "DualDatabaseHandler exception: $e")
        }

        return habitDataList
    }

    internal fun getAllHabitCheck(): List<HabitCheck> {
        val tableName = TABLES[1]

        try {
            habitCheckList.clear()

            // Select All Query
            val selectQuery = "SELECT  * FROM $tableName"

            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    val contact = HabitCheck(
                        cursor.getInt(0),
                        formatDateOutDb(cursor.getString(1)),
                        cursor.getInt(2) > 0,
                        cursor.getInt(3)
                    )
                    // Adding contact to list
                    habitCheckList.add(contact)
                } while (cursor.moveToNext())
            }

            // return contact list
            cursor.close()
            db.close()
            return habitCheckList
        } catch (e: Exception) {
            // TODO: handle exception
            Log.e("e_tag", "DualDatabaseHandler exception: $e")
        }

        return habitCheckList
    }

    // Deleting single
    fun deleteHabitData(id: Int) {
        val tableId = 0
        val tableName = TABLES[tableId]

        val db = this.writableDatabase
        db.delete(
            tableName, "${COL_KEYS_LIST[tableId][0]} = ?",
            arrayOf(id.toString())
        )
        db.close()
    }
}