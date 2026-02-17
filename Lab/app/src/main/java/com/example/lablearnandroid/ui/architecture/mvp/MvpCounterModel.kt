package com.example.lablearnandroid.ui.architecture.mvp

class MvpCounterModel {
    private var count = 0

    fun getCount(): Int {
        return count
    }

    fun incrementCounter() {
        count++
    }
}
