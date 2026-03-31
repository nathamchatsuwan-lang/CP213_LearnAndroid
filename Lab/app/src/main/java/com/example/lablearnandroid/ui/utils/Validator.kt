package com.example.lablearnandroid.ui.utils

/**
 * Validator: คลาสสำหรับจำลอง Business Logic เพื่อใช้ในการทำ Unit Test
 * เน้นการตรวจสอบข้อมูลเบื้องต้นที่มีเงื่อนไขชัดเจน (Edge Cases)
 */
object Validator {

    /**
     * ตรวจสอบ Email
     * เงื่อนไข:
     * 1. ไม่เป็นค่าว่าง
     * 2. ต้องมีเครื่องหมาย @
     * 3. ต้องมี domain (เช่น .com, .net)
     */
    fun validateEmail(email: String): Boolean {
        if (email.isBlank()) return false
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }

    /**
     * ตรวจสอบ Password
     * เงื่อนไข:
     * 1. ความยาวอย่างน้อย 8 ตัวอักษร
     * 2. ต้องมีตัวเลขอย่างน้อย 1 ตัว
     * 3. ต้องมีตัวอักษรพิมพ์ใหญ่กอย่างน้อย 1 ตัว
     */
    fun validatePassword(password: String): Boolean {
        if (password.length < 8) return false
        val hasNumber = password.any { it.isDigit() }
        val hasUpperCase = password.any { it.isUpperCase() }
        return hasNumber && hasUpperCase
    }

    /**
     * คำนวณภาษี (ตัวอย่างการคำนวณตัวเลข)
     * เงื่อนไข:
     * 1. รายได้ < 0 -> โยน Exception (Error Case)
     * 2. รายได้ 0 - 150,000 -> 0%
     * 3. รายได้ > 150,000 -> 5%
     */
    fun calculateTax(income: Double): Double {
        if (income < 0) throw IllegalArgumentException("Income cannot be negative")
        return if (income <= 150000) {
            0.0
        } else {
            (income - 150000) * 0.05
        }
    }
}
