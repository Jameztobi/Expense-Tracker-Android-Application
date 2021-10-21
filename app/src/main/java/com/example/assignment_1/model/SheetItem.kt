package com.example.assignment_1.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SheetItem(var id: Int, var _sheet_name: String, var _period: String, var _income: Int): Parcelable{
    // private fields of the class
    private var sheet_id: Int = 0
    private var sheet_name: String = _sheet_name
    private var period: String = _period
    private var income: Int = _income
    private var surplus: Int = 0

}

