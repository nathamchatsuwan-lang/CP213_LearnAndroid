package com.example.lablearnandroid.ui.architecture.mvvm

class MvvmCounterModel {
    private var count = 0

    fun getCount(): Int {
        return count
    }

    fun incrementCounter() {
        count++
    }
}
