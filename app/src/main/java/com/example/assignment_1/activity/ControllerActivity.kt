package com.example.assignment_1.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.content.DialogInterface
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment_1.R
import com.example.assignment_1.adapter.RecyclerAdapter
import com.example.assignment_1.database.ExpenseTrackerDB
import com.example.assignment_1.model.ExpenseItem
import com.example.assignment_1.model.SheetItem
import com.google.android.material.snackbar.Snackbar

class ControllerActivity : AppCompatActivity() {
    private lateinit var _recyclerview: RecyclerView
    private var _rl_arraylist: ArrayList<ExpenseItem> = ArrayList<ExpenseItem>()
    private var _add_expenses_button: Button? = null
    private var _editExpense: EditText? = null
    private var _editIncome: EditText? = null
    private var recycler_adapter: RecyclerAdapter? = null
    private var _income: EditText? = null
    private var _incomeView: TextView? = null
    private var _surplus_deficit_value: TextView? = null
    private var _surplus_deficit_text: TextView? = null
    private var _total_expense = 0
    private var _db: ExpenseTrackerDB? = null
    private lateinit var db: SQLiteDatabase
    lateinit var sheetItem: SheetItem
    private var fK_id: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controller)

        sheetItem = intent.extras?.getParcelable("sheetItem")!!

        _db = ExpenseTrackerDB(this)
        db = _db!!.readableDatabase

        _recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        _add_expenses_button = findViewById<Button>(R.id.add_expenses)
        _incomeView = findViewById<TextView>(R.id.incomeValue)
        _surplus_deficit_value = findViewById<TextView>(R.id.surplus_deficit_value)
        _surplus_deficit_text = findViewById<TextView>(R.id.surplus_deficit)

        try{
            expenseData()
            _recyclerview.layoutManager = LinearLayoutManager(this)
            recycler_adapter = RecyclerAdapter(this, _rl_arraylist)
            _recyclerview.adapter = recycler_adapter
        }
        catch (e: java.lang.Exception){
            Log.i("try", e.toString())
        }

        //add a listener to the input button that will trigger an input dialog
        _add_expenses_button?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                //trigger the alert dialog
                expenseDialog()
            }
        })

        _incomeView?.setText(sheetItem._income.toString())

        setSurplusValue()

        Snackbar.make(_recyclerview, sheetItem.id.toString(), Snackbar.LENGTH_LONG).show()

    }


    //private function that will retrieve the data from our database using a query and set it on the textview
    private fun retrieveData(): ArrayList<ExpenseItem> {
        //name of the table we are goign to query
        val table_name: String = "expense"
        val columns: Array<String> = arrayOf("ID", "EXPENSE_NAME", "AMOUNT", "FK_KEY")
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
                myList.add(ExpenseItem(id, expenseName, expenseAmount.toString(), expenseFK))
            } while (c.moveToNext())
        }
        return myList
    }

    private fun expenseData(){
        for(expense in retrieveData()){
            if(expense._fk_id == sheetItem.id){
                _total_expense += expense._expenses_amount.toInt()
                _rl_arraylist.add(ExpenseItem(expense._id, expense._expenses_text, expense._expenses_amount, expense._fk_id))
                recycler_adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun expenseDialog() {


        var dialog= Dialog(this)
        dialog.setContentView(R.layout.expense_item)
        var expenseName = dialog.findViewById<EditText>(R.id.expenseNameValue)
        var expenseIncome = dialog.findViewById<EditText>(R.id.amtValue)
        var send = dialog.findViewById<Button>(R.id.sendExp)
        var cancel = dialog.findViewById<Button>(R.id.cancelExp)

        var layoutParam = WindowManager.LayoutParams()
        layoutParam.copyFrom(dialog.window?.attributes)
        layoutParam.width=WindowManager.LayoutParams.MATCH_PARENT
        layoutParam.height= WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes=layoutParam


        send.setOnClickListener{
                var name = expenseName.text.toString()
                var amount = expenseIncome.text.toString()
                var exp = ExpenseItem(null, name, amount, sheetItem.id)
                _total_expense += amount.toInt()
                addData(exp)
                _rl_arraylist.add(exp)
                recycler_adapter?.notifyDataSetChanged()
                snackShow("Expenses Added")
                setSurplusValue()
                dialog.dismiss()
            }


        cancel.setOnClickListener{object : View.OnClickListener{
            override fun onClick(p0: View?) {
                snackShow("You have canceled")
                dialog.dismiss()
            }
        }

        }


        dialog.show()


//        var builder: AlertDialog.Builder = AlertDialog.Builder(this)
//        builder.setTitle("Please enter a new expense")
//        _editExpense = EditText(this)
//        _editExpense!!.inputType = InputType.TYPE_CLASS_TEXT
//        _editExpense!!.hint = "Enter text here"
//        builder.setView(_editExpense)
//
//
//        builder.setPositiveButton("Accept") { p0, p1 ->
//            amountDialog()
//        }
//
//        builder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
//            override fun onClick(p0: DialogInterface?, p1: Int) {
//            }
//        })
//
//        // build the dialog and show it
//        var dialog: AlertDialog = builder.create()
//        dialog.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun amountDialog() {
        var builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Enter the amount for the expense")
        _editIncome = EditText(this)
        _editIncome!!.inputType = InputType.TYPE_CLASS_NUMBER
        _editIncome!!.hint = "Enter amount here"
        builder.setView(_editIncome)

        builder.setPositiveButton(
            "Accept"
        ) { p0, p1 ->

            try{
                var exp: ExpenseItem=ExpenseItem(null, _editExpense!!.text.toString(), _editIncome!!.text.toString(), sheetItem.id)
                _total_expense += _editIncome!!.text.toString().toInt()
                addData(exp)
                _rl_arraylist.add(exp)
                recycler_adapter?.notifyDataSetChanged()

                setSurplusValue()
            }
            catch (e: Exception){
                Log.i("mine", e.toString())
            }

        }

        builder.setNegativeButton("Cancel") { p0, p1 ->

        }

        // build the dialog and show it
        var dialog: AlertDialog = builder.create()
        dialog.show()
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
                    sheetItem._income=_income!!.text.toString().toInt()
                    if(addIncome(sheetItem._income)){
                        _incomeView?.setText(_income!!.text.toString())

                    }
                    else{
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

            R.id.editIncome -> {
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun addData( expenseItem: ExpenseItem) {
        val row1: ContentValues = ContentValues().apply {
        put("EXPENSE_NAME", expenseItem._expenses_text)
        put("AMOUNT", expenseItem._expenses_amount)
        put("FK_KEY", sheetItem.id)
    }
    db.insert("expense", null, row1)
   }


    private fun addIncome(income: Int): Boolean {
        val contentValues = ContentValues()
        contentValues.put("INCOME", income)
        db.update("sheet", contentValues, "ID = ?", arrayOf(sheetItem.id.toString()))
        return true
    }

    private fun surplusValue(): Int{
        return sheetItem._income - _total_expense
    }


    private fun setSurplusValue(){
        if(surplusValue()>1){
            _surplus_deficit_text?.setText("Surplus")
            _surplus_deficit_value?.setText(surplusValue().toString())
        }else{
            _surplus_deficit_text?.setText("Deficit")
            _surplus_deficit_value?.setText(surplusValue().toString())
        }
    }


    private fun snackShow(message: String){
        Snackbar.make(_recyclerview, message, Snackbar.LENGTH_LONG).show()
    }




}


