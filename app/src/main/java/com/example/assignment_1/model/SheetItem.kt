package com.example.assignment_1.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SheetItem(var id: Int,
                var _period: String,
                var _income: Int,
                var _regular: Int,
                var _irregular: Int,
                var _total_expense: Int
                        ): Parcelable{
    // private fields of the class
    private var sheet_id: Int = 0
    private var period: String = _period
    private var income: Int = _income
    private var surplus: Int = 0
    private var regular: Int = _regular
    private var irregular: Int = _irregular
    private var total_expense: Int = _total_expense


}
