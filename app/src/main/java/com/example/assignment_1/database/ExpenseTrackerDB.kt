package com.example.assignment_1.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.assignment_1.model.SheetItem

class ExpenseTrackerDB(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 4
        private const val DATABASE_NAME = "myDB"
        private const val TABLE_SHEET = "sheet"
        private const val TABLE_EXPENSE = "expense"
    }
    //open a writable connection to the database

    //private constant for creating our single table
    private val CREATE_TABLE_SHEET: String = "create table " + TABLE_SHEET + " (" +
            "ID integer primary key autoincrement, " +
            "NEW_SHEET_NAME string, " +
            "DATE string, " +
            "INCOME integer, " +
            "PERIOD string " +
            ")"

    //private constant for creating our single table
    private val CREATE_TABLE_EXPENSE: String = "create table  " + TABLE_EXPENSE + "(" +
            "ID integer primary key autoincrement, " +
            "EXPENSE_NAME string, " +
            "AMOUNT integer, " +
            "REGULAR integer, " +
            "FK_KEY integer, " +
            "CONSTRAINT FK_KEY " +
            "FOREIGN KEY(ID) " +
            "REFERENCES " + TABLE_SHEET + "(ID)" +
            ")"

    //private constant for dropping our single table
    private val DROP_TABLE_SHEET: String = "drop table $TABLE_SHEET"
    private val DROP_TABLE_EXPENSE: String = "drop table $TABLE_EXPENSE"


    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(CREATE_TABLE_SHEET)
        p0?.execSQL(CREATE_TABLE_EXPENSE)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL(DROP_TABLE_SHEET)
        p0?.execSQL(DROP_TABLE_EXPENSE)

        p0?.execSQL(CREATE_TABLE_SHEET)
        p0?.execSQL(CREATE_TABLE_EXPENSE)
    }

    fun deleteRow(id: String): Int {
        var db: SQLiteDatabase = writableDatabase
        var where: String = "id=?"
        var whereArgs: String = id

        return db.delete("sheet", where, arrayOf(whereArgs))
    }

    fun updateData(id: Int, sheetName: String, period: String): Int {
        var db: SQLiteDatabase = writableDatabase
        val row: ContentValues = ContentValues().apply {
            put("NEW_SHEET_NAME", sheetName)
            put("PERIOD", period)
        }

        var table = TABLE_SHEET
        var where: String = "id=?"
        var whereArgs: String = id.toString()

        return db.update(table, row, where, arrayOf(whereArgs))
    }

}