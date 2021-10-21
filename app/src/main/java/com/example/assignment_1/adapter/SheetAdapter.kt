package com.example.assignment_1.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment_1.R
import com.example.assignment_1.model.SheetItem

class SheetAdapter(var c: Context, var _sheetList: ArrayList<SheetItem>, val itemClicked: (sheetItem: SheetItem) -> Unit) :
    RecyclerView.Adapter<SheetAdapter.ItemHolder>()
{
    private val context:Context = c
    private var sheetList: ArrayList<SheetItem> = _sheetList

    class ItemHolder(private val v: View, val itemClicked: (sheetItem: SheetItem) -> Unit):RecyclerView.ViewHolder(v){
        private var _view: View = v
        private var name: TextView = _view.findViewById<TextView>(R.id.newSheetItem)
        private var period: TextView = _view.findViewById<TextView>(R.id.period)

        fun bind(sheetItem: SheetItem){
            name.text = sheetItem._sheet_name
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
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return sheetList.size
    }
}







