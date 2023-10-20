package com.example.tipapplication.util

fun CalculateTotalTip(totalBill: Double, tippercentage: Int): Double {
    return if(totalBill>1  && totalBill.toString().isNotEmpty())
        (totalBill*tippercentage)/100
    else 0.0
}

fun calculateTotalPerson(totalBill: Double,splitBy:Int ,tippercentage: Int) : Double{
 val bill= CalculateTotalTip(totalBill =totalBill,tippercentage=tippercentage) +totalBill
    return (bill /splitBy)
}