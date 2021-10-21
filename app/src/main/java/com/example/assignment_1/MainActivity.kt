package com.example.assignment_1


import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment_1.activity.ControllerActivity
import com.example.assignment_1.adapter.SheetAdapter
import com.example.assignment_1.database.ExpenseTrackerDB
import com.example.assignment_1.model.SheetItem
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    //private fields of the class
    private lateinit var _expenseDb: ExpenseTrackerDB
    private lateinit var _sdb: SQLiteDatabase
    private lateinit var _sheetName: TextView
    private lateinit var _period: TextView
    private var list_sheetName : ArrayList<SheetItem> = ArrayList<SheetItem>()
    private var sheetAdapter: SheetAdapter?= null
    private lateinit var _recyclerview: RecyclerView
    private var _linear_layout: LinearLayout? = null
    private var _id: Int? = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //open a writable connection to the database
        _expenseDb = ExpenseTrackerDB(this)
        Log.i("error", _expenseDb.writableDatabase.toString())
        _sdb = _expenseDb.writableDatabase
        _recyclerview = findViewById<RecyclerView>(R.id.recyclerview_main)
        try {
            retrieveData()
        }
        catch(e: Exception){
            Log.e("MYERROR", e.toString())
        }
        _recyclerview.layoutManager = LinearLayoutManager(this)
        sheetAdapter = SheetAdapter(this, list_sheetName) {
           var startControllerIntent = Intent(this, ControllerActivity::class.java)
            startControllerIntent.putExtra("sheetItem", it)

            startActivity(startControllerIntent)
        }
        _recyclerview.adapter = sheetAdapter




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
                _newsheet_name.inputType = InputType.TYPE_CLASS_TEXT
                _newsheet_name.hint = "Please enter sheet name"

                builder.setView(_newsheet_name)

                builder.setPositiveButton(
                    "Accept"
                ) { p0, p1 ->
                    addData(_newsheet_name.text.toString(), "Jan-2021")
                    list_sheetName.clear()
                    retrieveData()
                    }

                builder.setNegativeButton("Cancel") { p0, p1 ->
                    var snackbar: Snackbar = Snackbar.make(_linear_layout!!, "You need to pick",
                    Snackbar.LENGTH_LONG)
                    snackbar.show()
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

    //private function that will retrieve the data from our database using a query and set it on the textview
    fun retrieveData(){
        //name of the table we are goign to query
        val table_name: String = "sheet"
        val columns: Array<String> = arrayOf("ID", "NEW_SHEET_NAME", "PERIOD", "INCOME")
        val where: String? = null
        val where_args: Array<String>? = null
        val group_by: String? = null
        val having: String? = null
        val order_by: String? = null
        var c: Cursor = _sdb.query(table_name, columns, where, where_args, group_by, having, order_by)
       // var _list_sheetName: java.util.ArrayList<SheetItem>? = null

        if(c.moveToFirst()){
            do {
                _id = c.getInt(0)
                var newSheetName: String = c.getString(1)
                var period_value: String = c.getString(2)
                var income_value: String = c.getInt(3).toString()

                list_sheetName?.add(SheetItem(_id!!, newSheetName, period_value, income_value.toInt()))
                sheetAdapter?.notifyDataSetChanged()

            }while (c.moveToNext())
        }

    }




    //private function that will add some data into our database
    fun addData(name: String, period: String){
        val row: ContentValues = ContentValues().apply {
            put("NEW_SHEET_NAME", name)
            put("PERIOD", period)
            put("INCOME", 0)
        }
        _sdb.insert("sheet", null, row)
    }

}