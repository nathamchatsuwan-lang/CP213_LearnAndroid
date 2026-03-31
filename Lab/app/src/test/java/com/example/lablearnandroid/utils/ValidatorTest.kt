package com.example.lablearnandroid.ui.utils

import com.example.lablearnandroid.ui.utils.Validator
import org.junit.Assert.*
import org.junit.Test

/**
 * ValidatorTest: ตัวอย่างการเขียน Unit Test สำหรับ logic ทั่วไป
 * ประกอบด้วยกรณีที่คาดหวังให้ผ่าน (Pass) และกรณีที่ตั้งใจให้ไม่ผ่าน (Fail/Edge Case)
 */
class ValidatorTest {

    // --- การทดสอบ Email ---

    @Test
    fun testEmail_Valid_ReturnsTrue() {
        // Case: รูปแบบถูกต้อง (Pass)
        val result = Validator.validateEmail("test@example.com")
        assertTrue("Email correctly formatted should return true", result)
    }

    @Test
    fun testEmail_InvalidFormat_ReturnsFalse() {
        // Case: รูปแบบผิด (Fail/Invalid)
        assertFalse("Missing @ should return false", Validator.validateEmail("testexample.com"))
        assertFalse("Missing domain should return false", Validator.validateEmail("test@example"))
        assertFalse("Invalid characters should return false", Validator.validateEmail("test@!#$.com"))
    }

    @Test
    fun testEmail_Empty_ReturnsFalse() {
        // Case: ค่าว่าง (Fail/Edge Case)
        assertFalse("Empty string should return false", Validator.validateEmail(""))
        assertFalse("Blank string should return false", Validator.validateEmail("   "))
    }


    // --- การทดสอบ Password ---

    @Test
    fun testPassword_Strong_ReturnsTrue() {
        // Case: ครบเงื่อนไข (Pass)
        val result = Validator.validatePassword("Pass1234")
        assertTrue("Password with 8+ chars, number, and uppercase should return true", result)
    }

    @Test
    fun testPassword_TooShort_ReturnsFalse() {
        // Case: สั้นไป (Fail)
        val result = Validator.validatePassword("P123")
        assertFalse("Password fewer than 8 chars should return false", result)
    }

    @Test
    fun testPassword_NoNumber_ReturnsFalse() {
        // Case: ไม่มีตัวเลข (Fail)
        val result = Validator.validatePassword("PassCodeOnly")
        assertFalse("Password without a number should return false", result)
    }

    @Test
    fun testPassword_NoUpperCase_ReturnsFalse() {
        // Case: ไม่มีตัวพิมพ์ใหญ่ (Fail)
        val result = Validator.validatePassword("password123")
        assertFalse("Password without an uppercase letter should return false", result)
    }


    // --- การทดสอบ การคำนวณภาษี (Numerical Logic) ---

    @Test
    fun testCalculateTax_NoTax_ReturnsZero() {
        // Case: รายได้ไม่ถึงเกณฑ์ (Pass - Low income)
        val tax = Validator.calculateTax(100000.0)
        assertEquals(0.0, tax, 0.001) // 0.001 คือค่าความคลาดเคลื่อนที่ยอมรับได้ (delta)
    }

    @Test
    fun testCalculateTax_UnderFivePercent_ReturnsCorrectValue() {
        // Case: รายได้เกินเกณฑ์ (Pass - High income)
        // รายได้ 200,000 บาท -> (200,000 - 150,000) * 0.05 = 2,500
        val tax = Validator.calculateTax(200000.0)
        assertEquals(2500.0, tax, 0.001)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCalculateTax_NegativeIncome_ThrowsException() {
        // Case: ข้อมูลติดลบ (Fail - Expected error)
        // เราคาดหวังให้ฟังก์ชันโยน Exception ออกมา
        Validator.calculateTax(-5000.0)
    }
}
