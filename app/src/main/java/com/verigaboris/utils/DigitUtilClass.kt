package com.verigaboris.utils

fun Int.countDigits(): Int {
    var count = 0
    var num = this
    while (num != 0) {
        num /= 10
        ++count
    }
    return count
}