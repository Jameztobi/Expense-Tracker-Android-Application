package com.example.assignment_1

import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    //private fields of the class
    private lateinit var _expenseDb: ExpenseTrackerDB
    private lateinit var _sdb: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }





    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.newSheet -> {
                var builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setTitle("Please enter a name for the new sheet")
                var _newsheet_name : EditText = EditText(this)
                _newsheet_name!!.inputType = InputType.TYPE_CLASS_TEXT
                _newsheet_name!!.hint = "Please enter sheet name"
                builder.setView(_newsheet_name)

                builder.setPositiveButton(
                    "Accept"
                ) { p0, p1 ->


                }

                builder.setNegativeButton("Cancel") { p0, p1 ->

                }

                // build the dialog and show it
                var dialog: AlertDialog = builder.create()
                dialog.show()
                return true
            }

            R.id.first -> {
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

}