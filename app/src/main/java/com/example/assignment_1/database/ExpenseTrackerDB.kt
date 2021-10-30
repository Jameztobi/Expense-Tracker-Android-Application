package com.example.assignment_1.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.assignment_1.model.ExpenseItem
import com.example.assignment_1.model.SheetItem
import com.example.assignment_1.model.StatItem

class ExpenseTrackerDB(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 13
        private const val DATABASE_NAME = "myDB"
        private const val TABLE_SHEET = "sheet"
        private const val TABLE_EXPENSE = "expense"
        private const val TABLE_STAT = "stat"
    }
    //open a writable connection to the database

    //private constant for creating our single table
    private val CREATE_TABLE_SHEET: String = "create table " + TABLE_SHEET + " (" +
            "ID integer primary key autoincrement, " +
            "INCOME integer, " +
            "PERIOD string, " +
            "REGULAR integer, " +
            "IRREGULAR integer, " +
            "TOTAL_EXPENSE integer " +
            ")"

    //private constant for creating our single table
    private val CREATE_TABLE_EXPENSE: String = "create table  " + TABLE_EXPENSE + "(" +
            "ID integer primary key autoincrement, " +
            "EXPENSE_NAME string, " +
            "AMOUNT integer, " +
            "STATUS string, " +
            "DATE string, " +
            "FK_KEY integer, " +
            "CONSTRAINT FK_KEY " +
            "FOREIGN KEY(ID) " +
            "REFERENCES " + TABLE_SHEET + "(ID)" +
            ")"


    //private constant for dropping our single table
    private val DROP_TABLE_SHEET: String = "drop table $TABLE_SHEET"
    private val DROP_TABLE_EXPENSE: String = "drop table $TABLE_EXPENSE"
    //private val DROP_TABLE_STAT: String = "drop table $TABLE_STAT"


    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(CREATE_TABLE_SHEET)
        p0?.execSQL(CREATE_TABLE_EXPENSE)
        //p0?.execSQL(CREATE_TABLE_STATS)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL(DROP_TABLE_SHEET)
        p0?.execSQL(DROP_TABLE_EXPENSE)
       // p0?.execSQL(DROP_TABLE_STAT)


        p0?.execSQL(CREATE_TABLE_SHEET)
        p0?.execSQL(CREATE_TABLE_EXPENSE)
      //  p0?.execSQL(CREATE_TABLE_STATS)
    }

    fun deleteRow(id: String): Int {
        var db: SQLiteDatabase = writableDatabase
        var where: String = "id=?"
        var whereArgs: String = id

        return db.delete("sheet", where, arrayOf(whereArgs))
    }


    fun deleteExpenseRow(id: String): Int {
        var db: SQLiteDatabase = writableDatabase
        var where: String = "id=?"
        var whereArgs: String = id

        return db.delete(TABLE_EXPENSE, where, arrayOf(whereArgs))
    }

    fun updateData(id: Int,  period: String): Int {
        var db: SQLiteDatabase = writableDatabase
        val row: ContentValues = ContentValues().apply {
            put("PERIOD", period)
        }

        var table = TABLE_SHEET
        var where: String = "id=?"
        var whereArgs: String = id.toString()

        return db.update(table, row, where, arrayOf(whereArgs))
    }




}