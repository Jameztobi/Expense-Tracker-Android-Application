package com.example.assignment_1.model

class StatItem(var regular: Int, var irregular: Int, var _status: String,
               var _prevExpense: Int, var _prevStatus: String

) {
    private var _regular: Int = regular
    private var _irregular: Int = irregular
    private var status: String = _status
    private var prevExpense: Int = _prevExpense
    private var prevStatus: String = _prevStatus


}