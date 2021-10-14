package com.example.assignment_1
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ExpenseTrackerDB(context: Context, name: String, factory
: SQLiteDatabase.CursorFactory?, version: Int): SQLiteOpenHelper(context, name, factory, version) {
    //private constant for creating our single table
    private val CREATE_TABLE_SHEET: String = "create table sheet("+
            "ID integer primary key autoincrement, " +
            "NEW_SHEET_NAME string, " +
            "PERIOD string "+
            ")"

    //private constant for creating our single table
    private val CREATE_TABLE_EXPENSE: String = "create table expense("+
            "ID integer primary key autoincrement, " +
            "EXPENSE_NAME string, " +
            "AMOUNT integer" +
            "DATE string, "+
            "REGULAR boolean, "+
            "FOREIGN KEY integer "+
            ")"

    //private constant for dropping our single table
    private val DROP_TABLE_SHEET: String = "drop table sheet"
    private val DROP_TABLE_EXPENSE: String = "drop table expense"


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


}