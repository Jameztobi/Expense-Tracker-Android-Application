package com.example.assignment_1

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ControllerActivity : AppCompatActivity() {
    private lateinit var _recyclerview: RecyclerView
    private var _rl_arraylist : ArrayList<RecyclerItem> = ArrayList<RecyclerItem>()
    private var _add_expenses_button: Button? = null
    private var _editExpense: EditText? = null
    private var _editIncome: EditText? = null
    private var recycler_adapter: RecyclerAdapter? = null
    private var _income: EditText? = null
    private var _incomeView: TextView? = null
    private var _surplus_deficit_value: TextView? = null
    private var _surplus_deficit_text: TextView? = null
    private var _total_value = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controller)

        _recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        _add_expenses_button = findViewById<Button>(R.id.add_expenses)
        _incomeView = findViewById<TextView>(R.id.incomeValue)
        _surplus_deficit_value = findViewById<TextView>(R.id.surplus_deficit_value)
        _surplus_deficit_text = findViewById<TextView>(R.id.surplus_deficit)

        _recyclerview.layoutManager = LinearLayoutManager(this)
        recycler_adapter = RecyclerAdapter(this, _rl_arraylist)
        _recyclerview.adapter = recycler_adapter

        //add a listener to the input button that will trigger an input dialog
        _add_expenses_button?.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?){
                //trigger the alert dialog
                expenseDialog()
            }
        })



    }

    private fun expenseDialog() {
        var builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Please enter a new expense")
        _editExpense = EditText(this)
        _editExpense!!.inputType = InputType.TYPE_CLASS_TEXT
        _editExpense!!.hint = "Enter text here"
        builder.setView(_editExpense)


        builder.setPositiveButton("Accept") { p0, p1 ->
            amountDialog()
        }

        builder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
            }
        })

        // build the dialog and show it
        var dialog: AlertDialog = builder.create()
        dialog.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun amountDialog() {
        var builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Enter the amount for the expense")
        _editIncome = EditText(this)
        _editIncome!!.inputType = InputType.TYPE_CLASS_NUMBER
        _editIncome!!.hint = "Enter amount here"
        builder.setView(_editIncome)

        builder.setPositiveButton("Accept"
        ) { p0, p1 ->
            _rl_arraylist.add(RecyclerItem(_editExpense!!.text.toString(), _editIncome!!.text.toString()))
            recycler_adapter?.notifyDataSetChanged()


        }

        builder.setNegativeButton("Cancel") { p0, p1 ->

        }

        // build the dialog and show it
        var dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.addIncome -> {
//                var builder: AlertDialog.Builder = AlertDialog.Builder(this)
//                builder.setTitle("Enter the monthly Income")
//                _income = EditText(this)
//                _income!!.inputType = InputType.TYPE_CLASS_NUMBER
//                _income!!.hint = "Please your monthly income"
//                builder.setView(_income)
//
//                builder.setPositiveButton(
//                    "Accept"
//                ) { p0, p1 ->
//                    _incomeView?.setText(_income!!.text.toString())
//
//
//                }
//
//                builder.setNegativeButton("Cancel") { p0, p1 ->
//
//                }
//
//                // build the dialog and show it
//                var dialog: AlertDialog = builder.create()
//                dialog.show()
//                return true
//            }
//
//            R.id.first -> {
//                return true
//            }
//
//        }
//        return super.onOptionsItemSelected(item)
//    }

}