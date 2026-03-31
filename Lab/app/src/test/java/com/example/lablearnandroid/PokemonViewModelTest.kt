package com.example.lablearnandroid

import com.example.lablearnandroid.ui.utils.PokedexResponse
import com.example.lablearnandroid.ui.utils.PokemonEntry
import com.example.lablearnandroid.ui.utils.PokemonNetwork
import com.example.lablearnandroid.ui.utils.PokemonSpecies
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

/**
 * PokemonViewModelTest: การทดสอบที่ซับซ้อนขึ้นโดยการ Mock API
 * เราไม่อยากยิง API จริงๆ ขณะทดสอบ (เพราะจะทำให้การทดสอบช้าและต้องพึ่งพา Internet)
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PokemonViewModelTest {

    private lateinit var viewModel: PokemonViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // บอก MockK ให้ "จับตาดู" Singleton Object นี้
        mockkObject(PokemonNetwork)
        
        // สร้าง ViewModel (ตัวแปร init จะเรียก fetchPokemon() อัตโนมัติ)
        // เพื่อความแม่นยำ เราจะ Mock API ก่อนสร้าง ViewModel
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        // ล้างสถานะ Mock เมื่อจบการทดสอบแต่ละเคส
        unmockkAll()
    }

    @Test
    fun testFetchPokemon_Success_UpdatesList() = runTest {
        // --- 1. เตรียมข้อมูลจำลอง (Mock Data) ---
        val mockData = PokedexResponse(
            pokemon_entries = listOf(
                PokemonEntry(1, PokemonSpecies("Bulbasaur", "url1")),
                PokemonEntry(4, PokemonSpecies("Charmander", "url4"))
            )
        )

        // --- 2. ตั้งค่าการ Mock (When) ---
        // เมื่อมีการเรียก PokemonNetwork.api.getKantoPokedex ให้คืนค่า mockData ที่เราสร้าง
        coEvery { PokemonNetwork.api.getKantoPokedex() } returns mockData

        // --- 3. ดำเนินการ (Action) ---
        viewModel = PokemonViewModel() // init จะเรียก fetchPokemon()
        viewModel.fetchPokemon()

        // --- 4. ตรวจสอบ (Verify / Assert) ---
        assertEquals("Pokemon list size should match the mock data", 2, viewModel.pokemonList.value.size)
        assertEquals("First pokemon should be Bulbasaur", "Bulbasaur", viewModel.pokemonList.value[0].pokemon_species.name)
    }

    @Test
    fun testFetchPokemon_NetworkError_HandlesException() = runTest {
        // --- 1. เตรียมกรณี Error (Fail Case - IOException) ---
        // จำลองสถานการณ์ Internet ล่ม หรือ Server ปิด
        coEvery { PokemonNetwork.api.getKantoPokedex() } throws IOException("Network Failure")

        // --- 2. ดำเนินการ ---
        viewModel = PokemonViewModel()
        viewModel.fetchPokemon()

        // --- 3. ตรวจสอบ ---
        // ใน ViewModel ปัจจุบัน เมื่อเกิด Error จะไม่ทำอะไรหรือแค่ Log
        // เราตรวจสอบว่า list ยังเป็นค่าว่างเหมือนเดิม (ไม่ Crash)
        assertTrue("If Network error occurs, pokemon list should remain empty", 
            viewModel.pokemonList.value.isEmpty())
    }

    @Test
    fun testFetchPokemon_EmptyApi_ReturnsEmptyList() = runTest {
        // --- กรณีที่ API ทำงานปกติ แต่ไม่มีข้อมูลส่งกลับมา ---
        val emptyMockData = PokedexResponse(pokemon_entries = emptyList())
        coEvery { PokemonNetwork.api.getKantoPokedex() } returns emptyMockData

        viewModel = PokemonViewModel()
        viewModel.fetchPokemon()

        assertTrue("Empty response from API should result in empty list state",
            viewModel.pokemonList.value.isEmpty())
    }
}
