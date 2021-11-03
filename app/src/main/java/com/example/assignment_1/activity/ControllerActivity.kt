package com.example.assignment_1.activity

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment_1.R
import com.example.assignment_1.adapter.RecyclerAdapter
import com.example.assignment_1.database.ExpenseTrackerDB
import com.example.assignment_1.model.ExpenseItem
import com.example.assignment_1.model.SheetItem
import com.example.assignment_1.model.StatItem
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.collections.ArrayList

class ControllerActivity : AppCompatActivity() {
    private lateinit var _recyclerview: RecyclerView
    private var _rl_arraylist: ArrayList<ExpenseItem> = ArrayList<ExpenseItem>()
    private var _add_expenses_button: Button? = null
    private var recycler_adapter: RecyclerAdapter? = null
    private var _income: EditText? = null
    private var _incomeView: TextView? = null
    private var _surplus_deficit_value: TextView? = null
    private var _surplus_deficit_text: TextView? = null
    private var totalRegularValue: TextView? = null
    private var totalIrregularValue: TextView? = null
    private var dateValue: EditText? = null
    private var retrieveSheet: SheetItem? = null
    private var _total_expense = 0
    private var _total_regular_expense = 0
    private var _total_irregular_expense = 0
    private var _db: ExpenseTrackerDB? = null
    private lateinit var db: SQLiteDatabase
    lateinit var sheetItem: SheetItem


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controller)

        sheetItem = intent.extras?.getParcelable("sheetItem")!!
        retrieveSheet = sheetItem

        _db = ExpenseTrackerDB(this)
        db = _db!!.readableDatabase

        _recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        _add_expenses_button = findViewById<Button>(R.id.add_expenses)
        _incomeView = findViewById<TextView>(R.id.incomeValue)
        _surplus_deficit_value = findViewById<TextView>(R.id.surplus_deficit_value)
        _surplus_deficit_text = findViewById<TextView>(R.id.surplus_deficit)
        totalRegularValue = findViewById<TextView>(R.id.regular_value)
        totalIrregularValue = findViewById<TextView>(R.id.irregular_value)


        expenseData()
        _recyclerview.layoutManager = LinearLayoutManager(this)
        recycler_adapter = RecyclerAdapter(this, _rl_arraylist, object: RecyclerAdapter.ClickListener{
            override fun onDelete(item: ExpenseItem) {
                updateTotalExpense(retrieveSheet()!!._total_expense-item._expenses_amount.toInt())
                if(item._status.equals("Regular")){
                    updateRegular(retrieveSheet()!!._regular - item._expenses_amount.toInt())
                }
                else{
                    updateIrregular(retrieveSheet()!!._irregular - item._expenses_amount.toInt())
                }

                setStatusValue()
                setSurplusValue()
            }

            override fun onUpdate(item1: StatItem?, item2: ExpenseItem) {
                if(item1!!._status.equals("Regular") && item1!!._prevStatus.equals("Regular")){
                    updateRegular(retrieveSheet()!!._regular - item1._prevExpense + item1.regular)
                    updateTotalExpense(retrieveSheet()!!._total_expense - item1._prevExpense + item1.regular)
                }
                else if(item1!!._status.equals("Regular") && !item1!!._prevStatus.equals("Regular")){
                    updateIrregular(retrieveSheet()!!._irregular - item1._prevExpense)
                    updateRegular(retrieveSheet()!!._regular + item1.regular)
                    updateTotalExpense(retrieveSheet()!!._total_expense - item1._prevExpense + item1.regular)
                }
                else if(!item1!!._status.equals("Regular") && item1!!._prevStatus.equals("Regular")){
                    updateRegular(retrieveSheet()!!._regular - item1._prevExpense)
                    updateIrregular(retrieveSheet()!!._irregular+item1.irregular)
                    updateTotalExpense(retrieveSheet()!!._total_expense - item1._prevExpense + item1.irregular)
                }
                else if(!item1!!._status.equals("Regular") && !item1!!._prevStatus.equals("Regular")){
                    updateIrregular(retrieveSheet()!!._irregular - item1._prevExpense + item1.irregular)
                    updateTotalExpense(retrieveSheet()!!._total_expense - item1._prevExpense + item1.irregular)
                }

                updateDataExpense(item2)
                setStatusValue()
                setSurplusValue()
            }
        })
        _recyclerview.adapter = recycler_adapter


        //add a listener to the input button that will trigger an input dialog
        _add_expenses_button?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                //trigger the alert dialog
                expenseDialog()
            }
        })

        _incomeView?.setText(sheetItem._income.toString())
        setStatusValue()
        setSurplusValue()

        Snackbar.make(_recyclerview, sheetItem.id.toString(), Snackbar.LENGTH_LONG).show()

    }

    //private function that will retrieve the data from our database using a query and set it on the textview
    private fun retrieveData(): ArrayList<ExpenseItem> {
        //name of the table we are goign to query
        val table_name: String = "expense"
        val columns: Array<String> = arrayOf("ID", "EXPENSE_NAME", "AMOUNT", "FK_KEY", "DATE","STATUS")
        val where: String? = null
        val where_args: Array<String>? = null
        val group_by: String? = null
        val having: String? = null
        val order_by: String? = null
        var c: Cursor = db.query(table_name, columns, where, where_args, group_by, having, order_by)
        var myList: ArrayList<ExpenseItem> = ArrayList()
        if (c.moveToFirst()) {
            do {
                var id: Int = c.getInt(0)
                var expenseName: String = c.getString(1)
                var expenseAmount: Int = c.getInt(2)
                var expenseFK: Int = c.getInt(3)
                var expenseDate: String = c.getString(4)
                var status: String = c.getString(5)
                myList.add(ExpenseItem(id, expenseName, expenseAmount.toString(), expenseFK,expenseDate, status))
            } while (c.moveToNext())
        }
        return myList
    }

    private fun expenseData() {
        for (expense in retrieveData()) {
                if (expense._fk_id == sheetItem.id && expense._status.equals("Regular")) {
                _total_expense += expense._expenses_amount.toInt()
                _total_regular_expense += expense._expenses_amount.toInt()

                _rl_arraylist.add(
                    ExpenseItem(
                        expense._id,
                        expense._expenses_text,
                        expense._expenses_amount,
                        expense._fk_id,
                        expense._date,
                        expense._status
                    )
                )
                recycler_adapter?.notifyDataSetChanged()
            }
            else if (expense._fk_id == sheetItem.id && expense._status.equals("Irregular")){
                _total_expense += expense._expenses_amount.toInt()
                 _total_irregular_expense += expense._expenses_amount.toInt()
                _rl_arraylist.add(
                    ExpenseItem(
                        expense._id,
                        expense._expenses_text,
                        expense._expenses_amount,
                        expense._fk_id,
                        expense._date,
                        expense._status
                    )
                )
                recycler_adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun expenseDialog() {
        var dialog = Dialog(this)
        dialog.setContentView(R.layout.expense_item)
        var layoutParam = WindowManager.LayoutParams()
        layoutParam.copyFrom(dialog.window?.attributes)
        layoutParam.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParam.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes = layoutParam
        var expenseName = dialog.findViewById<EditText>(R.id.expenseNameValue)
        var expenseIncome = dialog.findViewById<EditText>(R.id.amtValue)
        var radioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroup)
        var regular = dialog.findViewById<RadioButton>(R.id.rb1)
        var irregular = dialog.findViewById<RadioButton>(R.id.rb2)
        dataPopUpSelectUp(dialog)
        var send = dialog.findViewById<Button>(R.id.sendExp)
        var cancel = dialog.findViewById<Button>(R.id.cancelExp)


        send.setOnClickListener {
            var name = expenseName.text.toString()
            var amount = expenseIncome.text.toString()
            var date = dateValue?.text.toString()

            var id = radioGroup.checkedRadioButtonId
            var status : String? = null
            if(id == R.id.rb1){
                status = regular.text.toString()
                updateRegular(retrieveSheet()!!._regular + amount.toInt())
            }
            else{
               status = irregular.text.toString()
                updateIrregular(retrieveSheet()!!._irregular + amount.toInt())
            }

             var exp = ExpenseItem(null, name, amount, sheetItem.id, date, status)
            updateTotalExpense(retrieveSheet()!!._total_expense + amount.toInt())

            var expenseId: Long  = addData(exp)
            exp._id = expenseId.toInt()

            _rl_arraylist.add(exp)
            recycler_adapter?.notifyDataSetChanged()
            snackShow("Expenses Added")
            setStatusValue()
            setSurplusValue()
            dialog.dismiss()
        }
        cancel.setOnClickListener {
            snackShow("You have canceled")
            dialog.dismiss()

        }

        dialog.show()

    }

    private fun dataPopUpSelectUp(dialog: Dialog) {
        var calender: Calendar = Calendar.getInstance()
        var day = calender[Calendar.DAY_OF_MONTH]
        var month = calender[Calendar.MONTH]
        var year = calender[Calendar.YEAR]
        dateValue = dialog.findViewById<EditText>(R.id.calender)
        dateValue?.inputType = InputType.TYPE_NULL
        dateValue?.setOnClickListener {
            var dpd = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                    //set to textView
                    dateValue!!.setText("" + mDay + "/" + mMonth + "/" + mYear)
                },
                year,
                month,
                day
            )
            dpd.show()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_expense, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.newIncome -> {
                var builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setTitle("Enter the monthly Income")
                _income = EditText(this)
                _income!!.inputType = InputType.TYPE_CLASS_NUMBER
                _income!!.hint = "Please your monthly income"
                builder.setView(_income)

                builder.setPositiveButton(
                    "Accept"
                ) { p0, p1 ->
                    sheetItem._income = _income!!.text.toString().toInt()
                    if (addIncome(sheetItem._income)) {
                        _incomeView?.setText(_income!!.text.toString())

                    } else {
                        snackShow("Error")
                    }

                    setSurplusValue()
                }

                builder.setNegativeButton("Cancel") { p0, p1 ->

                }

                // build the dialog and show it
                var dialog: AlertDialog = builder.create()
                dialog.show()
                return true
            }
//
//            R.id.editIncome -> {
//                return true
//            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun addData(expenseItem: ExpenseItem): Long {
        val row1: ContentValues = ContentValues().apply {
            put("EXPENSE_NAME", expenseItem._expenses_text)
            put("AMOUNT", expenseItem._expenses_amount)
            put("FK_KEY", sheetItem.id)
            put("DATE", expenseItem._date)
            put("STATUS", expenseItem._status)
        }
        var count = db.insert("expense", null, row1)

        return count
    }


    private fun addIncome(income: Int): Boolean {
        val contentValues = ContentValues()
        contentValues.put("INCOME", income)
        db.update("sheet", contentValues, "ID = ?", arrayOf(sheetItem.id.toString()))
        return true
    }

    private fun updateRegular(regular: Int){
        val contentValues = ContentValues()
        contentValues.put("REGULAR", regular)
        db.update("sheet", contentValues, "ID = ?", arrayOf(sheetItem.id.toString()))
        //return true
    }

    private fun updateIrregular(irregular: Int){
        val contentValues = ContentValues()
        contentValues.put("IRREGULAR", irregular)
        db.update("sheet", contentValues, "ID = ?", arrayOf(sheetItem.id.toString()))
        //return true
    }

    private fun updateTotalExpense(expense: Int){
        val contentValues = ContentValues()
        contentValues.put("TOTAL_EXPENSE", expense)
        db.update("sheet", contentValues, "ID = ?", arrayOf(sheetItem.id.toString()))
        //return true
    }

    fun updateDataExpense(expenseItem: ExpenseItem): Int {
        val row: ContentValues = ContentValues().apply {
            put("EXPENSE_NAME", expenseItem._expenses_text)
            put("AMOUNT", expenseItem._expenses_amount)
            put("DATE", expenseItem._date)
            put("STATUS", expenseItem._status)
        }


        var where: String = "id=?"
        var whereArgs: String = expenseItem._id.toString()

        return db.update("expense", row, where, arrayOf(whereArgs))
    }


    private fun retrieveSheet(): SheetItem?{

        val table_name: String = "sheet"
        val columns: Array<String> = arrayOf("ID", "INCOME", "PERIOD", "REGULAR", "IRREGULAR","TOTAL_EXPENSE")
        val where_args: Array<String>? = null
        var where: String = "id=?"
        val group_by: String? = null
        val having: String? = null
        val order_by: String? = null
        var c: Cursor = db.query(table_name, columns, where, arrayOf(sheetItem.id.toString()), group_by, having, order_by)
        var myList: ArrayList<ExpenseItem> = ArrayList()
        if (c.moveToFirst()) {
            do {
                var id: Int = c.getInt(0)
                var income: Int = c.getInt(1)
                var period: String = c.getString(2)
                var regular: Int = c.getInt(3)
                var irregular: Int = c.getInt(4)
                var total_expense: Int = c.getInt(5)
                 retrieveSheet = SheetItem(id, period, income, regular, irregular, total_expense)
            } while (c.moveToNext())
        }
        return retrieveSheet
    }

    fun surplusValue(): Int {
        var temp = retrieveSheet()!!._income - retrieveSheet()!!._total_expense
        return temp
    }


     fun setSurplusValue() {
        if (surplusValue() >= 1) {
            _surplus_deficit_text?.setText("Surplus")
            _surplus_deficit_value?.setText(surplusValue().toString())
        } else {
            _surplus_deficit_text?.setText("Deficit")
            _surplus_deficit_value?.setText(surplusValue().toString())
        }
    }


    private fun snackShow(message: String) {
        Snackbar.make(_recyclerview, message, Snackbar.LENGTH_LONG).show()
    }

     private fun setStatusValue(){
        totalRegularValue?.setText(retrieveSheet()!!._regular.toString())
        totalIrregularValue?.setText(retrieveSheet()!!._irregular.toString())
    }


}


