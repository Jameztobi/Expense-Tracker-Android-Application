package com.example.assignment_1.model

class ExpenseItem(var _id: Int?, var _expenses_text: String, var _expenses_amount: String, var _fk_id: Int,
var _date: String, var _status: String)
{
    // private fields of the class
    private var id: Int? = _id
    private var expense_text: String = _expenses_text
    private var expense_amount: String = _expenses_amount
    private var fk_id: Int = _fk_id
    private var date: String = _date
    private var status: String = _status


}