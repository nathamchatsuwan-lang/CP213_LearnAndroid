package com.example.lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lablearnandroid.ui.theme.LabLearnAndroidTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// 1. ViewModel สำหรับจัดการข้อมูลรายชื่อและสถานะการโหลด
class ContactViewModel : ViewModel() {
    private val _contacts = mutableStateListOf<String>()
    val contacts: List<String> = _contacts

    var isLoading by mutableStateOf(false)
        private set

    init {
        // Mock ข้อมูลเริ่มต้น A-Z
        generateMockData()
    }

    private fun generateMockData() {
        val initialData = ('A'..'Z').map { char -> "$char Contact ${ (1..100).random() }" }.sorted()
        _contacts.addAll(initialData)
    }

    fun loadMore() {
        if (isLoading) return
        
        isLoading = true
        // ใช้ viewModelScope เพื่อจัดการ Coroutine ตาม lifecycle ของ ViewModel
        viewModelScope.launch {
            delay(2000) // หน่วงเวลา 2 วินาที
            val newData = (1..5).map { "New Friend ${(_contacts.size + it)}" }
            _contacts.addAll(newData)
            isLoading = false
        }
    }
}

class Part2Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ContactListScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactListScreen(
    modifier: Modifier = Modifier,
    viewModel: ContactViewModel = viewModel()
) {
    val contacts = viewModel.contacts
    val isLoading = viewModel.isLoading
    val listState = rememberLazyListState()

    // 2. ตรวจสอบการ Scroll เพื่อทำ Pagination ให้แม่นยำขึ้นโดยใช้ totalItemsCount
    val shouldLoadMore = remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            
            lastVisibleItem != null && lastVisibleItem.index >= totalItems - 1
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && !isLoading) {
            viewModel.loadMore()
        }
    }

    // จัดกลุ่มข้อมูลตามตัวอักษรแรกสำหรับ Sticky Header
    val grouped = contacts.groupBy { it.first().uppercaseChar() }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize()
    ) {
        grouped.forEach { (initial, names) ->
            // 3. ใช้ stickyHeader แสดงตัวอักษรนำหน้า
            stickyHeader {
                Text(
                    text = initial.toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            items(names) { name ->
                ContactItem(name)
            }
        }

        // 4. แสดง CircularProgressIndicator เมื่อกำลังโหลดรายชื่อเพิ่ม
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun ContactItem(name: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}