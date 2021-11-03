package com.example.assignment_1.adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment_1.R
import com.example.assignment_1.database.ExpenseTrackerDB
import com.example.assignment_1.model.SheetItem
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.widget.ArrayAdapter as ArrayAdapter

class SheetAdapter(
    var c: Context,
    var _sheetList: ArrayList<SheetItem>,
    val itemClicked: (sheetItem: SheetItem) -> Unit
) :
    RecyclerView.Adapter<SheetAdapter.ItemHolder>() {
    private val context: Context = c
    private var sheetList: ArrayList<SheetItem> = _sheetList
    private var _expenseDb: ExpenseTrackerDB = ExpenseTrackerDB(c)


    class ItemHolder(private val v: View, val itemClicked: (sheetItem: SheetItem) -> Unit) :
        RecyclerView.ViewHolder(v) {
        var _view: View = v
        private var period: TextView = _view.findViewById<TextView>(R.id.period)
        var btnDelete: ImageButton = _view.findViewById<ImageButton>(R.id.btndelete_expenseSheet)
        var btnEdit: ImageButton = _view.findViewById<ImageButton>(R.id.btnedit_expenseSheet)

        fun bind(sheetItem: SheetItem) {
            period.text = sheetItem._period
            _view.setOnClickListener {
                itemClicked(sheetItem)
            }

        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val inflater = LayoutInflater.from(context)
        var v = inflater.inflate(R.layout.recycler_item2, parent, false)
        return ItemHolder(v, itemClicked)

    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item: SheetItem = sheetList[position]
        var id: Int = item.id
        holder.bind(item)


        //add a listener to the input button that will trigger an input dialog
        holder.btnDelete.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                //trigger the alert dialog
                var builder: AlertDialog.Builder = AlertDialog.Builder(c)
                builder.setTitle("Confirm Delete Item")
                builder.setMessage("Sure you want to delete the item")
                builder.setIcon(android.R.drawable.ic_menu_delete)
                builder.setCancelable(false)


                builder.setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        var result: Int = _expenseDb.deleteRow(id.toString())

                        if (result > 0) {
                            Toast.makeText(c, "Deleted", Toast.LENGTH_SHORT).show()
                            _sheetList.remove(item)
                            notifyDataSetChanged()
                        } else {
                            Toast.makeText(c, "Failed", Toast.LENGTH_SHORT).show()
                        }

                    }
                })


                builder.setNegativeButton("No", null)

                builder.show()
                Log.i("successmessage", "This is true")
            }
        })

        holder.btnEdit.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var dialog = Dialog(c)
                dialog.setContentView(R.layout.update_sheet_item)
                var month = dialog.findViewById<Spinner>(R.id.update_month)
                var year = dialog.findViewById<Spinner>(R.id.update_year)
                var send = dialog.findViewById<Button>(R.id.updateSheet)
                var cancel = dialog.findViewById<Button>(R.id.cancel_updateSheet)

                var period: List<String> = item._period.split(" ", "-")
                var period_year: String = period[1]
                var period_month: String = period[0]
                var yearsList: ArrayList<String> = getYears()


                year.adapter = ArrayAdapter<String>(
                    context,
                    android.R.layout.simple_list_item_1,
                    yearsList

                )


                var index = getYearIndex(period_year.toInt())
                year.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }
                })

                getMonthDropDownList(month, c, period_month)


                var layoutParam = WindowManager.LayoutParams()
                layoutParam.copyFrom(dialog.window?.attributes)
                layoutParam.width = WindowManager.LayoutParams.MATCH_PARENT
                layoutParam.height = WindowManager.LayoutParams.WRAP_CONTENT
                dialog.window?.attributes = layoutParam


                send.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(p0: View?) {
                        var newPeriod =
                            month.selectedItem.toString() + "-" + year.selectedItem.toString()
                        var result: Int = _expenseDb.updateData(
                            id,
                            newPeriod
                        )

                        var newItem: SheetItem =
                            SheetItem(item.id, newPeriod, item._income, 0, 0, 0)

                        if (result > 0) {
                            _sheetList.remove(item)
                            _sheetList.add(newItem)
                            notifyDataSetChanged()
                            dialog.dismiss()
                            Toast.makeText(c, "Updated", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(c, "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                })


                cancel.setOnClickListener {
                    dialog.dismiss()

                }

                dialog.show()


            }
        })


    }

    override fun getItemCount(): Int {
        return sheetList.size
    }

    private fun getYears(): ArrayList<String> {
        var thisYear: Int = Calendar.getInstance().get(Calendar.YEAR)
        var yearList: ArrayList<String> = ArrayList()
        for (i in 1960..thisYear) {
            yearList.add(i.toString())
        }
        return yearList
    }

    private fun getYearIndex(year: Int): Int {
        for (i in getYears().indices) {
            if (year == getYears()[i].toInt()) {
                return i
            }
        }
        return 60
    }

    private fun getMonthDropDownList(month: Spinner, c: Context, period_month: String) {
        var monthList: Array<String> = c.resources.getStringArray(R.array.month_string)

        var adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this.c,
            R.array.month_string,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        month?.adapter = adapter


        month?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        })


    }


}







