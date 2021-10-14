package com.example.assignment_1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SheetAdapter(val context: Context, var sheet_arraylist: ArrayList<SheetItem>) :
    RecyclerView.Adapter<SheetAdapter.ItemHolder>() {
        //private field of the class
        private val _context: Context = context
        private var _sheet_arraylist: ArrayList<SheetItem> = sheet_arraylist

    // nested class that will implement a view holder for an item in the list
    class ItemHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener{
        // private fields of the class
        private var _view: View = v
        private var _sheet_item: SheetItem ? = null
        lateinit var _name: TextView
        lateinit var _period: TextView

        //called to initialise the object
        init {
            // pull references from the layout for the text views
            _name = _view.findViewById<TextView>(R.id.newSheetItem)
            _period = _view.findViewById<TextView>(R.id.period)

            // set a listener for clicks on this view holder
            _view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            TODO("Not yet implemented")
        }
    }


    // returns the number of items that are in this RecyclerAdapter
    override fun getItemCount(): Int {
        return _sheet_arraylist.size
    }

    // function that will create a viewholder for the recycler adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        // get access to the layout inflator and inflate a layout for one of our recycler view items
        return ItemHolder(
            LayoutInflater.from(_context).inflate(
                R.layout.recycler_item2, parent,
                false
            )
        )
    }

    // function that will bind an item in our arraylist to a view holder so it can be displayed
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        // get the item at the current position
        val item: SheetItem = _sheet_arraylist.get(position)

        // set the number and text on the view holder
        holder._name.setText(item._sheet_name)
        holder._period.setText(item._period)
    }


}






