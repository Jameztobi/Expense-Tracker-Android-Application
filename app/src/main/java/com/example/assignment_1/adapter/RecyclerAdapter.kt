package com.example.assignment_1.adapter

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment_1.R
import com.example.assignment_1.database.ExpenseTrackerDB
import com.example.assignment_1.model.ExpenseItem
import com.example.assignment_1.model.SheetItem
import com.example.assignment_1.model.StatItem
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.collections.ArrayList

class RecyclerAdapter(val context: Context, private var ri_arraylist: ArrayList<ExpenseItem>, var clickListener: ClickListener) :
    RecyclerView.Adapter<RecyclerAdapter.ItemHolder>() {
    //private fields of the class
    private val _context: Context = context
    private var _ri_arraylist: ArrayList<ExpenseItem> = ri_arraylist
    private var _expenseDb: ExpenseTrackerDB = ExpenseTrackerDB(_context)
    private var date_value: String = ""


    // nested class that will implement a view holder for an item in the list
    class ItemHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

        // private fields of the class
        private var _view: View = v
        private var _recycler_item: ExpenseItem? = null
        lateinit var _tv_expenses: TextView
        lateinit var _tv_amount: TextView
        lateinit var _tv_status: TextView
        lateinit var btnDelete: ImageButton
        lateinit var btnEdit: ImageButton
        lateinit var _surplus_deficit_value : TextView
        lateinit  var totalRegularValue : TextView
        lateinit var totalIrregularValue : TextView
        lateinit var incomeValue: TextView


        // called to initialise the object
        init {
            // pull references from the layout for the text views
            _tv_expenses = _view.findViewById<TextView>(R.id.expense)
            _tv_amount = _view.findViewById<TextView>(R.id.amount)
            _tv_status = _view.findViewById<TextView>(R.id.type_expense)
            btnDelete = _view.findViewById<ImageButton>(R.id.btn_delete)
            btnEdit = _view.findViewById<ImageButton>(R.id.btn_edit)



            // set a listener for clicks on this view holder
            _view.setOnClickListener(this)

        }

        override fun onClick(p0: View?) {
            TODO("Not yet implemented")

        }

    }

    // returns the number of items that are in this RecyclerAdapter
    override fun getItemCount(): Int {
        return _ri_arraylist.size
    }

    // function that will create a viewholder for the recycler adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        // get access to the layout inflator and inflate a layout for one of our recycler view items
        return ItemHolder(
            LayoutInflater.from(_context).inflate(
                R.layout.recycler_item, parent,
                false
            )
        )
    }

    // function that will bind an item in our arraylist to a view holder so it can be displayed
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        // get the item at the current position
        val item: ExpenseItem = _ri_arraylist.get(position)
        var id = item._id

        // set the number and text on the view holder
        holder._tv_expenses.setText(item._expenses_text)
        holder._tv_amount.setText(item._expenses_amount)
        holder._tv_status.setText(item._status)

        //add a listener to the input button that will trigger an input dialog
        holder.btnDelete.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                //trigger the alert dialog
                var builder: AlertDialog.Builder = AlertDialog.Builder(_context)
                builder.setTitle("Confirm Delete Item")
                builder.setMessage("Sure you want to delete the item")
                builder.setIcon(android.R.drawable.ic_menu_delete)
                builder.setCancelable(false)


                builder.setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        var result: Int = _expenseDb.deleteExpenseRow(id.toString())

                        if (result > 0) {
                            Toast.makeText(_context, "Deleted", Toast.LENGTH_SHORT).show()
                            _ri_arraylist.remove(item)
                            clickListener.onDelete(item)
                            notifyDataSetChanged()

                        } else {
                            Toast.makeText(_context, "Failed", Toast.LENGTH_SHORT).show()
                        }

                    }
                })


                builder.setNegativeButton("No", null)

                builder.show()
                Log.i("successmessage", "This is true")
            }
        })

        holder.btnEdit.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                expenseDialog(item)
            }

        })



    }

    private fun expenseDialog(item: ExpenseItem) {
        var dialog = Dialog(_context)
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
        dataPopUpSelectUp(dialog, item._date)
        var send = dialog.findViewById<Button>(R.id.sendExp)
        var cancel = dialog.findViewById<Button>(R.id.cancelExp)

        expenseName.setText(item._expenses_text)
        expenseIncome.setText(item._expenses_amount)
        if(item._status.equals("REGULAR")){
            regular.isChecked
        }
        else{
            irregular.isChecked
        }

        send.setOnClickListener {
            var name = expenseName.text.toString()
            var amount = expenseIncome.text.toString()
            var date = date_value

            var id = radioGroup.checkedRadioButtonId
            var status : String? = null
            var updatedSheet: StatItem? = null
            if(id == R.id.rb1){
                status = regular.text.toString()
                updatedSheet= StatItem(amount.toInt(), 0, status, item._expenses_amount.toInt(), item._status)

            }
            else{
                status = irregular.text.toString()
                updatedSheet= StatItem(0, amount.toInt(), status, item._expenses_amount.toInt(), item._status)

            }

            var exp = ExpenseItem(item._id, name, amount, item._fk_id, date, status)
            //_total_expense += amount.toInt()
            //addData(exp)
            //updateTotalExpense(retrieveSheet()!!._total_expense + _total_expense)
            _ri_arraylist.remove(item)
             clickListener.onUpdate(updatedSheet, exp)
            _ri_arraylist.add(exp)
            notifyDataSetChanged()
            snackShow("Expenses Added")
            //setStatusValue()
            //setSurplusValue()
            dialog.dismiss()
        }
        cancel.setOnClickListener {
            snackShow("You have canceled")
            dialog.dismiss()

        }

        dialog.show()

    }

    private fun dataPopUpSelectUp(dialog: Dialog, _date: String) {
        var calender: Calendar = Calendar.getInstance()
        var day = calender[Calendar.DAY_OF_MONTH]
        var month = calender[Calendar.MONTH]
        var year = calender[Calendar.YEAR]
        var dateValue = dialog.findViewById<EditText>(R.id.calender)
         dateValue.setText(_date)
        dateValue?.inputType = InputType.TYPE_NULL
        dateValue?.setOnClickListener {
            var dpd = DatePickerDialog(
                _context,
                DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                    //set to textView
                    dateValue!!.setText("" + mDay + "/" + mMonth + "/" + mYear)
                    date_value = dateValue.text.toString()

                },
                year,
                month,
                day
            )
            dpd.show()
        }

    }

    private fun snackShow(message: String) {
        Toast.makeText(_context, message, Toast.LENGTH_SHORT).show()
    }






    interface ClickListener {
        fun onDelete(item: ExpenseItem)
        fun onUpdate(item1: StatItem?, item2: ExpenseItem)
    }


    }