package com.example.lablearnandroid

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * SensorViewModelTest: ตัวอย่างการทดสอบ ViewModel ที่ใช้ StateFlow
 * จำเป็นต้องมีการตั้งค่า Main Dispatcher สำหรับการทำงานบนหน่วยความจำขณะทดสอบ
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SensorViewModelTest {

    private lateinit var viewModel: SensorViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        // ตั้งค่า Dispatcher หลักให้เป็น TestDispatcher เพื่อให้ทำงานร่วมกับ Coroutine ได้
        Dispatchers.setMain(testDispatcher)
        viewModel = SensorViewModel()
    }

    @After
    fun tearDown() {
        // รีเซ็ต Dispatcher หลังเสร็จสิ้นการทดสอบ
        Dispatchers.resetMain()
    }

    @Test
    fun testUpdateAccelerometer_UpdatesStateFlow() = runTest {
        // 1. กำหนดค่าที่ต้องการทดสอบ
        val testValues = floatArrayOf(1.2f, 3.4f, 5.6f)

        // 2. เรียกฟังก์ชันใน ViewModel
        viewModel.updateAccelerometer(testValues)

        // 3. ตรวจสอบว่า StateFlow เปลี่ยนตามจริงหรือไม่
        assertArrayEquals("Accelerometer data state should match with updated value",
            testValues, viewModel.accelerometerData.value, 0.0f)
    }

    @Test
    fun testUpdateLocation_UpdatesStateFlow() = runTest {
        // 1. กำหนดค่าที่ต้องการทดสอบ
        val lat = 13.7563
        val lng = 100.5018

        // 2. เรียกฟังก์ชันใน ViewModel
        viewModel.updateLocation(lat, lng)

        // 3. ตรวจสอบว่า StateFlow เปลี่ยนตามจริงหรือไม่
        val expected = Pair(lat, lng)
        assertEquals("Location data state should match with updated value",
            expected, viewModel.locationData.value)
    }

    @Test
    fun testInitialState_IsDefault() {
        // ทดสอบค่าเริ่มต้น (Initial State)
        val defaultAccel = floatArrayOf(0f, 0f, 0f)
        val defaultLoc = Pair(0.0, 0.0)

        assertArrayEquals("Initial Accelerometer should be all Zeros",
            defaultAccel, viewModel.accelerometerData.value, 0.0f)
        assertEquals("Initial Location should be 0.0, 0.0",
            defaultLoc, viewModel.locationData.value)
    }
}
