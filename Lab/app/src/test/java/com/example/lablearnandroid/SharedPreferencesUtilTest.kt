package com.example.lablearnandroid

import android.content.Context
import android.content.SharedPreferences
import com.example.lablearnandroid.ui.utils.SharedPreferencesUtil
import io.mockk.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * SharedPreferencesUtilTest: การทดสอบ Utility ที่ขึ้นกับ Android Framework (Context)
 * ในที่นี้เราจะ Mock ตัว Context และ SharedPreferences เพื่อให้รันบนคอมพิวเตอร์ได้โดยไม่ต้องพึ่ง Emulator
 */
class SharedPreferencesUtilTest {

    private val context = mockk<Context>()
    private val sharedPrefs = mockk<SharedPreferences>()
    private val editor = mockk<SharedPreferences.Editor>()

    @Before
    fun setup() {
        // เตรียมการ Mock SharedPreferences
        every { context.getSharedPreferences(any(), any()) } returns sharedPrefs
        every { sharedPrefs.edit() } returns editor
        
        // Mock chain สำหรับการจัดเก็บข้อมูล (เพราะ SharedPreferences.Editor ทำงานแบบ Chain)
        every { editor.putString(any(), any()) } returns editor
        every { editor.putInt(any(), any()) } returns editor
        every { editor.putBoolean(any(), any()) } returns editor
        every { editor.remove(any()) } returns editor
        every { editor.clear() } returns editor
        every { editor.apply() } just Runs // .apply() ไม่คืนค่าอะไร (Unit)

        // เริ่มต้นการใช้งาน (เรียก init)
        SharedPreferencesUtil.init(context)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testSaveString_CallsCorrectMethods() {
        val key = "username"
        val value = "JohnDoe"

        // Action
        SharedPreferencesUtil.saveString(key, value)

        // Verify: ตรวจสอบว่ามีคำสั่งใส่ข้อมูลลงใน editor จริงหรือไม่ และมีการ apply หรือไม่
        verify { editor.putString(key, value) }
        verify { editor.apply() }
    }

    @Test
    fun testGetString_ReturnsCorrectValue() {
        val key = "username"
        val expectedValue = "JohnDoe"

        // เตรียมข้อมูลดักไว้
        every { sharedPrefs.getString(key, any()) } returns expectedValue

        // Action
        val result = SharedPreferencesUtil.getString(key)

        // Assert
        assertEquals("Should return the value from SharedPreferences", expectedValue, result)
    }

    @Test
    fun testGetBoolean_WithDefaultValue_IfNull() {
        val key = "isLoggedIn"
        
        // จำลองว่าไม่มีข้อมูลในเครื่อง (คืนค่า default ที่ส่งเข้าไป)
        every { sharedPrefs.getBoolean(key, false) } returns false

        // Action
        val result = SharedPreferencesUtil.getBoolean(key, false)

        // Assert
        assertTrue("Should return false as default if not found", !result)
    }

    @Test
    fun testClearAll_CallsClearAndApply() {
        // Action
        SharedPreferencesUtil.clearAll()

        // Verify: การล้างข้อมูลทั้งหมดต้องเรียก .clear() และตามด้วย .apply()
        verify { editor.clear() }
        verify { editor.apply() }
    }
}
