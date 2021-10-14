package com.example.assignment_1

import android.widget.EditText

class RecyclerItem(var _expenses_text: String, var _expenses_amount: String) {
    // private fields of the class
    private var expense_text: String = _expenses_text
    private var expense_amount: String = _expenses_amount
}