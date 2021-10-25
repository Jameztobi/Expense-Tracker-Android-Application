package com.example.assignment_1


import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.res.Resources
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment_1.activity.ControllerActivity
import com.example.assignment_1.adapter.SheetAdapter
import com.example.assignment_1.database.ExpenseTrackerDB
import com.example.assignment_1.model.ExpenseItem
import com.example.assignment_1.model.SheetItem
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList
import java.text.SimpleDateFormat as SimpleDateFormat


class MainActivity : AppCompatActivity() {
    //private fields of the class
    private lateinit var _expenseDb: ExpenseTrackerDB
    private lateinit var _sdb: SQLiteDatabase
    private lateinit var _sheetName: TextView
    private lateinit var _period: TextView
    private var list_sheetName: ArrayList<SheetItem> = ArrayList<SheetItem>()
    private var sheetAdapter: SheetAdapter? = null
    private lateinit var _recyclerview: RecyclerView
    private var _linear_layout: LinearLayout? = null
    private var _id: Int? = 0
    private var _spinner: Spinner? = null
    private var _spinner_years: Spinner? = null
    private var _add_sheet_btn: Button? = null
    private var _cancel: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        _recyclerview = findViewById<RecyclerView>(R.id.recyclerview_main)

        _add_sheet_btn = findViewById<Button>(R.id.sendSheet)


        //open a writable connection to the database
        _expenseDb = ExpenseTrackerDB(this)
        _sdb = _expenseDb.writableDatabase

        retrieveData()
        _recyclerview.layoutManager = LinearLayoutManager(this)
        sheetAdapter = SheetAdapter(this, list_sheetName) {
            var startControllerIntent = Intent(this, ControllerActivity::class.java)
            startControllerIntent.putExtra("sheetItem", it)

            startActivity(startControllerIntent)
        }
        _recyclerview.adapter = sheetAdapter

        try {
            //add a listener to the input button that will trigger an input dialog
            _add_sheet_btn?.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    //trigger the alert dialog
                    createSheetDialog()
                }
            })
        } catch (e: Exception) {
            Log.i("dialoger", e.toString())
        }


    }


    //private function that will retrieve the data from our database using a query and set it on the textview
    fun retrieveData() {
        //name of the table we are goign to query
        val table_name: String = "sheet"
        val columns: Array<String> = arrayOf("ID", "NEW_SHEET_NAME", "PERIOD", "INCOME")
        val where: String? = null
        val where_args: Array<String>? = null
        val group_by: String? = null
        val having: String? = null
        val order_by: String? = null
        var c: Cursor =
            _sdb.query(table_name, columns, where, where_args, group_by, having, order_by)
        // var _list_sheetName: java.util.ArrayList<SheetItem>? = null

        if (c.moveToFirst()) {
            do {
                _id = c.getInt(0)
                var newSheetName: String = c.getString(1)
                var period_value: String = c.getString(2)
                var income_value: String = c.getInt(3).toString()

                list_sheetName?.add(
                    SheetItem(
                        _id!!,
                        newSheetName,
                        period_value,
                        income_value.toInt()
                    )
                )
                sheetAdapter?.notifyDataSetChanged()

            } while (c.moveToNext())
        }

    }


    //private function that will add some data into our database
    private fun addData(name: String, period: String) {
        val row: ContentValues = ContentValues().apply {
            put("NEW_SHEET_NAME", name)
            put("PERIOD", period)
            put("INCOME", 0)
        }
        _sdb.insert("sheet", null, row)
    }

    private fun getMonthDropDownList(): String? {
        var dataFormat: DateFormat = SimpleDateFormat("MM")
        var date: Date = Date()
        var currentMonth: String = dataFormat.format(date)
        var res: Resources = resources
        var monthList = res.getStringArray(R.array.month_string)
        var selectedMonth: String? = null


        var adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.month_string,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        _spinner?.adapter = adapter


        _spinner?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                p0?.setSelection(currentMonth.toInt() - 1)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        })

        return _spinner?.selectedItem.toString()

    }

    private fun getYearDropDownList(): String {
        var yearList: ArrayList<String> = ArrayList()
        var thisYear: Int = Calendar.getInstance().get(Calendar.YEAR)
        var selectedYear: String = ""
        var count: Int = 0
        for (i in 1960..thisYear) {
            count++
            yearList?.add(i.toString())

        }

        _spinner_years?.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            yearList
        )

        _spinner_years?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                p0?.setSelection(count - 1)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        })

        return _spinner_years?.selectedItem.toString()

    }

    private fun createSheetDialog() {
        var dialog = Dialog(this)
        dialog.setContentView(R.layout.addsheet_item)
        var sheet_name = dialog.findViewById<EditText>(R.id.sheet_new_name)
        _spinner = dialog.findViewById<Spinner>(R.id.month)
        _spinner_years = dialog.findViewById<Spinner>(R.id.years)
        var month = getMonthDropDownList()
        var year = getYearDropDownList()
        var period = month+" "+year
        var send = dialog.findViewById<Button>(R.id.sendSheet)
        _cancel = dialog.findViewById<Button>(R.id.cancelSheet)

        var layoutParam = WindowManager.LayoutParams()
        layoutParam.copyFrom(dialog.window?.attributes)
        layoutParam.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParam.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes = layoutParam


        send.setOnClickListener {
            if (TextUtils.isEmpty(sheet_name.text.toString())) {
                displayMessage("Sheet name can't be empty")
            }
            else{
                addData(sheet_name.text.toString(), period)
                list_sheetName.clear()
                retrieveData()
                displayMessage("New Sheet Created")
                dialog.dismiss()
            }

        }


        _cancel?.setOnClickListener {
            dialog.dismiss()

        }

        dialog.show()

    }

    private fun displayMessage(message: String) {
        Snackbar.make(_recyclerview, message, Snackbar.LENGTH_LONG).show()
    }

}